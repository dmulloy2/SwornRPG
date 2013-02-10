package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.PermissionInterface;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdRide implements CommandExecutor{
	
	public SwornRPG plugin;
	  public CmdRide(SwornRPG plugin)  {
	    this.plugin = plugin;

	  }
	  
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
		    Player player = null;
		    if (sender instanceof Player) {
		      player = (Player) sender;
		    }
		    
		    if (label.equalsIgnoreCase("ride")){
		    			    	
		    	if (PermissionInterface.checkPermission(player, this.plugin.adminRidePerm)) {
		    		
		    		if(args.length < 1){
		    			player.sendMessage(ChatColor.RED + "Error, too few arguments");
		    			player.leaveVehicle();
		    		}else{
		    			Player to = Util.MatchPlayer(args[0]);
		    			to.setPassenger(player);
		    			player.sendMessage(ChatColor.GOLD + "You are now riding " + to.getName());
		    		}
		    	
		    	}else{
		    		player.sendMessage(ChatColor.RED + "You do not have permission to perform this command");
		    		System.out.println("[SwornRPG] " + player.getName() + " was denied access to a command");	
		    		}
		    	}
		    
		   if (label.equalsIgnoreCase("unride")){
			   if (PermissionInterface.checkPermission(player, this.plugin.adminRidePerm)) {
			   player.leaveVehicle();
			   
			   }else{
				   player.sendMessage(ChatColor.RED + "You do not have permission to perform this command");
				   System.out.println("[SwornRPG]" + player.getName() + " was denied access to a command");
			   }
		   }

			return true;
	  	}
}