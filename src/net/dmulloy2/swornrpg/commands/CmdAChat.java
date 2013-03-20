package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdAChat extends SwornRPGCommand
{
	public CmdAChat (SwornRPG plugin)
	{
		super(plugin);
		this.name = "a";
		this.description = "Admin only chat";
		this.aliases.add("achat");
		this.requiredArgs.add("message");
		this.permission = PermissionType.CMD_ACHAT.permission;
	}
	
	@Override
	public void perform()
	{
		int amt = args.length;
		String str = "";
		for (int i = 0; i < amt; i++)
		{
			str = str + args[i] + " ";
		}
		String sname;
		if (sender instanceof Player)
			sname = sender.getName();
		else
			sname = "Console";
		Bukkit.getServer().broadcast(ChatColor.GRAY + sname + ": " + ChatColor.AQUA + str, "srpg.adminchat");
	}
}