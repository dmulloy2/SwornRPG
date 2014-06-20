package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class CmdLeaderboard extends SwornRPGCommand
{
	protected boolean updating;
	protected long lastUpdateTime;
	protected List<String> leaderboard;

	public CmdLeaderboard(SwornRPG plugin)
	{
		super(plugin);
		this.name = "lb";
		this.aliases.add("top");
		this.description = "Display experience leaderboard";
		this.permission = Permission.LEADERBOARD;
		this.mustBePlayer = true;
		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		if (updating)
		{
			err("Leaderboard is already updating!");
			return;
		}

		if (leaderboard == null)
		{
			this.leaderboard = new ArrayList<>();
		}

		if (System.currentTimeMillis() - lastUpdateTime > 600000L)
		{
			sendMessage(plugin.getMessage("leaderboard_wait"));
			this.updating = true;
			new BuildLeaderboardThread();
		}

		new DisplayLeaderboardThread(sender.getName(), args);
	}

	public void displayLeaderboard(String playerName, String[] args)
	{
		Player player = Util.matchPlayer(playerName);
		if (player == null)
			return;

		int index = 1;

		if (args.length > 0)
		{
			int indexFromArg = argAsInt(0, false);
			if (indexFromArg > 1)
				index = indexFromArg;
		}

		int pageCount = getPageCount();

		if (index > pageCount)
		{
			sendMessage(player, "&cError: &4" + getMessage("error_no_page_with_index"), args[0]);
			return;
		}

		for (String s : getPage(index))
			sendMessage(player, s);
	}

	private int linesPerPage = 10;

	public int getPageCount()
	{
		return (getListSize() + linesPerPage - 1) / linesPerPage;
	}

	public int getListSize()
	{
		return leaderboard.size();
	}

	public List<String> getPage(int index)
	{
		List<String> lines = new ArrayList<String>();

		StringBuilder line = new StringBuilder();
		line.append(getHeader(index));
		lines.add(line.toString());

		lines.addAll(getLines((index - 1) * linesPerPage, index * linesPerPage));

		if (index != getPageCount())
		{
			line = new StringBuilder();
			line.append(FormatUtil.format(getMessage("leaderboard_nextpage"), index + 1));
			lines.add(line.toString());
		}

		return lines;
	}
	
	public String getHeader(int index)
	{
		return FormatUtil.format(getMessage("leaderboard_header"), index, getPageCount());
	}

	public List<String> getLines(int startIndex, int endIndex)
	{
		List<String> lines = new ArrayList<String>();
		for (int i = startIndex; i < endIndex && i < getListSize(); i++)
		{
			lines.add(leaderboard.get(i));
		}

		return lines;
	}

	public class BuildLeaderboardThread extends Thread
	{
		public BuildLeaderboardThread()
		{
			super("SwornRPG-BuildLeaderboard");
			this.setPriority(MIN_PRIORITY);
			this.start();
		}

		@Override
		public void run()
		{
			plugin.outConsole("Updating leaderboard...");

			long start = System.currentTimeMillis();

			Map<String, PlayerData> allData = plugin.getPlayerDataCache().getAllPlayerData();
			Map<PlayerData, Integer> experienceMap = new HashMap<PlayerData, Integer>();

			for (Entry<String, PlayerData> entry : allData.entrySet())
			{
				PlayerData value = entry.getValue();
				if (value.getTotalxp() > 0)
				{
					experienceMap.put(value, value.getTotalxp());
				}
			}

			if (experienceMap.isEmpty())
			{
				err("No players with XP found");
				return;
			}

			List<Entry<PlayerData, Integer>> sortedEntries = new ArrayList<Entry<PlayerData, Integer>>(experienceMap.entrySet());
			Collections.sort(sortedEntries, new Comparator<Entry<PlayerData, Integer>>()
			{
				@Override
				public int compare(Entry<PlayerData, Integer> entry1, Entry<PlayerData, Integer> entry2)
				{
					return -entry1.getValue().compareTo(entry2.getValue());
				}
			});

			// Clear the map
			experienceMap.clear();

			String format = getMessage("leaderboard_format");

			for (int i = 0; i < sortedEntries.size(); i++)
			{
				try
				{
					PlayerData data = sortedEntries.get(i).getKey();

					String space = "";
					String name = data.getLastKnownBy();
					for (int ii = name.length(); ii < 19; ii++)
						space = space + " ";
					name = name + space;

					leaderboard.add(FormatUtil.format(format, i + 1, name, data.getLevel(), data.getTotalxp()));
				} catch (Throwable ex) { }
			}

			sortedEntries.clear();

			lastUpdateTime = System.currentTimeMillis();
			updating = false;

			plugin.outConsole("Leaderboard updated! [{0}ms]", System.currentTimeMillis() - start);

			// Save the data
			plugin.getPlayerDataCache().save();

			// Clean up the data
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					plugin.getPlayerDataCache().cleanupData();
				}
			}.runTaskLater(plugin, 2L);
		}
	}

	public class DisplayLeaderboardThread extends Thread
	{
		private final String[] args;
		private final String playerName;
		public DisplayLeaderboardThread(String playerName, String[] args)
		{
			super("SwornRPG-DisplayLeaderboard");
			this.setPriority(MIN_PRIORITY);
			this.playerName = playerName;
			this.args = args;
			this.start();
		}

		@Override
		public void run()
		{
			try
			{
				while (updating)
				{
					sleep(500L);
				}

				displayLeaderboard(playerName, args);
			}
			catch (Throwable ex)
			{
				Player player = Util.matchPlayer(playerName);
				if (player != null)
					sendMessage(player, "&cError: &4Failed to update leaderboard: &c{0}", ex);

				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "updating leaderboard"));
			}
		}
	}
}