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
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.OfflinePlayer;

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

		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		if (leaderboard == null)
		{
			this.leaderboard = new ArrayList<String>();
		}

		if ((System.currentTimeMillis() - lastUpdateTime) > 18000L)
		{
			sendpMessage(getMessage("leaderboard_wait"));

			new BuildLeaderboardThread();
		}

		new DisplayLeaderboardThread();
	}

	public void displayLeaderboard()
	{
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
			err(getMessage("error-no-page-with-index"), args[0]);
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
			this.thread.start();
		}

		@Override
		public void run()
		{
			updating = true;
			
			plugin.outConsole("Updating leaderboard...");

			long start = System.currentTimeMillis();

			Map<String, PlayerData> allData = plugin.getPlayerDataCache().getAllPlayerData();
			Map<String, Integer> experienceMap = new HashMap<String, Integer>();
			
			for (Entry<String, PlayerData> entry : allData.entrySet())
			{
				if (entry.getValue().getTotalxp()  > 0)
				{
					experienceMap.put(entry.getKey(), entry.getValue().getTotalxp());
				}
			}

//			loadedData.clear();
//			loadedData = null;

			List<Entry<String, Integer>> sortedEntries = new ArrayList<Entry<String, Integer>>(experienceMap.entrySet());
			Collections.sort(sortedEntries, new Comparator<Entry<String, Integer>>()
			{
				@Override
				public int compare(final Entry<String, Integer> entry1, final Entry<String, Integer> entry2)
				{
					return -entry1.getValue().compareTo(entry2.getValue());
				}
			});

			experienceMap.clear();
			experienceMap = null;

			int pos = 1;
			for (Entry<String, Integer> entry : sortedEntries)
			{
				try
				{
					OfflinePlayer player = Util.matchOfflinePlayer(entry.getKey());
					if (player != null)
					{
						PlayerData data = getPlayerData(player);
						if (data != null)
						{
							leaderboard.add(FormatUtil.format(getMessage("leaderboard_format"),
									pos, player.getName(), data.getLevel(), data.getTotalxp()));
							pos++;
						}
						
						data = null;
					}
					
					player = null;
				}
				catch (Exception e)
				{
					plugin.outConsole(Level.SEVERE, Util.getUsefulStack(e, "building leaderboard entry for " + entry.getKey()));
					continue;
				}
			}
			
			sortedEntries.clear();
			sortedEntries = null;

			lastUpdateTime = System.currentTimeMillis();

			updating = false;

			plugin.outConsole("Leaderboard updated! [{0}ms]", System.currentTimeMillis() - start);

			plugin.getPlayerDataCache().save();
		}
	}

	public class DisplayLeaderboardThread extends Thread
	{
		private Thread thread;
		public DisplayLeaderboardThread()
		{
			this.thread = new Thread(this, "SwornRPG-DisplayLeaderboard");
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

				displayLeaderboard();
			}
			catch (Exception e)
			{
				err("Could not update leaderboard: {0}", e);
			}
		}
	}
}