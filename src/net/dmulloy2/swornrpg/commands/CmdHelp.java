package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.PermissionInterface;
import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdHelp implements CommandExecutor{
	
	public SwornRPG plugin;
	  public CmdHelp (SwornRPG plugin)  {
	    this.plugin = plugin;

	  }
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
		    Player player = null;
		    if (sender instanceof Player) {
		      player = (Player) sender;
		    }
		    if(args.length == 0){
	    		  player.sendMessage(ChatColor.DARK_RED + "======" + ChatColor.GOLD + " SwornRPG " + ChatColor.DARK_RED + "======");
	        	  player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " <args> ");
	        	  player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " help " + ChatColor.YELLOW + "Displays this help menu");
	        	  //player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " level " + ChatColor.YELLOW + "Displays your current level");
	        	  //if if (PermissionInterface.checkPermission(player, this.plugin.adminClearPerm)){
	        		  //player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " levelr <name> " + ChatColor.YELLOW + "Resets a player's level.");}
	        	  //player.sendMessage(ChatColor.RED + "/frenzy" + ChatColor.YELLOW + " Enters beast mode");
	        	  if (PermissionInterface.checkPermission(player, this.plugin.adminRidePerm)){
	        		  player.sendMessage(ChatColor.RED + "/ride" + ChatColor.GOLD + " (unride) " + ChatColor.YELLOW + "Ride another player");}
	        	  player.sendMessage(ChatColor.RED + "/hat " + ChatColor.YELLOW + "Get a new hat!");
	        	  if (PermissionInterface.checkPermission(player, this.plugin.adminChatPerm)){
	        		  player.sendMessage(ChatColor.RED + "/a " + ChatColor.YELLOW + "Talk in admin chat");}
	        	  if (PermissionInterface.checkPermission(player, this.plugin.councilChatPerm)){
	        		  player.sendMessage(ChatColor.RED + "/hc " + ChatColor.YELLOW + "Talk in council chat");}
	        	  if (player.hasPermission("srpg.asay")){
	        		  player.sendMessage(ChatColor.RED + "/adm " + ChatColor.YELLOW + "Alternate admin say command");}
	        	  if (player.getName().contains("dmulloy2")){
	            	  player.sendMessage(ChatColor.RED + "/dmu " + ChatColor.YELLOW + "dmulloy's special chat");}
	      }
	    	  else if (args[0].equals("level")){
	    		  player.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.YELLOW + "This command has not been implimented yet");
	    	  }
	    	  else if (args[0].equals("levelr")){
	    		  if (PermissionInterface.checkPermission(player, this.plugin.adminClearPerm)) {
	    			  player.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.YELLOW + "This command has not been implimented yet");
	    		  }else{
	    			  player.sendMessage(ChatColor.RED + "You do not have permission to perform this command");
	    			  System.out.println("[SwornRPG] " + player.getName() + " was denied access to a command");
	    		  }
	    		  
	    	  }
	    	  else if (args[0].equals("help")){
	    		  player.sendMessage(ChatColor.DARK_RED + "======" + ChatColor.GOLD + " SwornRPG " + ChatColor.DARK_RED + "======");
	        	  player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " <args> ");
	        	  player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " help " + ChatColor.YELLOW + "Displays this help menu");
	        	  //player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " level " + ChatColor.YELLOW + "Displays your current level");
	        	  //if if (PermissionInterface.checkPermission(player, this.plugin.adminClearPerm)){
	        		  //player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " levelr <name> " + ChatColor.YELLOW + "Resets a player's level.");}
	        	  //player.sendMessage(ChatColor.RED + "/frenzy" + ChatColor.YELLOW + " Enters beast mode");
	        	  if (PermissionInterface.checkPermission(player, this.plugin.adminRidePerm)){
	        		  player.sendMessage(ChatColor.RED + "/ride" + ChatColor.GOLD + " (unride) " + ChatColor.YELLOW + "Ride another player");}
	        	  player.sendMessage(ChatColor.RED + "/hat " + ChatColor.YELLOW + "Get a new hat!");
	        	  if (PermissionInterface.checkPermission(player, this.plugin.adminChatPerm)){
	        		  player.sendMessage(ChatColor.RED + "/a " + ChatColor.YELLOW + "Talk in admin chat");}
	        	  if (PermissionInterface.checkPermission(player, this.plugin.councilChatPerm)){
	        		  player.sendMessage(ChatColor.RED + "/hc " + ChatColor.YELLOW + "Talk in council chat");}
	        	  if (player.hasPermission("srpg.asay")){
	        		  player.sendMessage(ChatColor.RED + "/adm " + ChatColor.YELLOW + "Alternate admin say command");}
	        	  if (player.getName().contains("dmulloy2")){
	            	  player.sendMessage(ChatColor.RED + "/dmu " + ChatColor.YELLOW + "dmulloy's special chat");}
	      }else{
    		  player.sendMessage(ChatColor.DARK_RED + "======" + ChatColor.GOLD + " SwornRPG " + ChatColor.DARK_RED + "======");
        	  player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " <args> ");
        	  player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " help " + ChatColor.YELLOW + "Displays this help menu");
        	  //player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " level " + ChatColor.YELLOW + "Displays your current level");
        	  //if if (PermissionInterface.checkPermission(player, this.plugin.adminClearPerm)){
        		  //player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " levelr <name> " + ChatColor.YELLOW + "Resets a player's level.");}
        	  //player.sendMessage(ChatColor.RED + "/frenzy" + ChatColor.YELLOW + " Enters beast mode");
        	  if (PermissionInterface.checkPermission(player, this.plugin.adminRidePerm)){
        		  player.sendMessage(ChatColor.RED + "/ride" + ChatColor.GOLD + " (unride) " + ChatColor.YELLOW + "Ride another player");}
        	  player.sendMessage(ChatColor.RED + "/hat " + ChatColor.YELLOW + "Get a new hat!");
        	  if (PermissionInterface.checkPermission(player, this.plugin.adminChatPerm)){
        		  player.sendMessage(ChatColor.RED + "/a " + ChatColor.YELLOW + "Talk in admin chat");}
        	  if (PermissionInterface.checkPermission(player, this.plugin.councilChatPerm)){
        		  player.sendMessage(ChatColor.RED + "/hc " + ChatColor.YELLOW + "Talk in council chat");}
        	  if (player.hasPermission("srpg.asay")){
        		  player.sendMessage(ChatColor.RED + "/adm " + ChatColor.YELLOW + "Alternate admin say command");}
        	  if (player.getName().contains("dmulloy2")){
            	  player.sendMessage(ChatColor.RED + "/dmu " + ChatColor.YELLOW + "dmulloy's special chat");}
	      	}
			return true;
	  }
}