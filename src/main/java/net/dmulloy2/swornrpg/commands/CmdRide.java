/**
 * SwornRPG - a Bukkit plugin
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.Util;

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
		this.addOptionalArg("player");
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

		/* if (target.getPassenger() != null)
		{
			err(getMessage("ride_vehicle"));
			return;
		} */

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
				target.addPassenger(player);

				sendpMessage(getMessage("ride_now_riding"), target.getName());
			}
		}.runTaskLater(plugin, 40L);
	}
}
