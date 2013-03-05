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

public class CmdUnride implements CommandExecutor
{
	public SwornRPG plugin;
	public CmdUnride(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		Player player = null;
		if (sender instanceof Player) 
		{
			player = (Player) sender;
			if(args.length > 0)
			{
				player.sendMessage(plugin.invalidargs + "(/unride)");
			}
			else
			{
				if (player.getVehicle() != null)
				{
					player.leaveVehicle();
				}
				else
				{
					player.sendMessage(plugin.prefix + ChatColor.RED + "Error, you are not riding anyone");
				}
			}
		}
		else
		{
			sender.sendMessage(plugin.mustbeplayer);
		}
		
		return true;
	}
}