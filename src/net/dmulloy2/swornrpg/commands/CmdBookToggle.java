package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdBookToggle implements CommandExecutor
{
	public SwornRPG plugin;
	public CmdBookToggle(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		if (sender instanceof Player) 
		{
			final PlayerData data = plugin.getPlayerDataCache().getData(sender.getName());
			if (args.length == 0)
			{
				if (data.isDeathbookdisabled())
				{
					data.setDeathbookdisabled(false);
					plugin.getPlayerDataCache().save();
					sender.sendMessage(plugin.prefix + ChatColor.RED + "Death coordinate messages enabled");
				}
				else
				{
					data.setDeathbookdisabled(true);
					plugin.getPlayerDataCache().save();
					sender.sendMessage(plugin.prefix + ChatColor.RED + "Death coordinate messages disabled");
				}
			}
			else if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("enabled"))
				{
					data.setDeathbookdisabled(false);
					plugin.getPlayerDataCache().save();
					sender.sendMessage(plugin.prefix + ChatColor.RED + "Death coordinate messages enabled");
				}
				else if (args[0].equalsIgnoreCase("disabled"))
				{
					data.setDeathbookdisabled(true);
					plugin.getPlayerDataCache().save();
					sender.sendMessage(plugin.prefix + ChatColor.RED + "Death coordinate messages disabled");
				}
				else
				{
					sender.sendMessage(plugin.invalidargs + ChatColor.RED + "(/deathbook [enabled/disabled])");
				}
			}
			else
			{
				sender.sendMessage(plugin.invalidargs + ChatColor.RED + "(/deathbook [enabled/disabled])");
			}				
		}
		else
		{
			sender.sendMessage(plugin.mustbeplayer);
		}
		
		return true;
	}
}