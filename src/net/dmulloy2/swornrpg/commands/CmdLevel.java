package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdLevel implements CommandExecutor
{	
	public SwornRPG plugin;
	public CmdLevel(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		if (args.length == 0)
		{
			if (sender instanceof Player)
			{
				plugin.getPlayerDataCache().save();
				final PlayerData data = plugin.getPlayerDataCache().getData(sender.getName());
				int xp = data.getPlayerxp();
				int level = xp/125;
				int nextlevel = (xp/125+1)*(125);
				int xptonext = nextlevel - xp;
				if (level < 1)
					level = 0;
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "You are level " + ChatColor.GREEN + level);
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "You are " + ChatColor.GREEN + xptonext + ChatColor.YELLOW + " xp away from level " + (level + 1));
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "(" + ChatColor.GREEN + xp + ChatColor.YELLOW + "/" + ChatColor.GREEN + nextlevel + ChatColor.YELLOW + ")");
			}
			else
			{
				sender.sendMessage(plugin.mustbeplayer);
			}
		}
		else if (args.length == 1)
		{
			Player target = Util.matchPlayer(args[0]);
			if (target == null)
			{
				sender.sendMessage(plugin.noplayer);
			}
			else
			{
				String targetp = target.getName();
				final PlayerData data = plugin.getPlayerDataCache().getData(targetp);
				int xp = data.getPlayerxp();
				int level = xp/125;
				int nextlevel = (xp/125+1)*(125);
				int xptonext = nextlevel - xp;
				if (level < 1)
					level = 0;
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + targetp + " is level " + ChatColor.GREEN + level);
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + targetp + " is " + ChatColor.GREEN + xptonext + ChatColor.YELLOW + " xp away from level " + (level + 1));
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "(" + ChatColor.GREEN + xp + ChatColor.YELLOW + "/" + ChatColor.GREEN + nextlevel + ChatColor.YELLOW + ")");
			}
		}
		else
		{
			sender.sendMessage(plugin.invalidargs + "(/level [player])");
		}
		
		return true;
	}
}