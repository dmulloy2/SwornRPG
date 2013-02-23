package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author dmulloy2
 */

public class CmdASay implements CommandExecutor
{
	public SwornRPG plugin;
	  public CmdASay(SwornRPG plugin)  
	  {
	    this.plugin = plugin;
	  }
	  
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	  {    
		  if (args.length > 0)
		  {
			  int amt = args.length;
			  String str = "";
			  for (int i = 0; i < amt; i++) 
			  {
				  str = str + args[i] + " ";
			  }
			  this.plugin.sendMessageAll(ChatColor.DARK_PURPLE + "[" + ChatColor.DARK_RED + "Admin" + ChatColor.DARK_PURPLE + "]: " + str);
			  System.out.println("[AdminSay] " + str);
		  }
		  else
		  {
			  sender.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.RED + "Invalid arguments count (/asay <message>)");
		  }
		  return true;
	  }
}