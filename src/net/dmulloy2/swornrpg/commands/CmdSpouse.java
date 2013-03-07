package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdSpouse implements CommandExecutor
{
	public SwornRPG plugin;
	public CmdSpouse(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		if (sender instanceof Player) 
		{
			sender = (Player) sender;
		}
		if (args.length == 1)
		{
			Player target = Util.matchPlayer(args[0]);
			if (target != null)
			{
				String targetp = target.getName();
				final PlayerData data = plugin.getPlayerDataCache().getData(targetp);
				String spouse = data.getSpouse();
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + targetp + " is married to " + spouse);
			}
			else
			{
				sender.sendMessage(plugin.noplayer);
			}
		}
		else if (args.length == 0)
		{
			if (sender instanceof Player)
			{
				final PlayerData data = plugin.getPlayerDataCache().getData(sender.getName());
				String spouse = data.getSpouse();
				if (spouse != null)
				{
					Player spousep = Util.matchPlayer(data.getSpouse());
					sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "You are married to " + spouse);
					if (spousep != null)
					{
						double x = (int) Math.floor(spousep.getLocation().getX());
						double y = (int) Math.floor(spousep.getLocation().getY());
						double z = (int) Math.floor(spousep.getLocation().getZ());
						World world = spousep.getLocation().getWorld();
						sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "Their coordinates are " + x + ", " + y + ", " + z + " in " + world.getName());
					}
					else
					{
						sender.sendMessage(plugin.prefix + ChatColor.YELLOW + spouse + " is currently offline");
					}
				
				}
				else
				{
					sender.sendMessage(plugin.prefix + ChatColor.RED + "You are not married");
				}
			}
			else
			{
				sender.sendMessage(plugin.mustbeplayer);
			}
		}
		else
		{
			sender.sendMessage(plugin.invalidargs + ChatColor.RED + "(/spouse [player])");
		}
		
		return true;
	  }
}