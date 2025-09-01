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
package net.dmulloy2.swornrpg.types;

import lombok.*;

import java.util.*;

import org.bukkit.Location;
import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Index;

import net.dmulloy2.swornapi.io.AbstractPlayerData;

/**
 * @author dmulloy2
 */

@Entity(
	indices = {
		@Index(fields = "totalxp", type = IndexType.NON_UNIQUE)
	}
)
@Getter @Setter
public class PlayerData extends AbstractPlayerData
{
	// ---- Experience
	private int playerxp;
	private int xpneeded;
	private int totalxp;
	private int level;

	private transient int herbalism;

	// ---- Abilities
	private transient boolean frenzyEnabled;
	private transient boolean superPickaxeEnabled;
	private transient boolean unlimitedAmmoEnabled;
	private transient long nextCooldownMessage;

	private Map<String, Long> cooldowns = new HashMap<>();

	private transient boolean frenzyWaiting;
	private transient boolean superPickaxeWaiting;

	private transient long frenzyReadyTime;
	private transient long superPickaxeReadyTime;

	private transient String itemName;

	// ---- Entity Fun
	private transient long rideWaitingTime;
	private transient boolean rideWaiting;

	// ---- Chairs
	private transient Location previousLocation;
	private transient boolean satRecently;

	// ---- Miscellaneous
	private String tag;
	private boolean deathCoordsEnabled;
	private transient long timeOfLastDeath;

	// ---- Marriage
	private String spouse;
	private transient Set<String> proposals = new HashSet<>();

	// ---- UUID Stuff
	private String lastKnownBy;

	public PlayerData()
	{
		super();
	}

	public PlayerData(Map<String, Object> args)
	{
		super(args);
	}

	/**
	 * Returns the xp needed to reach the next level.
	 * <p>
	 * Special case: this cannot return 0.
	 */
	public int getXpNeeded()
	{
		// Validate the data first
		if (xpneeded < 100)
			xpneeded = 100;

		return xpneeded;
	}

	/**
	 * Returns the player's level. Will return from 1 to max.
	 *
	 * @param max
	 *        - Maximum level for this action
	 */
	public final int getLevel(int max)
	{
		return level > max ? max : Math.max(level, 1);
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof PlayerData other && id.equals(other.id);
	}

	@Override
	public String toString()
	{
		return String.format("PlayerData[id=%s,name=%s]", id, lastKnownBy);
	}
}
