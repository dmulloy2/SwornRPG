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
package net.dmulloy2.swornrpg;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.commands.CmdHelp;
import net.dmulloy2.swornapi.handlers.CommandHandler;
import net.dmulloy2.swornapi.handlers.LogHandler;
import net.dmulloy2.swornapi.handlers.PermissionHandler;
import net.dmulloy2.swornapi.handlers.ResourceHandler;
import net.dmulloy2.swornapi.types.Reloadable;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.MaterialUtil;
import net.dmulloy2.swornapi.util.NumberUtil;
import net.dmulloy2.swornapi.util.Util;
import net.dmulloy2.swornrpg.commands.*;
import net.dmulloy2.swornrpg.handlers.AbilityHandler;
import net.dmulloy2.swornrpg.handlers.ExperienceHandler;
import net.dmulloy2.swornrpg.handlers.HealthBarHandler;
import net.dmulloy2.swornrpg.integration.EssentialsHandler;
import net.dmulloy2.swornrpg.integration.SwornNationsHandler;
import net.dmulloy2.swornrpg.integration.VaultHandler;
import net.dmulloy2.swornrpg.io.PlayerDataCache;
import net.dmulloy2.swornrpg.listeners.BlockListener;
import net.dmulloy2.swornrpg.listeners.EntityListener;
import net.dmulloy2.swornrpg.listeners.PlayerListener;
import net.dmulloy2.swornrpg.modules.ModuleHandler;
import net.dmulloy2.swornrpg.types.BlockDrop;
import net.dmulloy2.swornrpg.types.PlayerData;

/**
 * @author dmulloy2
 */

public class SwornRPG extends SwornPlugin
{
	// Handlers
	private @Getter ExperienceHandler experienceHandler;
	private @Getter HealthBarHandler healthBarHandler;
	private @Getter ResourceHandler resourceHandler;
	private @Getter AbilityHandler abilityHandler;
	private @Getter ModuleHandler moduleHandler;

	// Data cache
	private @Getter PlayerDataCache playerDataCache;

	// Integration
	private @Getter SwornNationsHandler swornNationsHandler;
	private @Getter EssentialsHandler essentialsHandler;
	private @Getter VaultHandler vaultHandler;

	private @Getter Map<String, Map<Material, Integer>> salvageRef;
	private @Getter Map<Material, List<BlockDrop>> blockDropsMap;
	private @Getter Map<Integer, List<BlockDrop>> fishDropsMap;

	private List<Reloadable> listeners;

	// Global prefix
	private final @Getter String prefix = FormatUtil.format("&3[&eSwornRPG&3]&e ");

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();

		// Register log and resource handlers
		logHandler = new LogHandler(this);
		resourceHandler = new ResourceHandler(this);

		// Initialize variables
		salvageRef = new HashMap<>();
		blockDropsMap = new HashMap<>();
		fishDropsMap = new HashMap<>();

		// Configuration
		File conf = new File(getDataFolder(), "config.yml");
		if (! conf.exists())
		{
			log(getMessage("log_config_create"));
			saveDefaultConfig();
		}
		else
		{
			if (getConfig().get("frenzy.effects", null) == null)
			{
				conf.renameTo(new File(getDataFolder(), "oldConfig.yml"));
				log(getMessage("log_config_outdated"));
				saveDefaultConfig();
			}
		}

		reloadConfig();

		// Delete legacy messages file
		File messages = new File(getDataFolder(), "messages.properties");
		if (messages.exists())
			messages.delete();

		// Register the other handlers
		permissionHandler = new PermissionHandler("srpg");
		experienceHandler = new ExperienceHandler(this);
		healthBarHandler = new HealthBarHandler(this);
		abilityHandler = new AbilityHandler(this);
		commandHandler = new CommandHandler(this);

		// Update block maps
		updateBlockDrops();
		updateFishDrops();

		// Salvaging
		updateSalvageRef();

		// Register prefixed commands
		commandHandler.setCommandPrefix("srpg");
		commandHandler.registerPrefixedCommand(new CmdHelp(this));
		commandHandler.registerPrefixedCommand(new CmdLeaderboard(this));
		commandHandler.registerPrefixedCommand(new CmdVersion(this));
		commandHandler.registerPrefixedCommand(new CmdReload(this));

		// Register non-prefixed commands
		commandHandler.registerCommand(new CmdAbilities(this));
		commandHandler.registerCommand(new CmdAddxp(this));
		commandHandler.registerCommand(new CmdAdminChat(this));
		commandHandler.registerCommand(new CmdAdminSay(this));
		commandHandler.registerCommand(new CmdCoordsToggle(this));
		commandHandler.registerCommand(new CmdDeny(this));
		commandHandler.registerCommand(new CmdDivorce(this));
		commandHandler.registerCommand(new CmdEject(this));
		commandHandler.registerCommand(new CmdFrenzy(this));
		commandHandler.registerCommand(new CmdHat(this));
		commandHandler.registerCommand(new CmdHighCouncil(this));
		commandHandler.registerCommand(new CmdLevel(this));
		commandHandler.registerCommand(new CmdLore(this));
		commandHandler.registerCommand(new CmdMarry(this));
		commandHandler.registerCommand(new CmdName(this));
		commandHandler.registerCommand(new CmdPropose(this));
		commandHandler.registerCommand(new CmdResetLevel(this));
		commandHandler.registerCommand(new CmdRide(this));
		commandHandler.registerCommand(new CmdSitdown(this));
		commandHandler.registerCommand(new CmdSpouse(this));
		commandHandler.registerCommand(new CmdStaffList(this));
		commandHandler.registerCommand(new CmdStandup(this));
		commandHandler.registerCommand(new CmdSuperPickaxe(this));
		commandHandler.registerCommand(new CmdUnride(this));
		commandHandler.registerCommand(new CmdUnlimitedAmmo(this));

		// Register listeners
		listeners = new ArrayList<>();
		registerListener(new PlayerListener(this));
		registerListener(new EntityListener(this));
		registerListener(new BlockListener(this));
		registerSwornGunsListener();

		// Register modules
		moduleHandler = new ModuleHandler(this);

		// Integration
		setupIntegration();

		playerDataCache = new PlayerDataCache(this);

		// Deploy auto save task
		if (getConfig().getBoolean("autoSave.enabled"))
		{
			int interval = 20 * 60 * getConfig().getInt("autoSave.interval");

			class AutoSaveTask extends BukkitRunnable
			{
				@Override
				public void run()
				{
					// Save and cleanup
					playerDataCache.save();
					playerDataCache.purgeCache();
				}
			}

			new AutoSaveTask().runTaskTimerAsynchronously(this, interval, interval);
		}

		// Cooldowns
		class CooldownTickTask extends BukkitRunnable
		{
			@Override
			public void run()
			{
				for (Player player : getServer().getOnlinePlayers())
				{
					PlayerData data = playerDataCache.getData(player);

					try
					{
						Map<String, Long> cooldowns = data.getCooldowns();
						if (! cooldowns.isEmpty())
						{
							for (Entry<String, Long> entry : cooldowns.entrySet())
							{
								String key = entry.getKey();
								long remaining = entry.getValue() - 1;
								if (remaining <= 0)
								{
									cooldowns.remove(key);
									player.sendMessage(prefix + FormatUtil.format(getMessage("ability_refreshed"), key));
								}
								else
								{
									cooldowns.put(entry.getKey(), remaining);
								}
							}
						}
					}
					catch (Throwable ex)
					{
						data.setCooldowns(new HashMap<>());
						logHandler.log(Level.WARNING, Util.getUsefulStack(ex, "ticking cooldown for " + data.getLastKnownBy()));
					}
				}
			}
		}

		new CooldownTickTask().runTaskTimerAsynchronously(this, 2L, 1L);

		log(getMessage("log_enabled"), getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();

		// Cancel tasks and services
		getServer().getServicesManager().unregisterAll(this);
		getServer().getScheduler().cancelTasks(this);

		// Save data
		if (playerDataCache != null)
			playerDataCache.save();

		// Clear memory
		clearMemory();

		log(getMessage("log_disabled"), getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	private void clearMemory()
	{
		blockDropsMap.clear();
		blockDropsMap = null;
		fishDropsMap.clear();
		fishDropsMap = null;
		salvageRef.clear();
		salvageRef = null;
	}

	private void setupIntegration()
	{
		try
		{
			essentialsHandler = new EssentialsHandler(this);
		} catch (Throwable ignored) { }

		try
		{
			swornNationsHandler = new SwornNationsHandler(this);
		} catch (Throwable ignored) { }

		try
		{
			vaultHandler = new VaultHandler(this);
		} catch (Throwable ignored) { }
	}

	public final boolean isEssentialsHandler()
	{
		return essentialsHandler != null && essentialsHandler.isEnabled();
	}

	public final boolean isSwornNationsEnabled()
	{
		return swornNationsHandler != null && swornNationsHandler.isEnabled();
	}

	public final boolean isVaultEnabled()
	{
		return vaultHandler != null && vaultHandler.isEnabled();
	}

	// ---- Console Logging

	public final void log(String string, Object... objects)
	{
		logHandler.log(string, objects);
	}

	public final void log(Level level, String string, Object... objects)
	{
		logHandler.log(level, string, objects);
	}

	public final void debug(String string, Object... objects)
	{
		logHandler.debug(string, objects);
	}

	/**
	 * Attempts to get a message from the messages.properties.
	 *
	 * @param string Message key
	 */
	public final String getMessage(String string)
	{
		return resourceHandler.getMessage(string);
	}

	/**
	 * Reloads the Configuration.
	 */
	@Override
	public final void reload()
	{
		clearMemory();
		salvageRef = new HashMap<>();
		blockDropsMap = new HashMap<>();
		fishDropsMap = new HashMap<>();

		reloadConfig();
		reloadListeners();
		updateSalvageRef();
		updateBlockDrops();
		updateFishDrops();

		moduleHandler.reload();
		abilityHandler.reload();
		healthBarHandler.reload();
		experienceHandler.reload();
	}

	private void registerSwornGunsListener()
	{
		if (getPluginManager().isPluginEnabled("SwornGuns"))
		{
			registerListener(new net.dmulloy2.swornrpg.listeners.SwornGunsListener(this));
		}
	}

	private void registerListener(Listener listener)
	{
		if (listener instanceof Reloadable)
			listeners.add((Reloadable) listener);

		getPluginManager().registerEvents(listener, this);
	}

	public final PluginManager getPluginManager()
	{
		return getServer().getPluginManager();
	}

	private void reloadListeners()
	{
		for (Reloadable listener : listeners)
		{
			listener.reload();
		}
	}

	private void updateSalvageRef()
	{
		try
		{
			String salvage = getConfig().getString("salvage");

			salvageRef.put("iron", new HashMap<>());
			salvageRef.put("gold", new HashMap<>());
			salvageRef.put("diamond", new HashMap<>());
			String[] salvageArray = salvage.split("; ");
			for (String s : salvageArray)
			{
				String[] subset = s.split(", ");
				Material mat = Material.matchMaterial(subset[0]);
				int amt = NumberUtil.toInt(subset[2]);
				if (mat != null && amt != -1)
				{
					String type = subset[1].toLowerCase();
					if (! salvageRef.containsKey(type))
					{
						logHandler.log("Invalid salvage type \"{0}\"", type);
						continue;
					}

					salvageRef.get(type).put(mat, amt);
				}
			}
		}
		catch (Throwable ex)
		{
			logHandler.log(Level.WARNING, Util.getUsefulStack(ex, "updating salvaging"));
		}
	}

	private void updateBlockDrops()
	{
		blockDropsMap.clear();

		boolean saveRequired = false;

		ConfigurationSection section = getConfig().getConfigurationSection("blockDropItems");
		Map<String, Object> sectionData = section.getValues(true);
		for (Entry<String, Object> entry : sectionData.entrySet())
		{
			String key = entry.getKey();

			@SuppressWarnings("unchecked") // No way to check this :I
			List<String> values = (List<String>) entry.getValue();

			boolean sectionChanged = false;
			List<String> newValues = new ArrayList<>();

			List<BlockDrop> blockDrops = new ArrayList<>();
			for (String value : values)
			{
				int lastColon = value.lastIndexOf(":");
				if (lastColon == -1)
				{
					logHandler.log(Level.WARNING, "Block drop \"{0}\" has an invalid format. Should be <material>:<chance>", value);
					continue;
				}

				int chance = NumberUtil.toInt(value.substring(lastColon + 1));
				if (chance == -1)
				{
					logHandler.log(Level.WARNING, "Block drop \"{0}\" has an invalid chance", value);
					continue;
				}

				Material type;
				String materialStr = value.substring(0, lastColon);
				if (materialStr.contains(":") || NumberUtil.isInt(materialStr))
				{
					logHandler.log(Level.WARNING, "Block drop \"{0}\" uses a legacy material format. Attempting to migrate", value);
					type = MaterialUtil.convertFromLegacy(materialStr);

					String newFormat = FormatUtil.format("{0}:{1}", type.name(), chance);
					newValues.add(newFormat);

					saveRequired = true;
					sectionChanged = true;
				}
				else
				{
					type = Material.matchMaterial(materialStr);
					newValues.add(value);
				}

				if (type == null)
				{
					logHandler.log(Level.WARNING, getMessage("log_null_material"), materialStr, "block drops");
					continue;
				}

				blockDrops.add(new BlockDrop(type, chance));
			}

			if (sectionChanged)
			{
				section.set(key, newValues);
			}

			Material material = key.equals("*") ? Material.AIR : Material.matchMaterial(key);
			if (material == null)
			{
				logHandler.log(Level.WARNING, "Block drop category \"{0}\" was invalid", key);
				continue;
			}

			blockDropsMap.put(material, blockDrops);

			if (saveRequired)
			{
				try
				{
					getConfig().save(new File(getDataFolder(), "config.yml"));
				} catch (IOException ex)
				{
					getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving config"));
				}
			}
		}
	}

	private void updateFishDrops()
	{
		fishDropsMap.clear();

		Map<String, Object> map = getConfig().getConfigurationSection("fishDropItems").getValues(true);

		for (Entry<String, Object> entry : map.entrySet())
		{
			@SuppressWarnings("unchecked") // No way to check this :I
			List<String> values = (List<String>) entry.getValue();

			List<BlockDrop> fishDrops = new ArrayList<>();
			for (String value : values)
			{
				String[] ss = value.split(":");
				Material type = Material.matchMaterial(ss[0]);
				if (type == null)
				{
					logHandler.log(Level.WARNING, getMessage("log_null_material"), ss[0], "fish drops");
					continue;
				}

				int chance = NumberUtil.toInt(ss[1]);
				fishDrops.add(new BlockDrop(type, chance));
			}

			fishDropsMap.put(NumberUtil.toInt(entry.getKey()), fishDrops);
		}
	}

	/**
	 * Not-so-simple camping check
	 * TODO: Optimize this
	 */
	public final boolean isCamping(Player player)
	{
		int radius = getConfig().getInt("campingRadius", -1);
		if (radius <= 0)
			return false;

		// Cap radius at 16, values too large cause lag
		radius = Math.min(radius, 16);

		Location loc = player.getLocation();
		World world = loc.getWorld();

		for (int dx = -radius; dx <= radius; dx++)
		{
			for (int dy = -radius; dy <= radius; dy++)
			{
				for (int dz = -radius; dz <= radius; dz++)
				{
					Material mat = world.getBlockAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz).getType();
					if (mat == Material.SPAWNER)
					{
						if (! isDisabledWorld(player))
							player.sendMessage(FormatUtil.format(prefix + getMessage("spawner_camper")));
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Gets a given player's killer.
	 * 
	 * @param killed Player who was killed
	 * @return Killer, or null if not found
	 */
	public final Player getKiller(Player killed)
	{
		Entity attacker = killed.getKiller();
		if (attacker == null)
		{
			EntityDamageEvent ed = killed.getLastDamageCause();
			if (ed instanceof EntityDamageByEntityEvent ede)
			{
				attacker = ede.getDamager();
			}
		}

		Player killer = null;
		if (attacker != null)
		{
			if (attacker instanceof Player)
			{
				killer = (Player) attacker;
			}
			else if (attacker instanceof Projectile proj)
			{
				if (proj.getShooter() instanceof Player)
				{
					killer = (Player) proj.getShooter();
				}
			}
		}

		return killer;
	}

	// ---- Disabled World Checks

	public boolean isDisabledWorld(Entity entity)
	{
		return isDisabledWorld(entity.getWorld().getName());
	}

	public boolean isDisabledWorld(Block block)
	{
		return isDisabledWorld(block.getWorld().getName());
	}

	public boolean isDisabledWorld(World world)
	{
		return isDisabledWorld(world.getName());
	}

	private List<String> disabledWorlds;

	public final boolean isDisabledWorld(String worldName)
	{
		if (disabledWorlds == null)
			disabledWorlds = getConfig().getStringList("disabledWorlds");

		if (disabledWorlds.isEmpty())
			return false;

		for (String world : disabledWorlds)
		{
			if (world.equalsIgnoreCase(worldName))
				return true;
		}

		return false;
	}
}
