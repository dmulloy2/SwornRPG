package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * @author dmulloy2
 */

public class CmdASay extends SwornRPGCommand
{
	public CmdASay (SwornRPG plugin)
	{
		super(plugin);
		this.name = "asay";
		this.aliases.add("adm");
		this.description = "Broadcast a colored message to the server";
		this.requiredArgs.add("message");
		this.permission = PermissionType.CMD_ASAY.permission;
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
		Bukkit.getServer().broadcastMessage(ChatColor.RED + "[" + ChatColor.DARK_RED + "Admin" + ChatColor.RED + "]: " + str);
	}
}