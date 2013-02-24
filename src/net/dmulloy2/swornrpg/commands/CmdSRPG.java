package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.PermissionInterface;
import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdSRPG implements CommandExecutor
{
	public SwornRPG plugin;
	  public CmdSRPG (SwornRPG plugin)  
	  {
	    this.plugin = plugin;
	  }
	  
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	  {    
		  if (sender instanceof Player) 
		  {
			  sender = (Player) sender;
		  }
		  if(args.length == 0)
		  {
			  plugin.displayHelp(sender);
		  }
		  else if (args[0].equals("reload"))
		  {
			  if (PermissionInterface.checkPermission(sender, plugin.adminReloadPerm))
			  {
				  plugin.reloadConfig();
				  plugin.reloadtagsConfig();
				  sender.sendMessage(plugin.prefix + ChatColor.GREEN + "Configuration reloaded");
				  if (sender instanceof Player)
				  {
					  SwornRPG.outConsole("Configuration reloaded");
				  }
			  }
			  else
			  {
				  sender.sendMessage(plugin.prefix + ChatColor.RED + "You do not have permission to perform this command");
			  }
		  }
		  else if (args[0].equals("level"))
		  {
			  sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "This command has not been implimented yet");
		  }
		  else if (args[0].equals("levelr"))
		  {
			  if (PermissionInterface.checkPermission(sender, plugin.adminClearPerm)) 
			  {
				  sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "This command has not been implimented yet");
			  }
			  else
			  {
				  sender.sendMessage(plugin.prefix + ChatColor.RED + "You do not have permission to perform this command");
			  }
		  }
		  else if (args[0].equals("help"))
		  {
			  plugin.displayHelp(sender);	  
		  }
		  else
		  {
			  plugin.displayHelp(sender);
		  }
		  
		  return true;
	  }
}