package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

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
		if (target == null)
		{
			err(getMessage("noplayer"));
			return;
		}
		
		if (target.getPassenger() != null)
		{
			err(getMessage("ride_passenger"));
			return;
		}
		
		if (target.getVehicle() != null)
		{
			err(getMessage("ride_vehicle"));
			return;
		}
		
		class RideTask extends BukkitRunnable
		{
			@Override
			public void run()
			{
				target.setPassenger(player);
				
				sendpMessage(plugin.getMessage("now_riding"), target.getName());
			}
		}
		
		new RideTask().runTaskLater(plugin, 40L);
	}
}