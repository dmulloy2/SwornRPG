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

public class CmdTag implements CommandExecutor
{
	
	public SwornRPG plugin;
	  public CmdTag(SwornRPG plugin)  
	  {
	    this.plugin = plugin;
	  }
	  
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	  {
		  if (sender instanceof Player)
		  {
			  if (args.length == 2) 
			  {
				  if (sender.hasPermission("srpg.othertag"))
				  {
					  if (args[1].length() > 2)
					  {
						  sender.sendMessage(plugin.invalidargs + "(/tag [player] + <colorcode>)");
					  }
					  else 
					  {
						  Player target = Util.matchPlayer(args[0]);
						  this.plugin.addNameChange(target.getName(), args[1] + target.getName());
						  sender.sendMessage(plugin.prefix + ChatColor.GREEN + "Changed tag for " + args[0] + "!");
						  target.sendMessage(plugin.prefix + ChatColor.GREEN + "Your tag is now '" + args[1] + target.getName() + "'");
					  }
				  }
				  else
				  {
					  sender.sendMessage(plugin.prefix + ChatColor.RED + "Error, you do not have permission to change other players' tags");
				  }
			  }
			  else if (args.length == 1)
			  {
				  if (args[0].length() > 2)
				  {
					  sender.sendMessage(plugin.invalidargs + "(/tag <colorcode>)");
				  }
				  else
				  {
					  this.plugin.addNameChange(sender.getName(), args[0] + sender.getName());
					  sender.sendMessage(plugin.prefix + ChatColor.GREEN + "You have successfully changed your tag to '" + args[0] + sender.getName() + "'");
				  }
			  }
			  else
			  {
				  sender.sendMessage(plugin.invalidargs + ChatColor.RED + "(/tag [player] <colorcode>)");
			  }
		  }
		  else
		  {
			  sender.sendMessage(plugin.mustbeplayer);
		  }
		  
		  return true;
	  }
}
