/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornrpg.listeners;

import java.util.Arrays;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.CropState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.NetherWarts;
import org.bukkit.material.Tree;

/**
 * @author dmulloy2
 */

// TODO: Move these events to their specific listeners, per convention
public class ExperienceListener implements Listener, Reloadable
{
	private boolean playerKillsEnabled;
	private boolean mobKillsEnabled;
	private boolean mcXpEnabled;
	private boolean herbalismEnabled;
	private boolean tamingEnabled;
	private boolean enchantingEnabled;

	private int killerXpGain;
	private int killedXpLoss;
	private int mobKillsGain;
	private int mcXpGain;
	private int herbalismGain;
	private int tamingGain;
	private int enchantingGain;
	
	private final SwornRPG plugin;
	public ExperienceListener(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.reload();
	}

	private static final List<String> tier3 = Arrays.asList(
			"wither", "ender dragon");
	private static final List<String> tier2 = Arrays.asList(
			"creeper", "enderman", "iron golem", "skeleton", "blaze", "zombie", "spider", "ghast", "magma cube", "witch", "slime");

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		LivingEntity died = event.getEntity();
		if (plugin.isDisabledWorld(died))
			return;

		if (died instanceof Player)
		{
			if (! playerKillsEnabled)
				return;

			// Special handling for players
			Player killed = (Player) died;

			// Figure out their killer
			Player killer = plugin.getKiller(killed);
			if (killer != null)
			{
				// Factions checks
				if (plugin.isSwornNationsEnabled() && (plugin.getSwornNationsHandler().isApplicable(killer, false)
						|| plugin.getSwornNationsHandler().isApplicable(killed, false)))
					return;

				// Suicide check
				if (killed.getName().equals(killer.getName()))
					return;

				// Prevent multiple deaths
				PlayerData data = plugin.getPlayerDataCache().getData(killed);
				if (System.currentTimeMillis() - data.getTimeOfLastDeath() <= 60L)
					return;

				data.setTimeOfLastDeath(System.currentTimeMillis());

				// Killer xp gain
				String message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("pvp_kill_msg"), killerXpGain, killed.getName());
				plugin.getExperienceHandler().handleXpGain(killer, killerXpGain, message);

				// Killed xp loss
				message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("pvp_death_msg"), killedXpLoss, killer.getName());
				plugin.getExperienceHandler().handleXpGain(killed, - killedXpLoss, message);

				/* plugin.debug(plugin.getMessage("log_pvp_killer"), killer.getName(), killerXpGain, killed.getName());
				plugin.debug(plugin.getMessage("log_pvp_killed"), killed.getName(), killedXpLoss, killer.getName()); */
			}
		}
		else if (mobKillsEnabled)
		{
			Player killer = died.getKiller();
			if (killer != null)
			{
				// Factions checks
				if (plugin.isSwornNationsEnabled() && plugin.getSwornNationsHandler().isApplicable(killer, true))
					return;

				// Camping check
				if (plugin.isCamping(killer))
					return;

				String mobName = FormatUtil.getFriendlyName(event.getEntity().getType());

				// Determine tier
				int killxp = mobKillsGain;
				if (tier3.contains(mobName.toLowerCase()))
					killxp *= 3;
				else if (tier2.contains(mobName.toLowerCase()))
					killxp *= 2;

				String article = FormatUtil.getArticle(mobName);
				String message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("mob_kill"), killxp, article, mobName);
				plugin.getExperienceHandler().handleXpGain(killer, killxp, message);

				// plugin.debug(plugin.getMessage("log_mob_kill"), killer.getName(), killxp, mobName);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLevelChange(PlayerLevelChangeEvent event)
	{
		if (! mcXpEnabled)
			return;

		// GameMode check
		Player player = event.getPlayer();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;

		// Factions checks
		if (plugin.isSwornNationsEnabled() && plugin.getSwornNationsHandler().isApplicable(player, true))
			return;

		// Disabled world check
		if (plugin.isDisabledWorld(player))
			return;

		// Camping check
		if (plugin.isCamping(player))
			return;

		// Only give xp for single level changes
		int oldLevel = event.getOldLevel();
		int newLevel = event.getNewLevel();
		if (newLevel - oldLevel != 1)
			return;

		String message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("mc_xp_gain"), mcXpGain);
		plugin.getExperienceHandler().handleXpGain(player, mcXpGain, message);

		// plugin.debug(plugin.getMessage("log_mcxpgain"), player.getName(), mcXpGain);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHerbalismBreak(BlockBreakEvent event)
	{
		if (! herbalismEnabled || event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (plugin.isDisabledWorld(player))
			return;

		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		if (herbalismNeeded(event.getBlock()))
		{
			PlayerData data = plugin.getPlayerDataCache().getData(player);
			int concurrentHerbalism = data.getConcurrentHerbalism();
			if (concurrentHerbalism >= 10)
			{
				int xp = herbalismGain * 10;
				String message = FormatUtil.format(plugin.getPrefix() + plugin.getMessage("herbalism_gain"), xp);
				plugin.getExperienceHandler().handleXpGain(player, xp, message);
				data.setConcurrentHerbalism(0);
			}
			else
			{
				data.setConcurrentHerbalism(data.getConcurrentHerbalism() + 1);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onHerbalismPlace(BlockPlaceEvent event)
	{
		if (! herbalismEnabled || event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (plugin.isDisabledWorld(player))
			return;
		
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		Block block = event.getBlock();
		if (! isGrowable(block))
			return;

		/** Instant Growth **/
		PlayerData data = plugin.getPlayerDataCache().getData(player);

		int level = data.getLevel(150);
		if (Util.random(200 - level) == 0)
		{
			boolean message = false;
			BlockState blockState = block.getState();
			Material mat = blockState.getType();
			MaterialData dat = blockState.getData();
			if (dat instanceof NetherWarts)
			{
				((NetherWarts) dat).setState(NetherWartsState.RIPE);
				blockState.update();
				message = true;
			}
			else if (dat instanceof Crops)
			{
				((Crops) dat).setState(CropState.RIPE);
				blockState.update();
				message = true;
			}
			else if (dat instanceof CocoaPlant)
			{
				((CocoaPlant) dat).setSize(CocoaPlantSize.LARGE);
				blockState.update();
				message = true;
			}
			else if (mat == Material.SAPLING)
			{
				Tree tree = (Tree) block.getState().getData();
				TreeSpecies species = tree.getSpecies();
				TreeType type = TreeType.TREE;
				switch (species)
				{
					case ACACIA:
						type = TreeType.ACACIA;
						break;
					case BIRCH:
						type = TreeType.BIRCH;
						break;
					case DARK_OAK:
						type = TreeType.DARK_OAK;
						break;
					case GENERIC:
						type = TreeType.TREE;
						break;
					case JUNGLE:
						type = TreeType.JUNGLE;
						break;
					case REDWOOD:
						type = TreeType.REDWOOD;
						break;
				}

				block.setType(Material.AIR);
				block.getWorld().generateTree(block.getLocation(), type);
				message = true;
			}
			else if (mat == Material.RED_MUSHROOM)
			{
				block.setType(Material.AIR);
				block.getWorld().generateTree(block.getLocation(), TreeType.RED_MUSHROOM);
				message = true;
			}
			else if (mat == Material.BROWN_MUSHROOM)
			{
				block.setType(Material.AIR);
				block.getWorld().generateTree(block.getLocation(), TreeType.BROWN_MUSHROOM);
				message = true;
			}

			if (message)
			{
				player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("insta_growth")));
			}
		}
	}

	/**
	 * Returns whether or not a given {@link Block} is ripe or otherwise
	 * farmable
	 * 
	 * @param block {@link Block} to check
	 */
	private final boolean herbalismNeeded(Block block)
	{
		BlockState blockState = block.getState();
		switch (blockState.getType())
		{
			case CACTUS:
			case MELON_BLOCK:
			case PUMPKIN:
				return true;
			case CROPS:
				return ((Crops) blockState.getData()).getState() == CropState.RIPE;
			case NETHER_WARTS:
				return ((NetherWarts) blockState.getData()).getState() == NetherWartsState.RIPE;
			case COCOA:
				return ((CocoaPlant) blockState.getData()).getSize() == CocoaPlantSize.LARGE;
			default:
				return false;
		}
	}

	/**
	 * Returns whether or not a given {@link Block} can be grown
	 * 
	 * @param block {@link Block} to check
	 */
	private final boolean isGrowable(Block block)
	{
		BlockState state = block.getState();
		Material material = block.getType();
		MaterialData data = state.getData();

		if (data instanceof NetherWarts || data instanceof Crops || data instanceof CocoaPlant)
			return true;

		return material == Material.SAPLING || material == Material.RED_MUSHROOM || material == Material.BROWN_MUSHROOM;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityTame(EntityTameEvent event)
	{
		if (! tamingEnabled || event.isCancelled())
			return;

		if (event.getOwner() instanceof Player)
		{
			Player player = (Player) event.getOwner();
			if (! plugin.isDisabledWorld(player))
			{
				/** XP Gain **/
				String mobname = FormatUtil.getFriendlyName(event.getEntity().getType());
				String article = FormatUtil.getArticle(mobname);

				String message = plugin.getPrefix() +
						FormatUtil.format(plugin.getMessage("taming_gain"), tamingGain, article, mobname);
				plugin.getExperienceHandler().handleXpGain(player, tamingGain, message);

				/** Wolf/Ocelot's Pal **/
				PlayerData data = plugin.getPlayerDataCache().getData(player);

				int level = data.getLevel(50);
				if (Util.random(150 / level) == 0)
				{
					if (event.getEntity() instanceof Wolf)
					{
						Wolf wolf = (Wolf) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
						wolf.setOwner(player);
					}
					else if (event.getEntity() instanceof Ocelot)
					{
						Ocelot ocelot = (Ocelot) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.OCELOT);
						ocelot.setOwner(player);
					}
				}

				/** Taming Bomb! **/
				if (Util.random(150 / level) == 0)
				{
					boolean sendMessage = false;
					List<Entity> entities = player.getNearbyEntities(10, 10, 10);
					if (entities.size() > 0)
					{
						for (Entity entity : entities)
						{
							if (entity != null && entity instanceof Tameable)
							{
								if (! ((Tameable) entity).isTamed())
								{
									((Tameable) entity).setOwner(player);
									sendMessage = true;
								}
							}
						}
					}

					if (sendMessage)
					{
						player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("tame_bomb")));
					}
				}
			}
		}
	}

	/** Enchanting XP **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerEnchant(EnchantItemEvent event)
	{
		if (! enchantingEnabled || event.isCancelled())
			return;

		int cost = event.getExpLevelCost();
		if (cost < 15)
			return;

		Player player = event.getEnchanter();
		if (player == null || plugin.isDisabledWorld(player))
			return;

		int xp = (cost / 2) + enchantingGain;

		String message = plugin.getPrefix() +
				FormatUtil.format(plugin.getMessage("enchant_gain"), xp);
		plugin.getExperienceHandler().handleXpGain(player, xp, message);
	}

	@Override
	public void reload()
	{
		this.playerKillsEnabled = plugin.getConfig().getBoolean("levelingMethods.playerKills.enabled");
		this.mobKillsEnabled = plugin.getConfig().getBoolean("levelingMethods.mobKills.enabled");
		this.mcXpEnabled = plugin.getConfig().getBoolean("levelingMethods.mcXpGain.enabled");
		this.herbalismEnabled = plugin.getConfig().getBoolean("levelingMethods.herbalism.enabled");
		this.tamingEnabled = plugin.getConfig().getBoolean("levelingMethods.taming.enabled");
		this.enchantingEnabled = plugin.getConfig().getBoolean("levelingMethods.enchanting.enabled");

		this.killerXpGain = plugin.getConfig().getInt("levelingMethods.playerKills.xpgain");
		this.killedXpLoss = plugin.getConfig().getInt("levelingMethods.playerKills.xploss");
		this.mobKillsGain = plugin.getConfig().getInt("levelingMethods.mobKills.xpgain");
		this.mcXpGain = plugin.getConfig().getInt("levelingMethods.mcXpGain.xpgain");
		this.herbalismGain = plugin.getConfig().getInt("levelingMethods.herbalism.xpgain");
		this.tamingGain = plugin.getConfig().getInt("levelingMethods.taming.xpgain");
		this.enchantingGain = plugin.getConfig().getInt("levelingMethods.enchanting.xpgain");
	}
}