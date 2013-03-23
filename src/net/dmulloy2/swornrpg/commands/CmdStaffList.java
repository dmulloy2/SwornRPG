package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdStaffList extends SwornRPGCommand
{
	public CmdStaffList (SwornRPG plugin)
	{
		super(plugin);
		this.name = "stafflist";
		this.aliases.add("staff");
		this.description = "List the staff that are currently on";
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		List<String> lines = new ArrayList<String>();
		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format("&4==== &6Online Staff &4 ===="));
		lines.add(line.toString());
		for (Player player : plugin.getServer().getOnlinePlayers()) 
		{
			if (player.hasPermission("srpg.staff"))
			{
				line = new StringBuilder();
				if (player.isOp())
					line.append(FormatUtil.format("&c" + player.getName()));
				else
					line.append(FormatUtil.format("&6" + player.getName()));
				lines.add(line.toString());
			}
		}
		
		for (String string : lines)
			sendMessage(string);
	}
}