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

public class CmdHighCouncil implements CommandExecutor
{	
	public SwornRPG plugin;
	public CmdHighCouncil(SwornRPG plugin)  
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
				Bukkit.getServer().broadcast(ChatColor.GOLD + sender.getName() + ": " + ChatColor.RED + str, "srpg.council");
			}
			else
			{
				Bukkit.getServer().broadcast(ChatColor.GOLD + "Console: " + ChatColor.RED + str, "srpg.council");	
			}
		}
		else
		{
			sender.sendMessage(plugin.invalidargs + "(/hc <message>)");
		}
		  
		return true;
	}
}