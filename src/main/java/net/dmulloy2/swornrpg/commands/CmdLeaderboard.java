package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

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
			this.leaderboard = new ArrayList<String>();
		}

		if ((System.currentTimeMillis() - lastUpdateTime) > 600000L || leaderboard.isEmpty())
		{
			sendpMessage(getMessage("leaderboard_wait"));
			
			leaderboard.clear();
			updating = true;

			new BuildLeaderboardThread();
		}

		new DisplayLeaderboardThread(player.getName());
	}

	public void displayLeaderboard(String playerName)
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
			err(getMessage("error_no_page_with_index"), args[0]);
			return;
		}

		for (String s : getPage(index))
			sendMessage(s);
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
		private Thread thread;
		public BuildLeaderboardThread()
		{
			this.thread = new Thread(this, "SwornRPG-BuildLeaderboard");
			this.thread.setPriority(1); // lowest priority
			this.thread.start();
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

			int pos = 1;
			for (Entry<PlayerData, Integer> entry : sortedEntries)
			{
				try
				{
					PlayerData data = entry.getKey();
					leaderboard.add(FormatUtil.format(getMessage("leaderboard_format"), pos, data.getLastKnownBy(), data.getLevel(),
							data.getTotalxp()));
					pos++;
				}
				catch (Throwable ex)
				{
					// Swallow the exception, move on
					continue;
				}
			}

			sortedEntries.clear();

			lastUpdateTime = System.currentTimeMillis();

			updating = false;

			plugin.outConsole("Leaderboard updated! [{0}ms]", System.currentTimeMillis() - start);

			// Save the data
			plugin.getPlayerDataCache().save();

			// Clean up the data sync
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
		private String player;
		private Thread thread;
		public DisplayLeaderboardThread(String player)
		{
			this.thread = new Thread(this, "SwornRPG-DisplayLeaderboard");
			this.player = player;

			this.thread.setPriority(1); // lowest priority
			this.thread.start();
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

				displayLeaderboard(player);
			}
			catch (Throwable ex)
			{
				err("Could not update leaderboard: {0}", ex);
			}
		}
	}
}