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

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;

/**
 * @author dmulloy2
 */

public class CmdUnride extends SwornRPGCommand
{
	public CmdUnride(SwornRPG plugin)
	{
		super(plugin);
		this.name = "unride";
		this.description = "Get off of a player's head";
		this.permission = Permission.UNRIDE;

		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (! player.isInsideVehicle())
		{
			err(plugin.getMessage("not_riding"));
			return;
		}

		Entity vehicle = player.getVehicle();
		
		player.teleport(vehicle.getLocation().add(0.5D, 1.0D, 0.5D));
		
		if (vehicle instanceof Arrow)
		{
			vehicle.remove();
		}
		else
		{
			player.leaveVehicle();
		}
		
		sendMessage(getMessage("unride_successful"));
	}
}
