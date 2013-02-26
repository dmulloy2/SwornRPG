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

public class CmdLevelr implements CommandExecutor
{
	
	public SwornRPG plugin;
	  public CmdLevelr(SwornRPG plugin)  
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
//				  sender.resetLevel();
				  sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have reset your level");
			  }
			  else
			  {
				  sender.sendMessage(plugin.mustbeplayer);
			  }
		  }
		  else if (args.length == 1)
		  {
			  Player target = Util.matchPlayer(args[0]);
//			  target.resetLevel();
			  sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have reset " + target.getName() + "'s level");
		  }
		  else
		  {
			  sender.sendMessage(plugin.invalidargs + "(/levelr [player])");
		  }
		  
		return true;
	  }
}