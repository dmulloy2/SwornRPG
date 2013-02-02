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

public class CmdDmu implements CommandExecutor{
	
	public SwornRPG plugin;
	  public CmdDmu(SwornRPG plugin)  {
	    this.plugin = plugin;

	  }
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
		    Player player = null;
		    if (sender instanceof Player) {
		      player = (Player) sender;
		    }
	        if (player.getName().contains("dmulloy2")) {
	        	int amt = args.length;
	        	String str = "";
	        	for (int i = 0; i < amt; i++) {
	        		str = str + args[i] + " ";
	        	}
	        	this.plugin.sendMessageAll(ChatColor.AQUA + "[" + ChatColor.DARK_GRAY + "dmulloy" + ChatColor.AQUA + "]: " + ChatColor.AQUA + str);
	        }else{
	        	player.sendMessage(ChatColor.RED + "Only dmulloy can use this command");
	        	System.out.println("[SwornRPG] " + player.getName() + " tried to use dmulloy's command");
	        }
			return false;
	  }
}