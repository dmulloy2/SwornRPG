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
				final PlayerData data = plugin.getPlayerDataCache().getData(sender.getName());
				int level = data.getLevel();
				int nextlevel = level+1;
				int totalxp = data.getTotalxp();
				int totalxpneeded = (Math.abs(data.getTotalxp()) + data.getXpneeded());
				int xptonext = (data.getXpneeded() - data.getPlayerxp());
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "You are level " + ChatColor.GREEN + level);
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "You are " + ChatColor.GREEN + xptonext + ChatColor.YELLOW + " xp away from level " + ChatColor.GREEN + nextlevel);
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "(" + ChatColor.GREEN + totalxp + ChatColor.YELLOW + "/" + ChatColor.GREEN + totalxpneeded + ChatColor.YELLOW + ")");
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
				int level = data.getLevel();
				int nextlevel = level+1;
				int totalxp = data.getTotalxp();
				int totalxpneeded = (Math.abs(data.getTotalxp()) + data.getXpneeded());
				int xptonext = (data.getXpneeded() - data.getPlayerxp());
				String name = null;
				String title = null;
				String senderp = sender.getName();
				if (targetp == senderp)
				{
					name = "You are";
					title = senderp;
				}
				else
				{
					name = targetp + " is";
					title = targetp;
				}
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "Level info for: " + title);
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + name + "level " + ChatColor.GREEN + level);
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + name + "is " + ChatColor.GREEN + xptonext + ChatColor.YELLOW + " xp away from level " + ChatColor.GREEN + nextlevel);
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "(" + ChatColor.GREEN + totalxp + ChatColor.YELLOW + "/" + ChatColor.GREEN + totalxpneeded + ChatColor.YELLOW + ")");
			}
		}
		else
		{
			sender.sendMessage(plugin.invalidargs + "(/level [player])");
		}
		
		return true;
	}
}