package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdEject implements CommandExecutor
{
	
	public SwornRPG plugin;
	  public CmdEject(SwornRPG plugin)  
	  {
	    this.plugin = plugin;
	  }
	  
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	  {    
		    Player player = null;
		    if (sender instanceof Player) 
		    {
		    	player = (Player) sender;
		    	if(args.length > 0)
		    	{
		    		player.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.RED + "Invalid arguments count (/eject)");
		    	}
		    	else
		    	{
		    		player.eject();
		    	}
		    }
		    else
		    {
		    	sender.sendMessage(ChatColor.RED + "Error: You must be a player to use this command");
		    }
		    
			return true;
	  }
}