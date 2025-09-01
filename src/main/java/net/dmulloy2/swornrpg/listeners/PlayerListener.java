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
package net.dmulloy2.swornrpg.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.integration.EssentialsHandler;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornapi.types.Reloadable;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.InventoryUtil;
import net.dmulloy2.swornapi.util.TimeUtil;
import net.dmulloy2.swornapi.util.Util;

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
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

public class PlayerListener implements Listener, Reloadable
{
	private boolean deathCoordinateMessages;
	private boolean speedBoostEnabled;

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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if (! deathCoordinateMessages)
			return;

		Player player = event.getEntity();
		if (plugin.isSwornNationsEnabled() && plugin.getSwornNationsHandler().isApplicable(player, true))
			return;

		// Death coordinates
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isDeathCoordsEnabled())
		{
			Location loc = player.getLocation();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();

			EssentialsHandler handler = plugin.getEssentialsHandler();
			if (handler != null && handler.isEnabled())
			{
				Player killer = plugin.getKiller(player);
				if (killer != null)
				{
					String mail = FormatUtil.format(plugin.getMessage("mail_pvp_format"),
							killer.getName(), x, y, z, loc.getWorld().getName(), TimeUtil.getLongDateCurr());
					handler.sendMail(player, mail);
				}
				else
				{
					String world = player.getWorld().getName();

					String mail = FormatUtil.format(plugin.getMessage("mail_pve_format"), x, y, z, world, TimeUtil.getLongDateCurr());
					handler.sendMail(player, mail);
				}

				player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("death_coords_mail")));
				plugin.debug(plugin.getMessage("log_death_coords"), player.getName(), "sent", "mail message");
			}
			else
			{
				ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
				BookMeta meta = (BookMeta) book.getItemMeta();

				meta.setTitle(FormatUtil.format("&eDeath Coords"));
				meta.setAuthor(FormatUtil.format("&bSwornRPG"));

				List<String> pages = new ArrayList<>();
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
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		data.setSatRecently(false);

		// This code shouldn't execute
		/* Location location = player.getLocation();
		if (isValid(location.getX()) && isValid(location.getY()) && isValid(location.getZ()))
		{
			player.teleport(player.getWorld().getSpawnLocation());
			plugin.getLogHandler().log(Level.INFO, "Corrected invalid position for {0}", player.getName());
		} */
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

		// Attempt to prevent invalid kicks caused by sitting in chairs.
		// This works by checking whether or not the player sat recently
		// and whether or not the kick was valid.  This only works on Spigot,
		// since the kick messages in CraftBukkit are "Nope", which can be for
		// a variety of reasons.

		if (data.isSatRecently() && event.getReason().equals("NaN in position (Hacking?)"))
		{
			// Check if their position is valid
			Location location = player.getLocation();
			if (isValid(location.getX()) && isValid(location.getY()) && isValid(location.getZ()))
			{
				plugin.getLogHandler().log("Blocked invalid kick for {0}", player.getName());
				event.setCancelled(true);
				return;
			}

			// Teleport them to spawn as a fallback
			player.teleport(player.getWorld().getSpawnLocation());

			// Were we successful?
			location = player.getLocation();
			if (isValid(location.getX()) && isValid(location.getY()) && isValid(location.getZ()))
			{
				plugin.getLogHandler().log("Corrected invalid position for {0}", player.getName());
				event.setCancelled(true);
			}
		}
	}

	// Makes sure that 'number' is real and finite
	private static boolean isValid(double number)
	{
		return Math.abs(number) <= Double.MAX_VALUE;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAbilityActivate(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		if (! event.hasItem() || action == Action.PHYSICAL)
			return;

		Block clicked = event.getClickedBlock();
		if (clicked != null)
		{
			// If it's a tile entity, don't try to activate abilities
			if (! clicked.getState().getClass().getName().equals("CraftBlockState"))
				return;
		}

		// Check ability activation
		plugin.getAbilityHandler().checkActivation(event.getPlayer(), action);
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
		if (event.isCancelled())
			return;

		if (! player.isInsideVehicle() && player.getPassengers().isEmpty())
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
					case MOOSHROOM:
					case OCELOT:
					case PIG:
					case SHEEP:
					case SILVERFISH:
					case SKELETON:
					case SLIME:
					case SNOW_GOLEM:
					case SPIDER:
					case SQUID:
					case VILLAGER:
					case WITCH:
					case WITHER:
					case WOLF:
					case ZOMBIE:
						clicked.addPassenger(player);

						String name = FormatUtil.getFriendlyName(type);
						player.sendMessage(plugin.getPrefix() +
								FormatUtil.format("&eYou are now riding {0} &b{1}", FormatUtil.getArticle(name), name));
						break;
					case ENDER_DRAGON:
						clicked.addPassenger(player);
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
		this.deathCoordinateMessages = plugin.getConfig().getBoolean("deathCoordinateMessages");
		this.speedBoostEnabled = plugin.getConfig().getBoolean("speedBoost.enabled");
		this.speedBoostOdds = plugin.getConfig().getInt("speedBoost.odds");
		this.speedBoostDuration = plugin.getConfig().getInt("speedBoost.duration");
		this.speedBoostStrength = plugin.getConfig().getInt("speedBoost.strength");
	}
}
