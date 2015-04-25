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
package net.dmulloy2.swornrpg.integration;

import net.dmulloy2.integration.DependencyProvider;
import net.dmulloy2.swornnations.SwornNations;
import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

/**
 * @author dmulloy2
 */

public class SwornNationsHandler extends DependencyProvider<SwornNations>
{
	public SwornNationsHandler(SwornRPG plugin)
	{
		super(plugin, "SwornNations");
	}

	public final boolean isApplicable(Player player, boolean safeZoneCheck)
	{
		return isApplicable(player.getLocation(), safeZoneCheck);
	}

	public final boolean isApplicable(Location location, boolean safeZoneCheck)
	{
		return safeZoneCheck ? isSafeZone(location) || isWarZone(location) : isWarZone(location);
	}

	private final boolean isWarZone(Location location)
	{
		if (! isEnabled())
			return false;

		try
		{
			Faction fac = Board.getAbsoluteFactionAt(new FLocation(location));
			return fac.isWarZone();
		}
		catch (Throwable ex)
		{
			//
		}

		return false;
	}

	private final boolean isSafeZone(Location location)
	{
		if (! isEnabled())
			return false;

		try
		{
			Faction fac = Board.getAbsoluteFactionAt(new FLocation(location));
			return fac.isSafeZone();
		}
		catch (Throwable ex)
		{
			//
		}

		return false;
	}
}
