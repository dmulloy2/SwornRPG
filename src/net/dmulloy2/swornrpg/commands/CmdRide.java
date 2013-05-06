package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class CmdRide extends SwornRPGCommand
{
	public CmdRide (SwornRPG plugin)
	{
		super(plugin);
		this.name = "ride";
		this.description = "Get on a player's head!";
		this.requiredArgs.add("player");
		this.permission = PermissionType.CMD_RIDE.permission;
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		final Player target = Util.matchPlayer(args[0]);
		if (target != null)
		{
			Location targetLocation = target.getLocation();
			player.teleport(targetLocation);	
			class PassengerTask extends BukkitRunnable
			{
				@Override
				public void run()
				{
					target.setPassenger(player);
					sendpMessage(plugin.getMessage("now_riding"), target.getName());
					PlayerData data = getPlayerData(player);
					data.setRiding(true);
					PlayerData data1 = getPlayerData(target);
					data1.setVehicle(true);
				}	
			}
			new PassengerTask().runTaskLater(plugin, 20);
		}
		else
		{
			sendpMessage(plugin.getMessage("noplayer"));
		}
	}
}