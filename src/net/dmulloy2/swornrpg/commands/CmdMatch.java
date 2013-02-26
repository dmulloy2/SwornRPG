package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdMatch implements CommandExecutor
{
	
	public SwornRPG plugin;
	  public CmdMatch(SwornRPG plugin)  
	  {
	    this.plugin = plugin;
	  }
	  
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	  {
//		  Player player = null;
		  if (sender instanceof Player)
		  {
			  sender = (Player) sender;
		  }
		  if (args.length == 0)
		  {
			  sender.sendMessage(plugin.invalidargs + "(/match <player>)");
		  }
		  else
		  {
			  Player match = Util.matchPlayer(args[0]);
			  if (match != null)
			  {
				  sender.sendMessage(plugin.prefix + ChatColor.GREEN + "Match found! '" + match.getName() + "'");;
			  }
			  else
			  {
				  OfflinePlayer offlinematch = Util.matchOfflinePlayer(args[0]);
				  if (offlinematch != null)
				  {
					  sender.sendMessage(plugin.prefix + ChatColor.GREEN + "Match found! '" + offlinematch.getName() + "'");
				  }
				  else
				  {
					  sender.sendMessage(plugin.prefix + ChatColor.RED + "Error, no match was found for '" + args[0] + "'");
				  }
			  }
		  }
		  
		return true;
	  }

}