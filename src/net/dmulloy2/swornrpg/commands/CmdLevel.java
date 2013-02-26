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
 * Unimplimented. Plan to add functionality.
 */

public class CmdLevel implements CommandExecutor
{
	
	public SwornRPG plugin;
	  public CmdLevel(SwornRPG plugin)  
	  {
	    this.plugin = plugin;
	  }
	  
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	  {    
//		  Player player = null;
		  sender = (Player) sender;
		  if (args.length == 0)
		  {
			  if (sender instanceof Player)
			  {
				  int level = 1;
//				  int level = sender.getLevel();
				  sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "Your level is " + level);
			  }
			  else
			  {
				  sender.sendMessage(plugin.mustbeplayer);
			  }
		  }
		  else if (args.length == 1)
		  {
			  Player target = Util.matchPlayer(args[0]);
			  int targetlevel = 1;
//			  int targetlevel = target.getLevel();
			  sender.sendMessage(plugin.prefix + ChatColor.YELLOW + target.getName() + "'s level is " + targetlevel);
		  }
		  else
		  {
			  sender.sendMessage(plugin.invalidargs + "(/levelr [player])");
		  }
		  
		return true;
	  }
}