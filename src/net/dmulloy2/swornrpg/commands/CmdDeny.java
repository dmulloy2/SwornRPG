package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdDeny implements CommandExecutor
{
	public SwornRPG plugin;
	public CmdDeny(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		if (sender instanceof Player) 
		{
			if (plugin.proposal.containsKey(sender.getName()))
			{
				Player target = Bukkit.getServer().getPlayer((String)plugin.proposal.get(sender.getName()));
				if (target != null)
				{
					String targetp = target.getName();
					String senderp = sender.getName();
					plugin.proposal.remove(senderp);
					plugin.proposal.remove(targetp);
					sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have denied " + targetp + "'s proposal");
					target.sendMessage(plugin.prefix + ChatColor.RED + senderp + " does not wish to marry at this time");
				}
				else
				{
					sender.sendMessage(plugin.noplayer);
				}
			}
			else
			{
				sender.sendMessage(plugin.prefix + ChatColor.RED + "Error, you do not have a proposal");
			}
		}
		else
		{
			sender.sendMessage(plugin.mustbeplayer);
		}
		
		return true;
	}
}