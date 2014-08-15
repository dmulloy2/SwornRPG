/**
 * SwornRPG - a bukkit plugin
 * Copyright (C) 2013 - 2014 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.swornrpg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.commands.CmdHelp;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;
import net.dmulloy2.handlers.ResourceHandler;
import net.dmulloy2.swornrpg.commands.CmdAbilities;
import net.dmulloy2.swornrpg.commands.CmdAddxp;
import net.dmulloy2.swornrpg.commands.CmdAdminChat;
import net.dmulloy2.swornrpg.commands.CmdAdminSay;
import net.dmulloy2.swornrpg.commands.CmdCoordsToggle;
import net.dmulloy2.swornrpg.commands.CmdDeny;
import net.dmulloy2.swornrpg.commands.CmdDivorce;
import net.dmulloy2.swornrpg.commands.CmdEject;
import net.dmulloy2.swornrpg.commands.CmdFrenzy;
import net.dmulloy2.swornrpg.commands.CmdHat;
import net.dmulloy2.swornrpg.commands.CmdHighCouncil;
import net.dmulloy2.swornrpg.commands.CmdLeaderboard;
import net.dmulloy2.swornrpg.commands.CmdLevel;
import net.dmulloy2.swornrpg.commands.CmdLore;
import net.dmulloy2.swornrpg.commands.CmdMarry;
import net.dmulloy2.swornrpg.commands.CmdName;
import net.dmulloy2.swornrpg.commands.CmdPropose;
import net.dmulloy2.swornrpg.commands.CmdReload;
import net.dmulloy2.swornrpg.commands.CmdResetLevel;
import net.dmulloy2.swornrpg.commands.CmdRide;
import net.dmulloy2.swornrpg.commands.CmdSitdown;
import net.dmulloy2.swornrpg.commands.CmdSpouse;
import net.dmulloy2.swornrpg.commands.CmdStaffList;
import net.dmulloy2.swornrpg.commands.CmdStandup;
import net.dmulloy2.swornrpg.commands.CmdSuperPickaxe;
import net.dmulloy2.swornrpg.commands.CmdUnlimitedAmmo;
import net.dmulloy2.swornrpg.commands.CmdUnride;
import net.dmulloy2.swornrpg.commands.CmdVersion;
import net.dmulloy2.swornrpg.handlers.AbilityHandler;
import net.dmulloy2.swornrpg.handlers.ExperienceHandler;
import net.dmulloy2.swornrpg.handlers.HealthBarHandler;
import net.dmulloy2.swornrpg.integration.EssentialsHandler;
import net.dmulloy2.swornrpg.integration.FactionsHandler;
import net.dmulloy2.swornrpg.integration.VaultHandler;
import net.dmulloy2.swornrpg.io.PlayerDataCache;
import net.dmulloy2.swornrpg.listeners.BlockListener;
import net.dmulloy2.swornrpg.listeners.EntityListener;
import net.dmulloy2.swornrpg.listeners.ExperienceListener;
import net.dmulloy2.swornrpg.listeners.PlayerListener;
import net.dmulloy2.swornrpg.types.BlockDrop;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.types.MyMaterial;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.MaterialUtil;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.TimeUtil;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class SwornRPG extends SwornPlugin implements Reloadable
{
	/** Handlers **/
	private @Getter ExperienceHandler experienceHandler;
	private @Getter HealthBarHandler healthBarHandler;
	private @Getter ResourceHandler resourceHandler;
	private @Getter AbilityHandler abilityHandler;

	private @Getter PlayerDataCache playerDataCache;

	/** Integration **/
	private @Getter EssentialsHandler essentialsHandler;
	private @Getter FactionsHandler factionsHandler;
	private @Getter VaultHandler vaultHandler;

	/** Listeners, Stored for Reloading **/
	private List<Listener> listeners;

	/** Maps **/
	private @Getter Map<String, Map<Material, Integer>> salvageRef;
	private @Getter Map<Material, List<BlockDrop>> blockDropsMap;
	private @Getter Map<Integer, List<BlockDrop>> fishDropsMap;

	/** Global Prefix Variable **/
	private @Getter String prefix;

	@Override
	public void onEnable()
	{
		try
		{
			long start = System.currentTimeMillis();

			/** Register LogHandler first **/
			logHandler = new LogHandler(this);

			/** Initialize Variables **/
			salvageRef = new HashMap<>();
			blockDropsMap = new HashMap<>();
			fishDropsMap = new HashMap<>();

			prefix = FormatUtil.format("&3[&eSwornRPG&3]&e ");

			/** Save and load messages.properties **/
			saveResource("messages.properties", true);
			resourceHandler = new ResourceHandler(this, getClassLoader());

			/** Register Other Handlers **/
			experienceHandler = new ExperienceHandler(this);
			healthBarHandler = new HealthBarHandler(this);
			permissionHandler = new PermissionHandler("srpg");
			abilityHandler = new AbilityHandler(this);
			commandHandler = new CommandHandler(this);

			/** Register Listeners **/
			listeners = new ArrayList<Listener>();
			registerListener(new PlayerListener(this));
			registerListener(new EntityListener(this));
			registerListener(new BlockListener(this));
			registerListener(new ExperienceListener(this));

			/** Check for PlayerData folder **/
			File playersFile = new File(getDataFolder(), "players");
			if (! playersFile.exists())
			{
				playersFile.mkdirs();
			}

			/** Configuration Stuff **/
			File conf = new File(getDataFolder(), "config.yml");
			if (! conf.exists())
			{
				outConsole(getMessage("log_config_create"));
				saveDefaultConfig();
			}
			else
			{
				if (! getConfig().isSet("disabledWorlds"))
				{
					conf.renameTo(new File(getDataFolder(), "oldConfig.yml"));
					outConsole(getMessage("log_config_outdated"));
					saveDefaultConfig();
				}
			}

			reloadConfig();

			/** Update Block Tables **/
			updateBlockDrops();
			updateFishDrops();

			/** Salvaging **/
			updateSalvageRef();

			/** Register Prefixed Commands **/
			commandHandler.setCommandPrefix("srpg");
			commandHandler.registerPrefixedCommand(new CmdHelp(this));
			commandHandler.registerPrefixedCommand(new CmdLeaderboard(this));
			commandHandler.registerPrefixedCommand(new CmdVersion(this));
			commandHandler.registerPrefixedCommand(new CmdReload(this));

			/** Register Non-Prefixed Commands **/
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

			/** Integration **/
			vaultHandler = new VaultHandler(this);
			factionsHandler = new FactionsHandler(this);
			essentialsHandler = new EssentialsHandler(this);

			playerDataCache = new PlayerDataCache(this);

			/** Deploy AutoSave Task **/
			if (getConfig().getBoolean("autoSave.enabled"))
			{
				int interval = 20 * 60 * getConfig().getInt("autoSave.interval");

				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						// Save and cleanup
						playerDataCache.save();
						playerDataCache.cleanupData();
					}
				}.runTaskTimerAsynchronously(this, interval, interval);
			}

			/** Cooldowns **/
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					for (Player player : Util.getOnlinePlayers())
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
							data.setCooldowns(new HashMap<String, Long>());
							logHandler.log(Level.WARNING, Util.getUsefulStack(ex, "ticking cooldown for " + data.getLastKnownBy()));
						}
					}
				}
			}.runTaskTimerAsynchronously(this, 2L, 1L);

			/** Online XP Gain **/
			final int onlineXpGain = getConfig().getInt("levelingMethods.onlineTime.xpgain");
			final long interval = TimeUtil.toTicks(60); // Minute

			if (getConfig().getBoolean("levelingMethods.onlineTime.enabled"))
			{
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						for (Player player : Util.getOnlinePlayers())
						{
							/** This method no longer causes any noticable lag **/
							experienceHandler.handleXpGain(player, onlineXpGain, "");
						}
					}
				}.runTaskTimer(this, interval, interval);
			}

			outConsole(getMessage("log_enabled"), getDescription().getFullName(), System.currentTimeMillis() - start);
		}
		catch (final Throwable ex)
		{
			// Something happened when we tried to enable
			getLogger().severe(Util.getUsefulStack(ex, "enabling SwornRPG"));

			// Alert online OP's
			for (Player player : Util.getOnlinePlayers())
			{
				if (player.isOp())
				{
					player.sendMessage(prefix + FormatUtil.format("&4SwornRPG failed to load! Exception: &c{0}", ex));
				}
			}

			// If an OP joins, alert them as well
			getPluginManager().registerEvents(new Listener()
			{
				@EventHandler
				public void onPlayerJoin(PlayerJoinEvent event)
				{
					Player player = event.getPlayer();
					if (player.isOp())
					{
						player.sendMessage(prefix + FormatUtil.format("&4SwornRPG failed to load! Exception: &c{0}", ex));
					}
				}
			}, this);

			// Set the plugin as disabled
			setEnabled(false);
		}
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();

		/** Cancel tasks / services **/
		getServer().getServicesManager().unregisterAll(this);
		getServer().getScheduler().cancelTasks(this);

		/** Save Data **/
		if (playerDataCache != null)
			playerDataCache.save();

		/** Clear Memory **/
		clearMemory();

		outConsole(getMessage("log_disabled"), getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	/**
	 * Clears Lists and Maps
	 */
	private final void clearMemory()
	{
//		healthBarHandler.unregister();

		blockDropsMap = null;
		fishDropsMap = null;
		salvageRef = null;
	}

	// ---- Console Logging

	public final void outConsole(String string, Object... objects)
	{
		logHandler.log(string, objects);
	}

	public final void outConsole(Level level, String string, Object... objects)
	{
		logHandler.log(level, string, objects);
	}

	public final void debug(String string, Object... objects)
	{
		logHandler.debug(string, objects);
	}

	/**
	 * Attempts to get a message from the messages.properties
	 *
	 * @param string Message key
	 */
	public final String getMessage(String string)
	{
		try
		{
			return resourceHandler.getMessages().getString(string);
		}
		catch (MissingResourceException ex)
		{
			outConsole(Level.WARNING, getMessage("log_message_missing"), string);
			return FormatUtil.format("(Missing message key \"{0}\")", string);
		}
	}

	/**
	 * Reloads the Configuration
	 */
	@Override
	public final void reload()
	{
		reloadConfig();
		reloadListeners();
		updateSalvageRef();
		updateBlockDrops();
		updateFishDrops();

		abilityHandler.reload();
		healthBarHandler.reload();
		experienceHandler.reload();
	}

	private final void registerListener(Listener listener)
	{
		listeners.add(listener);
		getPluginManager().registerEvents(listener, this);
	}

	public final PluginManager getPluginManager()
	{
		return getServer().getPluginManager();
	}

	private final void reloadListeners()
	{
		for (Listener listener : listeners)
		{
			if (listener instanceof Reloadable)
			{
				((Reloadable) listener).reload();
			}
		}
	}

	private final void updateSalvageRef()
	{
		try
		{
			String salvage = getConfig().getString("salvage");

			salvageRef.put("iron", new HashMap<Material, Integer>());
			salvageRef.put("gold", new HashMap<Material, Integer>());
			salvageRef.put("diamond", new HashMap<Material, Integer>());
			String[] salvageArray = salvage.split("; ");
			for (String s : salvageArray)
			{
				String[] subset = s.split(", ");
				Material mat = MaterialUtil.getMaterial(subset[0]);
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

	/**
	 * Update Block Drops
	 */
	public final void updateBlockDrops()
	{
		blockDropsMap.clear();

		Map<String, Object> map = getConfig().getConfigurationSection("blockDropItems").getValues(true);

		for (Entry<String, Object> entry : map.entrySet())
		{
			String key = entry.getKey();

			@SuppressWarnings("unchecked") // No way to check this :I
			List<String> values = (List<String>) entry.getValue();

			List<BlockDrop> blockDrops = new ArrayList<BlockDrop>();
			for (String value : values)
			{
				String[] ss = value.split(":");
				Material type = MaterialUtil.getMaterial(ss[0]);
				if (type == null)
				{
					logHandler.log(Level.WARNING, getMessage("log_null_material"), ss[0], "block drops");
					continue;
				}

				short data = -1;
				int chance = 0;
				if (ss.length == 3)
				{
					data = NumberUtil.toShort(ss[1]);
					chance = NumberUtil.toInt(ss[2]);
				}
				else
				{
					chance = NumberUtil.toInt(ss[1]);
				}

				if (type != null && chance > 0)
				{
					boolean ignoreData = data == -1;
					if (data < 0)
						data = 0;

					MyMaterial material = new MyMaterial(type, data, ignoreData);
					blockDrops.add(new BlockDrop(material, chance));
				}
			}

			Material material = key.equals("*") ? Material.AIR : MaterialUtil.getMaterial(key);
			blockDropsMap.put(material, blockDrops);
		}
	}

	/**
	 * Update Fish Drops
	 */
	private final void updateFishDrops()
	{
		fishDropsMap.clear();

		Map<String, Object> map = getConfig().getConfigurationSection("fishDropItems").getValues(true);

		for (Entry<String, Object> entry : map.entrySet())
		{
			@SuppressWarnings("unchecked") // No way to check this :I
			List<String> values = (List<String>) entry.getValue();

			List<BlockDrop> fishDrops = new ArrayList<BlockDrop>();
			for (String value : values)
			{
				String[] ss = value.split(":");
				Material type = MaterialUtil.getMaterial(ss[0]);
				if (type == null)
				{
					logHandler.log(Level.WARNING, getMessage("log_null_material"), ss[0], "fish drops");
					continue;
				}

				short data = -1;
				int chance = 0;
				if (ss.length == 3)
				{
					data = NumberUtil.toShort(ss[1]);
					chance = NumberUtil.toInt(ss[2]);
				}
				else
				{
					chance = NumberUtil.toInt(ss[1]);
				}

				if (type != null && chance != -1)
				{
					boolean ignoreData = data == -1;
					if (data < 0)
						data = 0;

					MyMaterial material = new MyMaterial(type, data, ignoreData);
					fishDrops.add(new BlockDrop(material, chance));
				}
			}

			fishDropsMap.put(NumberUtil.toInt(entry.getKey()), fishDrops);
		}
	}

	/**
	 * Camping Check
	 */
	public final boolean checkCamper(Player player)
	{
		Location loc = player.getLocation();
		World world = loc.getWorld();
		int RADIUS = getConfig().getInt("campingRadius");
		for (int dx = -RADIUS; dx <= RADIUS; dx++)
		{
			for (int dy = -RADIUS; dy <= RADIUS; dy++)
			{
				for (int dz = -RADIUS; dz <= RADIUS; dz++)
				{
					Material mat = world.getBlockAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz).getType();
					if (mat == Material.MOB_SPAWNER)
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

	public final Player getKiller(Player killed)
	{
		Entity attacker = killed.getKiller();
		if (attacker == null)
		{
			EntityDamageEvent ed = killed.getLastDamageCause();
			if (ed instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent ede = (EntityDamageByEntityEvent) ed;
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
			else if (attacker instanceof Projectile)
			{
				Projectile proj = (Projectile) attacker;
				if (proj.getShooter() instanceof Player)
				{
					killer = (Player) proj.getShooter();
				}
			}
		}

		return killer;
	}

	// ---- Disabled World Checks

	public boolean isDisabledWorld(Player player)
	{
		return isDisabledWorld(player.getWorld());
	}

	public boolean isDisabledWorld(Entity entity)
	{
		return isDisabledWorld(entity.getWorld());
	}

	public boolean isDisabledWorld(Block block)
	{
		return isDisabledWorld(block.getWorld());
	}

	public boolean isDisabledWorld(World world)
	{
		return getConfig().getStringList("disabledWorlds").contains(world.getName());
	}
}