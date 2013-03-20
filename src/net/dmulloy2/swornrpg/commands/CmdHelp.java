package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.Perms;
import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdHelp extends SwornRPGCommand
{
	public CmdHelp (SwornRPG plugin)
	{
		super(plugin);
		this.name = "srpg";
		this.aliases.add("swornrpg");
		this.description = "SwornRPG root command";
		this.optionalArgs.add("help");
		this.optionalArgs.add("misc");
		this.optionalArgs.add("ride");
		this.optionalArgs.add("save");
		this.optionalArgs.add("reload");
		this.optionalArgs.add("chat");
		this.optionalArgs.add("level");
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		if(args.length == 0)
		{
			displayHelp(sender);
		}
		else if (args[0].equalsIgnoreCase("reload"))
		{
			if (Perms.has(sender, plugin.adminReloadPerm))
			{
				plugin.reloadConfig();
				plugin.reloadtagsConfig();
				sendpMessage("&aConfiguration reloaded");
				if (sender instanceof Player)
				{
					plugin.outConsole("Configuration reloaded");
				}
			}
			else
			{
				sendMessage(plugin.noperm);
			}
		}
		else if (args[0].equalsIgnoreCase("save"))
		{
			if(Perms.has(sender, plugin.adminReloadPerm))
			{
				plugin.getPlayerDataCache().save();
				sendpMessage("&aPlayer data files saved");
			}
			else
			{
				sender.sendMessage(plugin.noperm);
			}
		}
		else if (args[0].equalsIgnoreCase("help"))
		{
			displayHelp(sender);	  
		}
		else if (args[0].equalsIgnoreCase("ride"))
		{
			sendMessage("&4====== &6SwornRPG Ride Commands &4======"); 
			sendMessage("&c/<command> &4<required> &6[optional]");
			if(Perms.has(sender, plugin.adminRidePerm))
			{
				sendMessage("&c/ride &4<player> &eRide another player");
				sendMessage("&c/unride &eStop riding another player");
				sendMessage("&c/eject &eKick someone off your head");
			}
		}
		else if (args[0].equalsIgnoreCase("chat"))
		{
			sendMessage("&4====== &6SwornRPG Chat Commands &4======"); 
			sendMessage("&c/<command> &4<required> &6[optional]");
			if (Perms.has(sender, plugin.adminChatPerm))
			{
				sendMessage("&c/a &4<message> &eTalk in admin chat");
			}
			if (Perms.has(sender, plugin.councilChatPerm))
			{
				sendMessage("&c/hc &4<message> &eTalk in council chat");
			}
			if (Perms.has(sender, plugin.adminSayPerm))
			{
				sendMessage("&c/asay &4<message> &eAlternate admin say command");
			}
		}
		else if (args[0].equalsIgnoreCase("tag"))
		{
			sendMessage("&4====== &6SwornRPG Tag Commands &4======"); 
			sendMessage("&c/<command> &4<required> &6[optional]");
			if (Perms.has(sender, plugin.tagPerm))
			{
				sendMessage("&c/tag &6[player] &4<tag> &eChange the color of the name above your head");
			}
			if (Perms.has(sender, plugin.tagresetPerm))
			{
				sendMessage("&c/tagr &6player] &eReset a player's tag");
			}
		}
		else if (args[0].equalsIgnoreCase("level"))
		{
			sendMessage("&4====== &6SwornRPG Level Commands &4======"); 
			sendMessage("&c/<command> &4<required> &6[optional]");
			sendMessage("&c/level &6[name] &eDisplays your current level");
			if (Perms.has(sender, plugin.adminResetPerm))
			{
				sendMessage("&c/levelr &6[name] &eReset a player's level.");
			}
			sendMessage("&c/frenzy &eEnter Frenzy mode.");
		}
		else if (args[0].equalsIgnoreCase("misc"))
		{
			sendMessage("&4====== &6SwornRPG Misc Commands &4======"); 
			sendMessage("&c/<command> &4<required> &6[optional]");
			sendMessage("/deathmessage &eToggles death coordinate books/messages");
			if (Perms.has(sender, plugin.adminItemPerm))
			{
				sendMessage("&c/iname &4<name> &eSet the name of an item");
			}
			if (Perms.has(sender, plugin.adminMatchPerm))
			{
				sendMessage("&c/match &4<string> &eMatch a string with the closest player");
			}
		}
		else if (args[0].equalsIgnoreCase("marriage"))
		{
			sendMessage("&4====== &6SwornRPG Marriage Commands &4======"); 
			sendMessage("&c/<command> &4<required> &6[optional]");
			sender.sendMessage(ChatColor.RED + "/propose" + ChatColor.DARK_RED + " <player> " + ChatColor.YELLOW + "Request to marry a player");
			sender.sendMessage(ChatColor.RED + "/deny" + ChatColor.DARK_RED + " <player> " + ChatColor.YELLOW + "Deny a player's hand in marriage");
			sender.sendMessage(ChatColor.RED + "/marry" + ChatColor.DARK_RED + " <player> " + ChatColor.YELLOW + "Marry another player");
			sender.sendMessage(ChatColor.RED + "/spouse" + ChatColor.GOLD + " [player] " + ChatColor.YELLOW + "Shows information about a player's spouse");
		}
		else
		{
			displayHelp(sender);
		}
	}
    //Main help menu
    public void displayHelp(CommandSender p)
    {
    	sendMessage("&4====== &6" + plugin.getDescription().getFullName() + " &4======"); 
    	sendMessage("&c/<command> &4<required> &6[optional]");
    	if (Perms.has(p, plugin.adminReloadPerm))
    	{
    		sendMessage("&c/srpg &4reload &eReload the configuration");
    		sendMessage("&c/srpg &4save &eSave all player data");
    	}
    	sendMessage("&c/srpg &4help &eDisplay this help menu");			
    	if (Perms.has(p, plugin.adminRidePerm))
    	{
    		sendMessage("&c/srpg &4ride &eDisplay ride commands");
    	}
    	if (Perms.has(p, plugin.adminChatPerm))
    	{
    		sendMessage("&c/srpg &4chat &eDisplay chat commands");
    	}
    	if (Perms.has(p, plugin.tagPerm))
    	{
    		sendMessage("&c/srpg &4tag &eDisplay tag commands");		
    	}
    	sendMessage("&c/srpg &4level &eDisplay level commands");
    	sendMessage("&c/srpg &4marriage &eDisplay marriage commands");
    	sendMessage("&c/srpg &4misc &eDisplay miscellaneous commands");
    	if (Perms.has(p, plugin.hatPerm))
    	{
    		sendMessage("&c/hat &6[remove] &eGet a new hat!");
    	}
    }
}