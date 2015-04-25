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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Data;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * @author dmulloy2
 */

@Data
public class PlayerData implements ConfigurationSerializable
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

	public PlayerData() { }

	public PlayerData(Map<String, Object> args)
	{
		for (Entry<String, Object> entry : args.entrySet())
		{
			try
			{
				for (Field field : getClass().getDeclaredFields())
				{
					if (field.getName().equals(entry.getKey()))
					{
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						field.set(this, entry.getValue());
						field.setAccessible(accessible);
					}
				}
			} catch (Throwable ex) { }
		}
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> data = new LinkedHashMap<>();

		for (Field field : getClass().getDeclaredFields())
		{
			if (Modifier.isTransient(field.getModifiers()))
				continue;

			try
			{
				boolean accessible = field.isAccessible();

				field.setAccessible(true);

				if (field.getType().equals(Integer.TYPE))
				{
					if (field.getInt(this) != 0)
						data.put(field.getName(), field.getInt(this));
				}
				else if (field.getType().equals(Long.TYPE))
				{
					if (field.getLong(this) != 0)
						data.put(field.getName(), field.getLong(this));
				}
				else if (field.getType().equals(Boolean.TYPE))
				{
					if (field.getBoolean(this))
						data.put(field.getName(), field.getBoolean(this));
				}
				else if (field.getType().isAssignableFrom(Collection.class))
				{
					if (! ((Collection<?>) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(String.class))
				{
					if (((String) field.get(this)) != null)
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(Map.class))
				{
					if (! ((Map<?, ?>) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				}
				else
				{
					if (field.get(this) != null)
						data.put(field.getName(), field.get(this));
				}

				field.setAccessible(accessible);
			} catch (Throwable ex) { }
		}

		return data;
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
		return level > max ? max : level < 1 ? 1 : level;
	}
}
