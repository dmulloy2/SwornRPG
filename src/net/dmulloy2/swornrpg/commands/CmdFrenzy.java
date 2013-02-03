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
 * Unimplimented. Plan to add functionality.
 */

@SuppressWarnings("unused")
public class CmdFrenzy implements CommandExecutor{
	
	public SwornRPG plugin;
	  public CmdFrenzy(SwornRPG plugin)  {
	    this.plugin = plugin;
	  }
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
		    Player player = null;
		    if (sender instanceof Player) {
		      player = (Player) sender;
		    }
		    player.sendMessage(ChatColor.GOLD + "[SwornRPG]" + ChatColor.YELLOW + " Now entering BeastMode");
			return false;
	  }
}