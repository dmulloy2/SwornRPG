package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
		    	if (args.length == 0)
		    	{
		    		player.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.RED + "Invalid arguments count (/ride <player>)");
		    		player.leaveVehicle();
		    	}
		    	else
		    	{
		    		final Player target = Util.matchPlayer(args[0]);
		    		final Player player1 = (Player) sender;
		    		Location targetLocation = target.getLocation();
		    		player1.teleport(targetLocation);
		    			
		    		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		    		{
		    			@Override
		    			public void run()
		    			{
		    				target.setPassenger(player1);
		    				player1.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.YELLOW + "You are now riding " + target.getName());
		    			}				
		    		},20);
		    	}
		    }
		    else
		    {
		    	sender.sendMessage(ChatColor.RED + "Error: You must be a player to use this command");
		    }
		    
		    return true;
	  }
}