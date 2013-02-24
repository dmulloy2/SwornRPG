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

public class CmdResetTag implements CommandExecutor
{
	
	public SwornRPG plugin;
	  public CmdResetTag(SwornRPG plugin)  
	  {
	    this.plugin = plugin;
	  }
	  
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	  {
		  if (sender instanceof Player)
		  {
			  if (args.length == 0) 
			  {
				  this.plugin.removeNameChange(sender.getName());
				  sender.sendMessage(plugin.prefix + ChatColor.GREEN + "You have reset your tag");
			  }
			  else if (args.length == 1)
			  {
				  if (args[0].length() > 16) 
				  {
					  sender.sendMessage(plugin.prefix + ChatColor.RED + "That username is too large to be a players!");
				  }
				  else 
				  {
					  Player target = Util.matchPlayer(args[0]);
					  this.plugin.removeNameChange(target.getName());
					  sender.sendMessage(plugin.prefix + ChatColor.GREEN + "You have reset " + target.getName() + "'s tag");
					  target.sendMessage(plugin.prefix + ChatColor.RED + "Your tag has been reset");
				  }
			  }
			  else
			  {
				  sender.sendMessage(plugin.invalidargs + "(/tagr [player])");
			  }
		  }
		  else
		  {
			  sender.sendMessage(plugin.mustbeplayer);
		  }
		  
      return true;
  }
}