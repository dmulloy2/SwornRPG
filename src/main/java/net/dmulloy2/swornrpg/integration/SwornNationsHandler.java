/**
 * (c) 2014 dmulloy2
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