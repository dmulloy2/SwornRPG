package net.dmulloy2.swornrpg.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.integration.EssentialsHandler;
import net.dmulloy2.swornrpg.types.BlockDrop;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.TimeUtil;
import net.dmulloy2.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

public class PlayerListener implements Listener, Reloadable
{
	private boolean salvagingEnabled;
	private boolean deathCoordinateMessages;
	private boolean fishingEnabled;
	private boolean fishDropsEnabled;
	private boolean speedBoostEnabled;

	private int fishingGain;
	private int speedBoostOdds;
	private int speedBoostDuration;
	private int speedBoostStrength;

	private Map<String, ItemStack> bookMap;

	private final SwornRPG plugin;
	public PlayerListener(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.bookMap = new HashMap<>();
		this.reload();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (! salvagingEnabled || event.isCancelled())
			return;

		if (! event.hasBlock() || event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		Block block = event.getClickedBlock();
		if (plugin.isDisabledWorld(block))
			return;

		Player player = event.getPlayer();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;

		String blockType = "";
		if (block.getType() == Material.IRON_BLOCK)
			blockType = "Iron";
		if (block.getType() == Material.GOLD_BLOCK)
			blockType = "Gold";
		if (block.getType() == Material.DIAMOND_BLOCK)
			blockType = "Diamond";

		if (! blockType.isEmpty())
		{
			if (block.getRelative(-1, 0, 0).getType() == Material.FURNACE
					|| block.getRelative(1, 0, 0).getType() == Material.FURNACE
					|| block.getRelative(0, 0, -1).getType() == Material.FURNACE
					|| block.getRelative(0, 0, 1).getType() == Material.FURNACE)
			{
				ItemStack item = player.getItemInHand();
				Material type = item.getType();

				double mult = 1.0D - ((double) item.getDurability() / item.getType().getMaxDurability());
				double amt = 0.0D;

				if (plugin.getSalvageRef().get(blockType.toLowerCase()).containsKey(type))
					amt = Math.round(plugin.getSalvageRef().get(blockType.toLowerCase()).get(type) * mult);

				if (amt > 0.0D)
				{
					String article = "a";
					if (blockType == "Iron")
						article = "an";
					String materialExtension = " ingot";
					if (blockType == "Diamond")
						materialExtension = "";
					String plural = "";
					if (amt > 1.0D)
						plural = "s";

					String itemName = FormatUtil.getFriendlyName(item.getType());
					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("salvage_success"),
							article, itemName, amt, blockType.toLowerCase(), materialExtension, plural));

					plugin.log(plugin.getMessage("log_salvage"), player.getName(), itemName, amt, blockType.toLowerCase(),
							materialExtension, plural);

					PlayerInventory inv = player.getInventory();
					inv.removeItem(item);

					Material give = null;
					if (blockType == "Iron")
						give = Material.IRON_INGOT;
					if (blockType == "Gold")
						give = Material.GOLD_INGOT;
					if (blockType == "Diamond")
						give = Material.DIAMOND;

					ItemStack salvaged = new ItemStack(give, (int) amt);
					InventoryUtil.giveItem(player, salvaged);
					event.setCancelled(true);
				}
				else
				{
					String itemName = FormatUtil.getFriendlyName(item.getType());
					player.sendMessage(plugin.getPrefix() +
							FormatUtil.format(plugin.getMessage("not_salvagable"), itemName, blockType.toLowerCase()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if (! deathCoordinateMessages)
			return;

		Player player = event.getEntity();
		if (plugin.isSwornNationsEnabled() && plugin.getSwornNationsHandler().isApplicable(player, true))
			return;

		Location loc = player.getLocation();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();

		// Death coordinates
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isDeathCoordsEnabled())
		{
			EssentialsHandler handler = plugin.getEssentialsHandler();
			if (handler != null && handler.isEnabled())
			{
				Player killer = plugin.getKiller(player);
				if (killer != null)
				{
					String mail = FormatUtil.format(plugin.getMessage("mail_pvp_format"),
							killer.getName(), x, y, z, loc.getWorld().getName(), TimeUtil.getLongDateCurr());
					handler.sendMail(player, mail);

					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("death_coords_mail")));
					plugin.debug(plugin.getMessage("log_death_coords"), player.getName(), "sent", "mail message");
				}
				else
				{
					String world = player.getWorld().getName();

					String mail = FormatUtil.format(plugin.getMessage("mail_pve_format"), x, y, z, world, TimeUtil.getLongDateCurr());
					handler.sendMail(player, mail);

					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("death_coords_mail")));
					plugin.debug(plugin.getMessage("log_death_coords"), player.getName(), "sent", "mail message");
				}
			}
			else
			{
				ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
				BookMeta meta = (BookMeta) book.getItemMeta();

				meta.setTitle(FormatUtil.format("&eDeath Coords"));
				meta.setAuthor(FormatUtil.format("&bSwornRPG"));

				List<String> pages = new ArrayList<String>();
				pages.add(FormatUtil.format(plugin.getMessage("book_format"), player.getName(), x, y, z));
				meta.setPages(pages);

				book.setItemMeta(meta);

				if (! bookMap.containsKey(player.getName()))
				{
					bookMap.put(player.getName(), book);
					plugin.debug(plugin.getMessage("log_death_coords"), player.getName(), "given", "book");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		if (bookMap.containsKey(player.getName()))
		{
			InventoryUtil.giveItem(player, bookMap.get(player.getName()));
			bookMap.remove(player.getName());
		}

		plugin.getHealthBarHandler().updateHealth(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		// Check for NaN
		Player player = event.getPlayer();
		Location location = player.getLocation();
		if (Double.isNaN(location.getX()) || Double.isNaN(location.getY()) || Double.isNaN(location.getZ()))
		{
			player.teleport(player.getWorld().getSpawnLocation());
			plugin.getLogHandler().log(Level.INFO, "Corrected invalid position for {0}", player.getName());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player);

		// Disable abilities
		data.setFrenzyEnabled(false);
		data.setSuperPickaxeEnabled(false);
		data.setUnlimitedAmmoEnabled(false);

		// Clear the previousLocation variable
		if (data.getPreviousLocation() != null)
		{
			player.teleport(data.getPreviousLocation());
			data.setPreviousLocation(null);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event)
	{
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player);

		// Clear the previousLocation variable
		if (data.getPreviousLocation() != null)
		{
			player.teleport(data.getPreviousLocation());
			data.setPreviousLocation(null);
		}

		// Attempts to correct invalid positioning with chairs. This works by
		// first checking if the kick was invalid, then attempting to teleport
		// the player to spawn. This only works on Spigot, since the messages in
		// CraftBukkit are "Nope!" (which can be for multiple things)
		if (event.getReason().equals("NaN in position (Hacking?)"))
		{
			Location location = player.getLocation();
			if (! Double.isNaN(location.getX()) && ! Double.isNaN(location.getY()) && ! Double.isNaN(location.getZ()))
			{
				plugin.getLogHandler().log("Blocked invalid kick for {0}", player.getName());
				event.setCancelled(true);
				return;
			}

			// Attempt to correct the position
			player.teleport(player.getWorld().getSpawnLocation());

			// Were we successful?
			location = player.getLocation();
			if (! Double.isNaN(location.getX()) && ! Double.isNaN(location.getY()) && ! Double.isNaN(location.getZ()))
			{
				plugin.getLogHandler().log("Corrected invalid position for {0}", player.getName());
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAbilityActivate(PlayerInteractEvent event)
	{
		// Check ability activation
		plugin.getAbilityHandler().checkActivation(event.getPlayer(), event.getAction());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerFish(PlayerFishEvent event)
	{
		if (! fishingEnabled || event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (plugin.isDisabledWorld(player))
			return;

		Entity caught = event.getCaught();
		if (caught == null || caught.getType() != EntityType.DROPPED_ITEM)
			return;

		// Fishing xp gain
		String message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("fishing_gain"), fishingGain);
		plugin.getExperienceHandler().handleXpGain(event.getPlayer(), fishingGain, message);

		/** Fish Drops **/
		if (! fishDropsEnabled)
			return;

		if (player.getGameMode() != GameMode.SURVIVAL)
			return;

		PlayerData data = plugin.getPlayerDataCache().getData(player);

		int level = data.getLevel(10);

		List<BlockDrop> drops = new ArrayList<BlockDrop>();
		for (int i = 0; i < level; i++)
		{
			if (plugin.getFishDropsMap().containsKey(i))
			{
				for (BlockDrop fishDrop : plugin.getFishDropsMap().get(i))
				{
					if (fishDrop.getMaterial() == null)
						continue;

					if (Util.random(fishDrop.getChance()) == 0)
					{
						drops.add(fishDrop);
					}
				}
			}
		}

		if (! drops.isEmpty())
		{
			int rand = Util.random(drops.size());
			BlockDrop fishDrop = drops.get(rand);
			if (fishDrop != null)
			{
				caught.getWorld().dropItemNaturally(caught.getLocation(), fishDrop.getMaterial().newItemStack(1));

				String name = fishDrop.getMaterial().getName();
				String article = FormatUtil.getArticle(name);
				player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("fishing_drop"), article, name));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerToggleSprint(PlayerToggleSprintEvent event)
	{
		if (! speedBoostEnabled || event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (plugin.isDisabledWorld(player))
			return;

		if (! player.isSprinting() || player.getGameMode() != GameMode.SURVIVAL)
			return;

		if (plugin.isSwornNationsEnabled() && plugin.getSwornNationsHandler().isApplicable(player, false))
			return;

		if (Util.random(speedBoostOdds) == 0)
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speedBoostDuration, speedBoostStrength));
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("speed_boost")));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		Player player = event.getPlayer();
		if (event.isCancelled() || player == null)
			return;

		if (! player.isInsideVehicle() && player.getPassenger() == null)
		{
			PlayerData data = plugin.getPlayerDataCache().getData(player);
			if (data.isRideWaiting() && (System.currentTimeMillis() - data.getRideWaitingTime()) <= 200L)
			{
				Entity clicked = event.getRightClicked();
				EntityType type = clicked.getType();
				switch (type)
				{
					case BLAZE:
					case CAVE_SPIDER:
					case CHICKEN:
					case COW:
					case CREEPER:
					case ENDERMAN:
					case GHAST:
					case GIANT:
					case IRON_GOLEM:
					case MAGMA_CUBE:
					case MUSHROOM_COW:
					case OCELOT:
					case PIG:
					case PIG_ZOMBIE:
					case SHEEP:
					case SILVERFISH:
					case SKELETON:
					case SLIME:
					case SNOWMAN:
					case SPIDER:
					case SQUID:
					case VILLAGER:
					case WITCH:
					case WITHER:
					case WOLF:
					case ZOMBIE:
						clicked.setPassenger(player);

						String name = FormatUtil.getFriendlyName(type);
						player.sendMessage(plugin.getPrefix() +
								FormatUtil.format("&eYou are now riding {0} &b{1}", FormatUtil.getArticle(name), name));
						break;
					case ENDER_DRAGON:
						clicked.setPassenger(player);
						player.sendMessage(plugin.getPrefix() + FormatUtil.format("&eYou are a Dragon Tamer, &b{0}&e!", player.getName()));
					default:
						break;
				}
			}
		}
	}

	@Override
	public void reload()
	{
		this.salvagingEnabled = plugin.getConfig().getBoolean("salvaging");
		this.deathCoordinateMessages = plugin.getConfig().getBoolean("deathCoordinateMessages");
		this.fishingEnabled = plugin.getConfig().getBoolean("levelingMethods.fishing.enabled");
		this.fishDropsEnabled = plugin.getConfig().getBoolean("fishDropsEnabled");
		this.speedBoostEnabled = plugin.getConfig().getBoolean("speedBoost.enabled");

		this.fishingGain = plugin.getConfig().getInt("levelingMethods.fishing.xpgain");
		this.speedBoostOdds = plugin.getConfig().getInt("speedBoost.odds");
		this.speedBoostDuration = plugin.getConfig().getInt("speedBoost.duration");
		this.speedBoostStrength = plugin.getConfig().getInt("speedBoost.strength");
	}
}