package net.dmulloy2.swornrpg.commands;

import java.util.HashMap;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdPropose implements CommandExecutor
{	
	public SwornRPG plugin;
	public HashMap<String, String> proposal;
	public CmdPropose(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		if (sender instanceof Player) 
		{
			final PlayerData data = plugin.getPlayerDataCache().getData(sender.getName());
			Player target = Bukkit.getPlayer(args[0]);
			if (target != null)
			{
				String targetp = target.getName();
				final PlayerData data1 = plugin.getPlayerDataCache().getData(targetp);
				if (args.length == 1)
				{
					if (data.getSpouse() != null)
					{
						sender.sendMessage(plugin.prefix + ChatColor.RED + "Polygamy is not allowed!");
					}
					else if (targetp.equals(sender.getName()))
					{
						sender.sendMessage(plugin.prefix + ChatColor.RED + "You cannot marry yourself");
					}
					else if (data1.getSpouse() != null)
					{
						sender.sendMessage(plugin.prefix + ChatColor.RED + targetp + " is already married");
					}
					else
					{
						plugin.proposal.put(targetp, sender.getName());
						sender.sendMessage(plugin.prefix + ChatColor.GREEN + "You have proposed to " + targetp);
						target.sendMessage(plugin.prefix + ChatColor.GREEN + sender.getName() + " Wishes to marry you. Type " + ChatColor.RED + "/marry " + ChatColor.GOLD + sender.getName() + ChatColor.GREEN + " to confirm");
					}
				}
				else
				{
					sender.sendMessage(plugin.invalidargs + ChatColor.RED + "(/marry [player])");
				}
			}
			else
			{
				sender.sendMessage(plugin.noplayer);
			}
		}
		else
		{
			sender.sendMessage(plugin.mustbeplayer);
		}
		
		return true;
	  }
}