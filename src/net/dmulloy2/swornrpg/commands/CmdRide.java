package net.dmulloy2.swornrpg.commands;

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

public class CmdRide implements CommandExecutor
{
	
	public SwornRPG plugin;
	  public CmdRide(SwornRPG plugin)  
	  {
	    this.plugin = plugin;
	  }
	  
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	  {    
		    Player player = null;
		    if (sender instanceof Player) 
		    {
		    	player = (Player) sender;
		    }
		    if(args.length < 1)
		    {
		    	player.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.RED + "Invalid arguments count");
		    	player.leaveVehicle();
		    }
		    else
		    {
		    	Player target = Util.matchPlayer(args[0]);
		    	((Player)sender).teleport(target);
		    	target.setPassenger(player);
		    	player.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.YELLOW + "You are now riding " + target.getName());
		    }
		    
		    return true;
	  }
}