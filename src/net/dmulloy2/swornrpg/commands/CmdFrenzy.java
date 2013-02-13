package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 * Unimplimented. Plan to add functionality.
 */

public class CmdFrenzy implements CommandExecutor
{
	
	public SwornRPG plugin;
	  public CmdFrenzy(SwornRPG plugin)  
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
		    if (args.length < 0)
		    {
		    	player.sendMessage(ChatColor.GOLD + "[SwornRPG]" + ChatColor.YELLOW + " This command has not been implimented yet");
		    }
		    else
		    {
		    	player.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.RED + "Invalid arguments count");
		    }
		    return true;
	  }
}