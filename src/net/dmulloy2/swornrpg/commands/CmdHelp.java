package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.Perms;
import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdHelp implements CommandExecutor
{
	public SwornRPG plugin;
	public CmdHelp (SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		if (sender instanceof Player) 
		{
			sender = (Player) sender;
		}
		if(args.length == 0)
		{
			plugin.displayHelp(sender);
		}
		else if (args[0].equalsIgnoreCase("reload"))
		{
			if (Perms.has(sender, plugin.adminReloadPerm))
			{
				plugin.reloadConfig();
				sender.sendMessage(plugin.prefix + ChatColor.GREEN + "Configuration reloaded");
				if (sender instanceof Player)
				{
					plugin.outConsole("Configuration reloaded");
				}
			}
			else
			{
				sender.sendMessage(plugin.noperm);
			}
		}
		else if (args[0].equalsIgnoreCase("save"))
		{
			if(Perms.has(sender, plugin.adminReloadPerm))
			{
				plugin.getPlayerDataCache().save();
				sender.sendMessage(plugin.prefix + ChatColor.GREEN + "Player data files saved");
			}
			else
			{
				sender.sendMessage(plugin.noperm);
			}
		}
		else if (args[0].equalsIgnoreCase("help"))
		{
			plugin.displayHelp(sender);	  
		}
		else if (args[0].equalsIgnoreCase("ride"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "====== " + ChatColor.GOLD + "SwornRPG Ride Commands" + ChatColor.DARK_RED + " ======"); 
			sender.sendMessage(ChatColor.RED + "/<command>" + ChatColor.DARK_RED + " <required> " + ChatColor.GOLD + "[optional]");
			if(Perms.has(sender, plugin.adminRidePerm))
			{
				sender.sendMessage(ChatColor.RED + "/ride" + ChatColor.DARK_RED + " <player> " + ChatColor.YELLOW + "Ride another player");
				sender.sendMessage(ChatColor.RED + "/unride" + ChatColor.YELLOW + " Stop riding another player");
				sender.sendMessage(ChatColor.RED + "/eject" + ChatColor.YELLOW + " Kick someone off your head");
			}
		}
		else if (args[0].equalsIgnoreCase("chat"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "====== " + ChatColor.GOLD + "SwornRPG Chat Commands" + ChatColor.DARK_RED + " ======"); 
			sender.sendMessage(ChatColor.RED + "/<command>" + ChatColor.DARK_RED + " <required> " + ChatColor.GOLD + "[optional]");
			if (Perms.has(sender, plugin.adminChatPerm))
			{
				sender.sendMessage(ChatColor.RED + "/a" + ChatColor.DARK_RED + " <message> "+ ChatColor.YELLOW + "Talk in admin chat");
			}
			if (Perms.has(sender, plugin.councilChatPerm))
			{
				sender.sendMessage(ChatColor.RED + "/hc" + ChatColor.DARK_RED + " <message> " + ChatColor.YELLOW + "Talk in council chat");
			}
			if (Perms.has(sender, plugin.adminSayPerm))
			{
				sender.sendMessage(ChatColor.RED + "/asay" + ChatColor.DARK_RED + " <message> " + ChatColor.YELLOW + "Alternate admin say command");
			}
		}
		else if (args[0].equalsIgnoreCase("tag"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "====== " + ChatColor.GOLD + "SwornRPG Tag Commands" + ChatColor.DARK_RED + " ======"); 
			sender.sendMessage(ChatColor.RED + "/<command>" + ChatColor.DARK_RED + " <required> " + ChatColor.GOLD + "[optional]");
			if (Perms.has(sender, plugin.tagPerm))
			{
				sender.sendMessage(ChatColor.RED + "/tag" + ChatColor.GOLD + " [player] " + ChatColor.DARK_RED + "<tag> " + ChatColor.YELLOW + "Change the name above your head");
			}
			if (Perms.has(sender, plugin.tagresetPerm))
			{
				sender.sendMessage(ChatColor.RED + "/tagr" + ChatColor.GOLD + " [player] " + ChatColor.YELLOW + "Resets a player's tag");
			}
		}
		else if (args[0].equalsIgnoreCase("level"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "====== " + ChatColor.GOLD + "SwornRPG Level Commands" + ChatColor.DARK_RED + " ======"); 
			sender.sendMessage(ChatColor.RED + "/<command>" + ChatColor.DARK_RED + " <required> " + ChatColor.GOLD + "[optional]");
			sender.sendMessage(ChatColor.RED + "/level " + ChatColor.GOLD + "[name] " + ChatColor.YELLOW + "Displays your current level");
			if (Perms.has(sender, plugin.adminResetPerm))
			{
				sender.sendMessage(ChatColor.RED + "/levelr " + ChatColor.GOLD + "[name] " + ChatColor.YELLOW + "Resets a player's level.");
			}
			sender.sendMessage(ChatColor.RED + "/frenzy" + ChatColor.YELLOW + " Enters Frenzy mode.");
		}
		else if (args[0].equalsIgnoreCase("misc"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "====== " + ChatColor.GOLD + "SwornRPG Miscellaneous Commands" + ChatColor.DARK_RED + " ======"); 
			sender.sendMessage(ChatColor.RED + "/<command>" + ChatColor.DARK_RED + " <required> " + ChatColor.GOLD + "[optional]");
			sender.sendMessage(ChatColor.RED + "/propose" + ChatColor.DARK_RED + " <player> " + ChatColor.YELLOW + "Request to marry a player");
			sender.sendMessage(ChatColor.RED + "/deny" + ChatColor.DARK_RED + " <player> " + ChatColor.YELLOW + "Deny a player's hand in marriage");
			sender.sendMessage(ChatColor.RED + "/marry" + ChatColor.DARK_RED + " <player> " + ChatColor.YELLOW + "Marry another player");
			sender.sendMessage(ChatColor.RED + "/spouse" + ChatColor.GOLD + " [player] " + ChatColor.YELLOW + "Shows information about a player's spouse");
			sender.sendMessage(ChatColor.RED + "/match" + ChatColor.DARK_RED + " <string> " + ChatColor.YELLOW + "Match a string with the closest player");
			sender.sendMessage(ChatColor.RED + "/deathmessage" + ChatColor.YELLOW + " Toggles death coordinate books/messages");
		}
		else
		{
			plugin.displayHelp(sender);
		}
		
		return true;
	}
}