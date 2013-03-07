package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdDivorce implements CommandExecutor
{
	public SwornRPG plugin;
	public CmdDivorce(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		if (sender instanceof Player) 
		{
			if (args.length == 0)
			{
				final PlayerData data = plugin.getPlayerDataCache().getData(sender.getName());
				String targetp = data.getSpouse();
				final PlayerData data1 = plugin.getPlayerDataCache().getData(targetp);
				if (targetp != null)
				{
					data.setSpouse(null);
					data1.setSpouse(null);
					sender.sendMessage(plugin.prefix + ChatColor.RED + "You have divorced " + targetp);
					Bukkit.getServer().broadcastMessage(plugin.prefix + ChatColor.RED + sender.getName() + " has divorced " + targetp);
					Player target = Util.matchPlayer(targetp);
					plugin.getPlayerDataCache().save();
					if (target != null)
					{
						target.sendMessage(plugin.prefix + ChatColor.RED + "You are now single");
					}
				}
				else
				{
					sender.sendMessage(plugin.prefix + ChatColor.RED + "Error, you are not married");
				}
			}
			else
			{
				sender.sendMessage(plugin.invalidargs + ChatColor.RED + "(/divorce)");
			}
		}
		else
		{
			sender.sendMessage(plugin.mustbeplayer);
		}
		
		return true;
	}
}