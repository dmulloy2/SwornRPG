package net.dmulloy2.swornrpg.types;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

	private transient int concurrentHerbalism;

	// ---- Abilities
	private transient boolean frenzyEnabled;
	private transient boolean superPickaxeEnabled;
	private transient boolean unlimitedAmmoEnabled;

	private boolean frenzyCooldownEnabled;
	private boolean superPickaxeCooldownEnabled;
	private boolean unlimitedAmmoCooldownEnabled;

	private long frenzyCooldownTime;
	private long superPickaxeCooldownTime;
	private long unlimitedAmmoCooldownTime;

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

	// ---- Miscellaneous
	private String tag;
	private boolean deathCoordsEnabled;
	private transient long timeOfLastDeath;

	// ---- Marriage
	private String spouse;
	private transient Set<String> proposals = new HashSet<String>();

	// ---- UUID Stuff
	private String lastKnownBy;
	private String uniqueId;

	public PlayerData()
	{
		//
	}

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
			}
			catch (Exception e)
			{
				//
			}
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map<String, Object> serialize()
	{
		Map<String, Object> data = new HashMap<String, Object>();

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
					if (! ((Collection) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(String.class))
				{
					if (((String) field.get(this)) != null)
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(Map.class))
				{
					if (! ((Map) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				}
				else
				{
					if (field.get(this) != null)
						data.put(field.getName(), field.get(this));
				}

				field.setAccessible(accessible);
			}
			catch (Exception e)
			{
				//
			}
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