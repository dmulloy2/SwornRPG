package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

/**
 * @author dmulloy2
 */

public class CmdLeaderboard extends SwornRPGCommand
{
	public CmdLeaderboard (SwornRPG plugin)
	{
		super(plugin);
		this.name = "top";
		this.description = "Display level leaderboard";
		this.aliases.add("lb");
		this.usesPrefix = true;
	}
	
	@Override
	public void perform()
	{
		Map<String, PlayerData> data = plugin.getPlayerDataCache().getAllPlayerData();
		sendpMessage("&eOrdering Statistics of &a{0} &ePlayers...", data.size());
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
		line.append(FormatUtil.format("&e==== &aSwornRPG Leaderboard &e===="));
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
						line.append(FormatUtil.format("&a{0}&e) &a{1} &eLevel: &a{2} &eXP: &a{3}", pos, player.getName(), level, xp));
						lines.add(line.toString());
						pos++;
					}
				}
			}
		}
		
		for (String s : lines)
			sendMessage(s);
		
		sendpMessage("&eCheck your stats with &c/level&e!");
	}
}