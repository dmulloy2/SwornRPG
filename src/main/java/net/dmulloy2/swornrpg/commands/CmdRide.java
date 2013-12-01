package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class CmdRide extends SwornRPGCommand
{
	public CmdRide(SwornRPG plugin)
	{
		super(plugin);
		this.name = "ride";
		this.optionalArgs.add("player");
		this.description = "Get on a player's head";
		this.permission = Permission.RIDE;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		if (args.length == 0)
		{
			PlayerData data = getPlayerData(player);
			data.setRideWaitingTime(System.currentTimeMillis());
			data.setRideWaiting(true);

			sendpMessage(getMessage("ride_entity"));
			return;
		}

		final Player target = Util.matchPlayer(args[0]);
		if (target == null)
		{
			err(getMessage("player_not_found"), args[0]);
			return;
		}

		if (target.getPassenger() != null)
		{
			err(getMessage("ride_vehicle"));
			return;
		}

		if (target.getVehicle() != null)
		{
			err(getMessage("ride_passenger"));
			return;
		}

		player.teleport(target);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				target.setPassenger(player);

				sendpMessage(getMessage("ride_now_riding"), target.getName());
			}
		}.runTaskLater(plugin, 40L);
	}
}