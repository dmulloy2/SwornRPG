package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdStaffList extends SwornRPGCommand
{
	public CmdStaffList(SwornRPG plugin)
	{
		super(plugin);
		this.name = "stafflist";
		this.aliases.add("staff");
		this.description = "List online staff";
		this.permission = Permission.STAFFLIST;
	}

	@Override
	public void perform()
	{
		HashMap<String, List<String>> staffMap = new HashMap<String, List<String>>();

		for (Player player : plugin.getServer().getOnlinePlayers())
		{
			if (plugin.getPermissionHandler().hasPermission(player, Permission.STAFF))
			{
				if (plugin.getPermission() != null)
				{
					String group = plugin.getPermission().getPrimaryGroup(player);
					if (group != null)
					{
						if (! staffMap.containsKey(group))
						{
							staffMap.put(group, new ArrayList<String>());
						}
	
						staffMap.get(group).add(player.isOp() ? "&b" : "&e" + player.getName());
					}
				}
				else
				{
					if (! staffMap.containsKey("staff"))
					{
						staffMap.put("staff", new ArrayList<String>());
					}
	
					staffMap.get("staff").add(player.isOp() ? "&b" : "&e" + player.getName());
				}
			}
		}

		List<String> lines = new ArrayList<String>();

		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format("&3There are &e{0} &3out of a maximum &e{1} staff online", staffMap.values().size(),
				plugin.getServer().getMaxPlayers()));
		lines.add(line.toString());

		for (Entry<String, List<String>> entry : staffMap.entrySet())
		{
			line = new StringBuilder();
			line.append("&3" + WordUtils.capitalize(entry.getKey()) + "&e: ");

			for (String player : entry.getValue())
			{
				line.append("&e" + player + "&b, ");
			}

			if (line.lastIndexOf("&b, ") >= 0)
			{
				line.replace(line.lastIndexOf("&"), line.lastIndexOf(" "), "");
			}

			lines.add(line.toString());
		}

		for (String string : lines)
			sendMessage(string);
	}
}