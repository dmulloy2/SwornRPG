package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdStandup implements CommandExecutor
{
	public SwornRPG plugin;
	public CmdStandup(SwornRPG plugin)  
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
				player.sendMessage(plugin.invalidargs + "(/standup)");
			}
			else
			{
				PlayerData data = plugin.getPlayerDataCache().getData(player);
				if (data.isSitting())
				{
					Entity vehicle = player.getVehicle();
					if (vehicle instanceof Arrow)
					{
						player.leaveVehicle();
						vehicle.remove();
						player.teleport(vehicle.getLocation().add(0, 1, 0));
						data.setSitting(false);
					}
				}
			}
		}
		return true;
	}
}