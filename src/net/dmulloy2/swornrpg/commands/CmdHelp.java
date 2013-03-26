package net.dmulloy2.swornrpg.commands;

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
	
	//Permission Strings
	public String adminChatPerm = "srpg.adminchat";
	public String adminRidePerm = "srpg.ride";
	public String adminSayPerm = "srpg.asay";
	public String adminResetPerm = "srpg.levelr";
	public String councilChatPerm = "srpg.council";
	public String adminReloadPerm = "srpg.reload";
	public String hatPerm = "srpg.hat";
	public String matchPerm = "srpg.match";
	public String tagPerm = "srpg.tag";
	public String tagresetPerm = "srpg.tagr";
	public String adminItemPerm = "srpg.iname";
	public String adminMatchPerm = "srpg.match";
	
	@Override
	public void perform()
	{
		if(args.length == 0)
		{
			displayHelp(sender);
		}
		else if (args[0].equalsIgnoreCase("reload"))
		{
			if (hasPerm(sender, adminReloadPerm))
			{
				plugin.reload();
				plugin.updateBlockDrops();
				sendpMessage(plugin.getMessage("config_reloaded"));
				if (sender instanceof Player)
				{
					plugin.outConsole("Configuration reloaded");
				}
			}
			else
			{
				sendpMessage(plugin.getMessage("noperm"));
			}
		}
		else if (args[0].equalsIgnoreCase("save"))
		{
			if(hasPerm(sender, adminReloadPerm))
			{
				plugin.getPlayerDataCache().save();
				sendpMessage(plugin.getMessage("data_saved"));
			}
			else
			{
				sendpMessage(plugin.getMessage("noperm"));
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
			if(hasPerm(sender, adminRidePerm))
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
			if (hasPerm(sender, adminChatPerm))
			{
				sendMessage("&c/a &4<message> &eTalk in admin chat");
			}
			if (hasPerm(sender, councilChatPerm))
			{
				sendMessage("&c/hc &4<message> &eTalk in council chat");
			}
			if (hasPerm(sender, adminSayPerm))
			{
				sendMessage("&c/asay &4<message> &eAlternate admin say command");
			}
		}
		else if (args[0].equalsIgnoreCase("tag"))
		{
			sendMessage("&4====== &6SwornRPG Tag Commands &4======"); 
			sendMessage("&c/<command> &4<required> &6[optional]");
			if (hasPerm(sender, tagPerm))
			{
				sendMessage("&c/tag &6[player] &4<tag> &eChange the color of the name above your head");
			}
			if (hasPerm(sender, tagresetPerm))
			{
				sendMessage("&c/tagr &6player] &eReset a player's tag");
			}
		}
		else if (args[0].equalsIgnoreCase("level"))
		{
			sendMessage("&4====== &6SwornRPG Level Commands &4======"); 
			sendMessage("&c/<command> &4<required> &6[optional]");
			sendMessage("&c/level &6[name] &eDisplays your current level");
			if (hasPerm(sender, adminResetPerm))
			{
				sendMessage("&c/levelr &6[name] &eReset a player's level.");
				sendMessage("&c/addxp &4<name> &eGive xp to a player");
			}
			sendMessage("&c/frenzy &eEnter Frenzy mode.");
			sendMessage("&c/mine &eActivate super pickaxe");
		}
		else if (args[0].equalsIgnoreCase("misc"))
		{
			sendMessage("&4====== &6SwornRPG Misc Commands &4======"); 
			sendMessage("&c/<command> &4<required> &6[optional]");
			sendMessage("&c/deathmessage &eToggles death coordinate books/messages");
			sendMessage("&c/standup &eGet out of your chair");
			sendMessage("&c/sitdown &eSit in a chair");
			sendMessage("&c/stafflist &eList online staff");
			if (hasPerm(sender, adminItemPerm))
			{
				sendMessage("&c/iname &4<name> &eSet the name of an item");
			}
			if (hasPerm(sender, adminMatchPerm))
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
    	if (hasPerm(p, adminReloadPerm))
    	{
    		sendMessage("&c/srpg &4reload &eReload the configuration");
    		sendMessage("&c/srpg &4save &eSave all player data");
    	}
    	sendMessage("&c/srpg &4help &eDisplay this help menu");			
    	if (hasPerm(p, adminRidePerm))
    	{
    		sendMessage("&c/srpg &4ride &eDisplay ride commands");
    	}
    	if (hasPerm(p, adminChatPerm))
    	{
    		sendMessage("&c/srpg &4chat &eDisplay chat commands");
    	}
    	if (hasPerm(p, tagPerm))
    	{
    		sendMessage("&c/srpg &4tag &eDisplay tag commands");		
    	}
    	sendMessage("&c/srpg &4level &eDisplay level commands");
    	sendMessage("&c/srpg &4marriage &eDisplay marriage commands");
    	sendMessage("&c/srpg &4misc &eDisplay miscellaneous commands");
    	if (hasPerm(p, hatPerm))
    	{
    		sendMessage("&c/hat &6[remove] &eGet a new hat!");
    	}
    }
    
    //Perms check
	public static boolean hasPerm(CommandSender player, String command)
	{
		return player.hasPermission(command) || player.isOp();
	}
}