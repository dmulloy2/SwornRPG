package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */

public class CmdLeaderboard extends SwornRPGCommand
{
	public CmdLeaderboard (SwornRPG plugin)
	{
		super(plugin);
		this.name = "lb";
		this.description = "Display level leaderboard";
		this.aliases.add("top");
		this.usesPrefix = true;
	}
	
	@Override
	public void perform()
	{
		sendpMessage(plugin.getMessage("leaderboard_wait"));
		Map<String, PlayerData> data = plugin.getPlayerDataCache().getAllPlayerData();
		HashMap<String, Integer> xpmap = new HashMap<String, Integer>();
		for (Entry<String, PlayerData> entrySet : data.entrySet())
		{
			String player = entrySet.getKey();
			PlayerData data1 = entrySet.getValue();
			int xp = data1.getTotalxp();
			xpmap.put(player, xp);
		}
		
		final List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<Map.Entry<String, Integer>>(xpmap.entrySet());
		Collections.sort(
		sortedEntries, new Comparator<Map.Entry<String, Integer>>()
		{
			@Override
			public int compare(final Entry<String, Integer> entry1, final Entry<String, Integer> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});
		
		List<String>lines = new ArrayList<String>();
		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format(plugin.getMessage("leaderboard_header")));
		lines.add(line.toString());
		
		int pos = 1;
		for (Map.Entry<String, Integer> entry : sortedEntries)
		{
			if (pos <= 10)
			{
				String string = entry.getKey();
				OfflinePlayer player = Util.matchOfflinePlayer(string);
				if (player != null)
				{
					PlayerData data2 = getPlayerData(player);
					if (data2 != null)
					{
						int level = data2.getLevel();
						int xp = data2.getTotalxp();
					
						line = new StringBuilder();
						line.append(FormatUtil.format(plugin.getMessage("leaderboard_format"), pos, player.getName(), level, xp));
						lines.add(line.toString());
						pos++;
					}
				}
			}
		}
		
		for (String s : lines)
			sendMessage(s);
		
		sendMessage(plugin.getMessage("leaderboard_check"));
	}
}

/*public class CmdLeaderboard extends PaginatedCommand
{
	private long lastUpdate;
	private boolean hasBeenUpdated;
	private List<String> leaderboard;
	public CmdLeaderboard(SwornRPG plugin)
	{
		super(plugin);
		this.name = "lb";
		this.aliases.add("top");
		this.optionalArgs.add("page");
		this.description = "Displays level leaderboard";
		this.linesPerPage = 10;
		this.usesPrefix = true;
		
		this.lastUpdate = System.currentTimeMillis();
		this.leaderboard = new ArrayList<String>();
	}

	@Override
	public int getListSize() 
	{
		return leaderboard.size();
	}

	@Override
	public String getHeader(int index) 
	{
		return FormatUtil.format(plugin.getMessage("leaderboard_header"), index, getPageCount());
	}
	
	@Override
	public void perform() 
	{
		if ((System.currentTimeMillis() - lastUpdate) >= 18000L || ! hasBeenUpdated)
		{
			sendpMessage(getMessage("leaderboard_wait"));
			buildLeaderboard();
		}
		
		int index = 1;
		if (this.args.length > pageArgIndex) 
		{
			try 
			{
				index = Integer.parseInt(args[pageArgIndex]);
				if (index < 1 || index > getPageCount())
					throw new IndexOutOfBoundsException();
			}
			catch (NumberFormatException ex)
			{
				err(plugin.getMessage("error-invalid-page"), args[0]);
				return;
			}
			catch (IndexOutOfBoundsException ex) 
			{
				err(plugin.getMessage("error-no-page-with-index"), args[0]);
				return;
			}
		}
		
		List<String> page = getPage(index);
		page.add(FormatUtil.format(plugin.getMessage("leaderboard_check")));
		
		for (String s : page)
			sendMessage(s);
	}
	
	public void buildLeaderboard()
	{
		leaderboard.clear();
		
		Map<String, PlayerData> data = plugin.getPlayerDataCache().getAllPlayerData();
		HashMap<String, Integer> xpmap = new HashMap<String, Integer>();
		for (Entry<String, PlayerData> entrySet : data.entrySet())
		{
			String player = entrySet.getKey();
			PlayerData data1 = entrySet.getValue();
			int xp = data1.getTotalxp();
			if (xp > 0)
				xpmap.put(player, xp);
		}
		
		final List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<Map.Entry<String, Integer>>(xpmap.entrySet());
		Collections.sort(
				sortedEntries, new Comparator<Map.Entry<String, Integer>>()
				{
					@Override
					public int compare(final Entry<String, Integer> entry1, final Entry<String, Integer> entry2)
					{
						return -entry1.getValue().compareTo(entry2.getValue());
					}
				});

		int pos = 1;
		for (Map.Entry<String, Integer> entry : sortedEntries)
		{
			String string = entry.getKey();
			OfflinePlayer player = Util.matchOfflinePlayer(string);
			if (player != null)
			{
				PlayerData data2 = getPlayerData(player);
				if (data2 != null)
				{
					int level = data2.getLevel();
					int xp = data2.getTotalxp();
							
					StringBuilder line = new StringBuilder();
					line.append(FormatUtil.format(plugin.getMessage("leaderboard_format"), pos, player.getName(), level, xp));
					leaderboard.add(line.toString());
					pos++;
				}
			}
		}
		
		this.hasBeenUpdated = true;
		this.lastUpdate = System.currentTimeMillis();
	}
	
	@Override
	public List<String> getLines(int startIndex, int endIndex) 
	{
		List<String> lines = new ArrayList<String>();
		for (int i = startIndex; i < endIndex && i < getListSize(); i++) 
		{
			lines.add(leaderboard.get(i));
		}
		
		return lines;
	}

	@Override
	public String getLine(int index)
	{
		// Not needed since we override perform
		return null;
	}
}*/