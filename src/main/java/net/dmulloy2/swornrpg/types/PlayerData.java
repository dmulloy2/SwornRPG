package net.dmulloy2.swornrpg.types;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * @author dmulloy2
 */

@Data
public class PlayerData implements ConfigurationSerializable
{
	// ---- Misc --- //
	private String spouse;
	private String tag;

	private boolean deathCoordsEnabled;

	@Deprecated
	private boolean deathbookdisabled;

	private transient long timeOfLastDeath;

	// ---- XP Stuff ---- //
	private int playerxp;
	private int oldlevel;
	private int xpneeded;
	private int level;
	private int totalxp;

	private transient int concurrentHerbalism;

	// ---- Abilities ---- //
	private transient boolean frenzyEnabled;
	private transient boolean superPickaxeEnabled;
	private transient boolean unlimitedAmmoEnabled;

	private boolean frenzyCooldownEnabled;
	private boolean superPickaxeCooldownEnabled;
	private boolean unlimitedAmmoCooldownEnabled;

	private int frenzyCooldownTime;
	private int superPickaxeCooldownTime;
	private int unlimitedAmmoCooldownTime;

	private transient boolean frenzyWaiting;
	private transient boolean superPickaxeWaiting;
	private transient boolean unlimitedAmmoWaiting;

	private transient long frenzyReadyTime;
	private transient long superPickaxeReadyTime;
	private transient long unlimitedAmmoReadyTime;

	// Used for ability cleanup
	private transient String itemName;

	@Setter(AccessLevel.NONE)
	private Map<String, Object> data = new HashMap<String, Object>();

	public PlayerData()
	{
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
			}
		}
	}

	/**
	 * Any data put into this map needs to be inherently serializable, either
	 * using ConfigurationSerializable or being a java primitive.
	 * 
	 * @param key
	 *        - Key to store the object under
	 * @param object
	 *        - Object to store.
	 */
	public void putData(String key, Object object)
	{
		data.put(key, object);
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
					if (!((Collection) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(String.class))
				{
					if (((String) field.get(this)) != null)
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(Map.class))
				{

					if (!((Map) field.get(this)).isEmpty())
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
			}
		}

		return data;
	}

	// Special case, since this returning 0 
	// results in an arithmatic exception
	public int getXpNeeded()
	{
		if (xpneeded == 0)
		{
			this.xpneeded = 100;
		}

		return xpneeded;
	}
}