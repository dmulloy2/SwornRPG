package net.dmulloy2.swornrpg.listeners;

import java.util.Arrays;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.types.Reloadable;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.NetherWarts;

/**
 * @author dmulloy2
 */

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

	/** Rewards XP in PvP situations **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		/** Configuration Check **/
		if (! playerKillsEnabled)
			return;

		Player killed = event.getEntity();

		/** Figure out the killer **/
		Player killer = plugin.getKiller(killed);

		if (killer != null)
		{
			/** Warzone Checks **/
			if (plugin.checkFactions(killer, false) || plugin.checkFactions(killed, false))
				return;

			/** Suicide Check **/
			if (killed.getName().equals(killer.getName()))
				return;

			PlayerData data = plugin.getPlayerDataCache().getData(killed);
			if (System.currentTimeMillis() - data.getTimeOfLastDeath() <= 60L)
				return;

			data.setTimeOfLastDeath(System.currentTimeMillis());

			/** Killer Xp Gain **/
			String message = plugin.getPrefix() + 
					FormatUtil.format(plugin.getMessage("pvp_kill_msg"), killerXpGain, killed.getName());
			plugin.getExperienceHandler().onXPGain(killer, killerXpGain, message);

			/** Killed Xp Loss **/
			int msgxp = Math.abs(-killedXpLoss);
			message = plugin.getPrefix() + 
					FormatUtil.format(plugin.getMessage("pvp_death_msg"), msgxp, killer.getName());
			plugin.getExperienceHandler().onXPGain(killed, -killedXpLoss, message);

			/** Debug Messages **/
			plugin.debug(plugin.getMessage("log_pvp_killer"), killer.getName(), killerXpGain, killed.getName());
			plugin.debug(plugin.getMessage("log_pvp_killed"), killed.getName(), msgxp, killer.getName());
		}
	}

	/** Rewards XP in PvE situations **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		/** Configuration Check **/
		if (! mobKillsEnabled)
			return;

		/** This is handled above **/
		LivingEntity entity = event.getEntity();
		if (entity instanceof Player)
			return;

		Player killer = entity.getKiller();
		if (killer != null)
		{
			/** Warzone and Safezone check **/
			if (plugin.checkFactions(killer, true))
				return;

			/** Camping Check **/
			if (plugin.checkCamper(killer))
				return;

			String mobname = FormatUtil.getFriendlyName(event.getEntity().getType());

			/** XP gain calculation **/
			List<String> tier3 = Arrays.asList(new String[] { "wither", "ender dragon" });
			List<String> tier2 = Arrays.asList(new String[] { "creeper", "enderman", "iron golem", "skeleton", "blaze", "zombie", "spider",
					"ghast", "magma cube", "witch", "slime" });
			
			int killxp = mobKillsGain;
			if (tier3.contains(mobname.toLowerCase()))
			{
				killxp *= 3;
			}
			else if (tier2.contains(mobname.toLowerCase()))
			{
				killxp *= 2;
			}

			/** Message **/
			String article = FormatUtil.getArticle(mobname);
			String message = plugin.getPrefix() 
					+ FormatUtil.format(plugin.getMessage("mob_kill"), killxp, article, mobname);

			plugin.getExperienceHandler().onXPGain(killer, killxp, message);

			plugin.debug(plugin.getMessage("log_mob_kill"), killer.getName(), killxp, mobname);
		}
	}

	/** Rewards XP on Minecraft xp levelup **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLevelChange(PlayerLevelChangeEvent event)
	{
		/** Configuration Check **/
		if (! mcXpEnabled)
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

		String message = plugin.getPrefix() + 
				FormatUtil.format(plugin.getMessage("mc_xp_gain"), mcXpGain);
		plugin.getExperienceHandler().onXPGain(player, mcXpGain, message);

		plugin.debug(plugin.getMessage("log_mcxpgain"), player.getName(), mcXpGain);
	}

	/** Herbalism : Breaking **/
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
		
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		int concurrentHerbalism = data.getConcurrentHerbalism();

		BlockState blockState = event.getBlock().getState();
		if (herbalismNeeded(blockState))
		{
			if (concurrentHerbalism >= 10)
			{
				int xp = herbalismGain * 10;
				String message = FormatUtil.format(plugin.getPrefix() + plugin.getMessage("herbalism_gain"), xp);
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
		if (! herbalismEnabled || event.isCancelled())
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

		int level = data.getLevel(150);
		if (Util.random(200 - level) == 0)
		{
			boolean message = false;
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
			// Special case, since logs and leaves are considered "Trees"
			// TODO: Account for different species of trees
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

			if (message)
			{
				player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("insta_growth")));
			}
		}
	}

	/** Herbalism Check **/
	private final boolean herbalismNeeded(BlockState blockState)
	{
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

	/** Taming XP Gain **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityTame(EntityTameEvent event)
	{
		if (! tamingEnabled || event.isCancelled())
			return;

		if (event.getOwner() instanceof Player)
		{
			Player player = (Player) event.getOwner();
			if (player != null)
			{
				/** XP Gain **/
				String mobname = FormatUtil.getFriendlyName(event.getEntity().getType());
				String article = FormatUtil.getArticle(mobname);

				String message = plugin.getPrefix() + 
						FormatUtil.format(plugin.getMessage("taming_gain"), tamingGain, article, mobname);
				plugin.getExperienceHandler().onXPGain(player, tamingGain, message);

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
		if (player == null)
			return;

		int xp = (cost / 2) + enchantingGain;

		String message = plugin.getPrefix() +
				FormatUtil.format(plugin.getMessage("enchant_gain"), xp);
		plugin.getExperienceHandler().onXPGain(player, xp, message);
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