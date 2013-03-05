package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdAChat implements CommandExecutor
{	
	public SwornRPG plugin;
	public CmdAChat(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length > 0)
		{
			int amt = args.length;
			String str = "";
			for (int i = 0; i < amt; i++)
			{
				str = str + args[i] + " ";
			}
			if (sender instanceof Player)
			{
				Bukkit.getServer().broadcast(ChatColor.GRAY + sender.getName() + ": " + ChatColor.AQUA + str, "srpg.adminchat");
			}
			else
			{
				Bukkit.getServer().broadcast(ChatColor.GRAY + "Console: " + ChatColor.AQUA + str, "srpg.adminchat");
			}
		}
		else
		{
			sender.sendMessage(plugin.invalidargs + "(/a <message>)");
		}
		
		return true;
	}
}