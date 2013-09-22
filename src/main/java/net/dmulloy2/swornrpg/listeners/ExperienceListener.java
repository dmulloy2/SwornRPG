package net.dmulloy2.swornrpg.listeners;

import java.util.Arrays;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.CropState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Crops;
import org.bukkit.material.NetherWarts;

/**
 * @author dmulloy2
 */

public class ExperienceListener implements Listener
{
	private final SwornRPG plugin;
	public ExperienceListener(SwornRPG plugin)
	{
		this.plugin = plugin;
	}

	/** Rewards XP in PvP situations **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		/** Checks for player kills to be enabled in the config **/
		if (! plugin.isPlayerkills())
			return;

		Player killed = event.getEntity().getPlayer();
		Player killer = event.getEntity().getKiller();

		/** Checks to see if it was PvP **/
		if (killer instanceof Player)
		{
			/** Warzone Checks **/
			if (plugin.checkFactions(killer, false))
				return;

			if (plugin.checkFactions(killed, false))
				return;

			String killerp = killer.getName();
			String killedp = killed.getName();

			/** Suicide Check **/
			if (killedp == killerp)
				return;

			String message = "";

			/** Killer Xp Gain **/
			int killxp = plugin.getKillergain();
			message = (plugin.prefix + FormatUtil.format(plugin.getMessage("pvp_kill_msg"), killxp, killedp));
			plugin.getExperienceHandler().onXPGain(killer, killxp, message);

			/** Killed Xp Loss **/
			int killedxp = -(plugin.getKilledloss());
			int msgxp = Math.abs(killedxp);
			message = (plugin.prefix + FormatUtil.format(plugin.getMessage("pvp_death_msg"), msgxp, killerp));
			plugin.getExperienceHandler().onXPGain(killed, killedxp, message);

			/** Debug Messages **/
			plugin.debug(plugin.getMessage("log_pvp_killed"), killedp, msgxp, killerp);
			plugin.debug(plugin.getMessage("log_pvp_killer"), killerp, killxp, killedp);
		}
	}

	/** Rewards XP in PvE situations **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		/** Checks for mob kills to be enabled in the config **/
		if (! plugin.isMobkills())
			return;

		Entity kill = event.getEntity().getKiller();
		Entity killed = event.getEntity();

		/** Checks to make sure it wasn't pvp **/
		if (killed instanceof Player)
			return;

		/** Checks to make sure the killer is a player **/
		if (kill instanceof Player)
		{
			Player killer = event.getEntity().getKiller();
			String mobname = FormatUtil.getFriendlyName(event.getEntity().getType());

			/** Warzone and Safezone check **/
			if (plugin.checkFactions(killer, true))
				return;

			/** Camping Check **/
			if (plugin.checkCamper(killer))
				return;

			/** XP gain calculation **/
			int killxp;
			List<String> tier3 = Arrays.asList(new String[] { "wither", "ender dragon" });
			List<String> tier2 = Arrays.asList(new String[] { "creeper", "enderman", "iron golem", "skeleton", "blaze", "zombie", "spider",
					"ghast", "magma cube", "witch", "slime" });

			if (tier3.contains(mobname.toLowerCase()))
			{
				killxp = plugin.getMobkillsxp() * 3;
			}
			else if (tier2.contains(mobname.toLowerCase()))
			{
				killxp = plugin.getMobkillsxp() * 2;
			}
			else
			{
				killxp = plugin.getMobkillsxp();
			}

			/** Message **/
			String article = FormatUtil.getArticle(mobname);
			String message = (plugin.prefix + FormatUtil.format(plugin.getMessage("mob_kill"), killxp, article, mobname));

			/** Give the player some xp **/
			plugin.getExperienceHandler().onXPGain(killer, killxp, message);
			plugin.debug(plugin.getMessage("log_mob_kill"), killer.getName(), killxp, mobname);
		}
	}

	/** Rewards XP on Minecraft xp levelup **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLevelChange(PlayerLevelChangeEvent event)
	{
		/** Checks to make sure xp level xp gain is enabled in the config **/
		if (! plugin.isXplevel())
			return;

		/** Warzone Check **/
		Player player = event.getPlayer();
		if (plugin.checkFactions(player, true))
			return;

		/** Camping Check **/
		if (plugin.checkCamper(player))
			return;

		/** Define Stuff **/
		int oldlevel = event.getOldLevel();
		int newlevel = event.getNewLevel();
		if (newlevel - oldlevel != 1)
			return;

		int xpgained = plugin.getXplevelgain();
		String message = (plugin.prefix + FormatUtil.format(plugin.getMessage("mc_xp_gain"), xpgained));

		/** Give the player some XP **/
		plugin.getExperienceHandler().onXPGain(player, xpgained, message);
		plugin.debug(plugin.getMessage("log_mcxpgain"), player.getName(), xpgained);
	}

	/** Herbalism : Breaking **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onHerbalismBreak(BlockBreakEvent event)
	{
		if (! plugin.isHerbalism() || event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (plugin.isDisabledWorld(player))
			return;
		
		if (player.getGameMode() == GameMode.CREATIVE)
			return;
		
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		int concurrentHerbalism = data.getConcurrentHerbalism();

		BlockState blockState = event.getBlock().getState();
		if (herbalismNeeded(blockState))
		{
			if (concurrentHerbalism >= 10)
			{
				int xp = plugin.getHerbalismgain() * 10;
				String message = FormatUtil.format(plugin.prefix + plugin.getMessage("herbalism_gain"), xp);
				plugin.getExperienceHandler().onXPGain(player, xp, message);
				data.setConcurrentHerbalism(0);
			}
			else
			{
				data.setConcurrentHerbalism(data.getConcurrentHerbalism() + 1);
			}
		}
	}

	/** Herbalism : Instant Growth **/
	@EventHandler(priority = EventPriority.NORMAL)
	public void onHerbalismPlace(BlockPlaceEvent event)
	{
		if (! plugin.isHerbalism() || event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (plugin.isDisabledWorld(player))
			return;
		
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		Block block = event.getBlock();
		BlockState blockState = block.getState();

		/** Insta-Growth **/
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		int level = data.getLevel();
		if (level == 0)
			level = 1;
		if (level > 150)
			level = 150;
		int rand = Util.random(200 - level);
		if (rand == 0)
		{
			boolean message = false;
			Material mat = blockState.getType();
			if (mat == Material.NETHER_WARTS)
			{
				((NetherWarts) blockState.getData()).setState(NetherWartsState.RIPE);
				blockState.update();
				message = true;
			}
			else if (mat == Material.CARROT || mat == Material.CROPS || mat == Material.POTATO)
			{
				((Crops) blockState.getData()).setState(CropState.RIPE);
				blockState.update();
				message = true;
			}
			else if (mat == Material.SAPLING)
			{
				block.setType(Material.AIR);
				block.getWorld().generateTree(block.getLocation(), TreeType.TREE);
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
			if (message == true)
			{
				player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("insta_growth")));
			}
		}
	}

	/** Herbalism Check **/
	public boolean herbalismNeeded(BlockState blockState)
	{
		switch (blockState.getType())
		{
			case CACTUS:
			case MELON_BLOCK:
			case PUMPKIN:
				return true;

			case CARROT:
			case CROPS:
			case POTATO:
				return ((Crops) blockState.getData()).getState() == CropState.RIPE;

			case NETHER_WARTS:
				return ((NetherWarts) blockState.getData()).getState() == NetherWartsState.RIPE;

			case COCOA:
				return ((CocoaPlant) blockState.getData()).getSize() == CocoaPlantSize.LARGE;

			default:
				return false;
		}
	}

	/** Taming XP Gain **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityTame(EntityTameEvent event)
	{
		if (! plugin.isTaming() || event.isCancelled())
			return;

		if (event.getOwner() instanceof Player)
		{
			Player player = (Player) event.getOwner();
			if (player != null)
			{
				/** XP Gain **/
				String mobname = FormatUtil.getFriendlyName(event.getEntity().getType());
				String article = FormatUtil.getArticle(mobname);

				String message = FormatUtil
						.format(plugin.prefix + plugin.getMessage("taming_gain"), plugin.getTaminggain(), article, mobname);
				plugin.getExperienceHandler().onXPGain(player, plugin.getTaminggain(), message);

				/** Wolf/Ocelot's Pal **/
				PlayerData data = plugin.getPlayerDataCache().getData(player);
				int level = data.getLevel();
				if (level <= 0)
					level = 1;
				if (level >= 50)
					level = 50;
				int rand2 = Util.random(150 / level);
				if (rand2 == 0)
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
				int rand1 = Util.random(150 / level);
				if (rand1 == 0)
				{
					boolean msg = false;
					List<Entity> entities = player.getNearbyEntities(10, 10, 10);
					if (entities.size() > 0)
					{
						for (Entity entity : entities)
						{
							if (entity != null && entity instanceof Tameable)
							{
								((Tameable) entity).setOwner(player);
								msg = true;
							}
						}
					}
					if (msg == true)
					{
						player.sendMessage(FormatUtil.format(plugin.prefix + plugin.getMessage("tame_bomb")));
					}
				}
			}
		}
	}

	/** Enchanting XP **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerEnchant(EnchantItemEvent event)
	{
		if (! plugin.isEnchanting() || event.isCancelled())
			return;

		int cost = event.getExpLevelCost();
		if (cost < 15)
			return;

		Player player = event.getEnchanter();
		if (player == null)
			return;
		
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		int level = data.getLevel();
		if (level == 0)
			level = 1;
		if (level > 30)
			level = 30;

		int xp = (cost / 2) + plugin.getEnchantbase();
		String message = FormatUtil.format(plugin.prefix + plugin.getMessage("enchant_gain"), xp);

		plugin.getExperienceHandler().onXPGain(player, xp, message);
	}
}