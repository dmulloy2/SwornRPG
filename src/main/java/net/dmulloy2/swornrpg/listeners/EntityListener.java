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

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * @author dmulloy2
 */

public class EntityListener implements Listener, Reloadable
{
	private boolean arrowFireEnabled;
	private boolean axeKnockbackEnabled;
	private boolean confusionEnabled;
	private boolean gracefulRollEnabled;
	private boolean instaKillEnabled;

	private int arrowFireOdds;
	private int axeKnockbackOdds;
	private int confusionDuration;
	private int confusionStrength;
	private int gracefulRollOdds;
	private int instaKillOdds;

	private final SwornRPG plugin;
	public EntityListener(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.reload(); // Load configuration
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageMonitor(EntityDamageByEntityEvent event)
	{
		if (event.getDamage() <= 0 || event.isCancelled())
			return;

		Entity damager = event.getDamager();

		if (plugin.isDisabledWorld(damager))
			return;

		// Arrow fire
		if (damager instanceof Arrow)
		{
			if (arrowFireEnabled)
			{
				if (Util.random(arrowFireOdds) == 0)
				{
					Entity defender = event.getEntity();
					defender.setFireTicks(5 * 20);

					if (defender instanceof Player)
					{
						Player player = (Player) defender;
						player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("fire_damage")));
					}

					Arrow arrow = (Arrow) damager;
					if (arrow.getShooter() instanceof Player)
					{
						Player player = (Player) arrow.getShooter();
						player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("fire_damage")));
					}
				}
			}
		}
		else if (damager instanceof Player)
		{
			Player player = (Player) damager;
			ItemStack inHand = player.getInventory().getItemInMainHand();

			// Confusion
			if (inHand == null || inHand.getType() == Material.AIR)
			{
				if (confusionEnabled)
				{
					if (Util.random(20) == 0)
					{
						Entity defender = event.getEntity();
						if (defender instanceof Player)
						{
							Player confused = (Player) defender;
							confused.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, confusionDuration, confusionStrength));
						}
					}
				}

				return;
			}

			// Axe blowback
			String type = FormatUtil.getFriendlyName(inHand.getType());
			if (type.toLowerCase().contains("axe"))
			{
				if (axeKnockbackEnabled)
				{
					if (Util.random(axeKnockbackOdds) == 0)
					{
						Entity defender = event.getEntity();
						double distance = damager.getLocation().distance(defender.getLocation());
						double mult = 0.75D;
						if (distance < 10.0D)
							mult = 0.25D;
						if (distance < 5.0D)
							mult = 0.45D;
						if (distance < 4.0D)
							mult = 0.75D;
						if (distance < 3.0D)
							mult = 1.0D;
						if (distance < 2.0D)
							mult = 1.125D;

						Vector v = defender.getLocation().add(0.0D, 0.875D, 0.0D).subtract(defender.getLocation()).toVector();
						Vector v2 = new Vector(v.getX() * mult, v.getY() * mult, v.getZ() * mult);
						if (v2.getY() > 1.0D)
							v2.setY(1.0D);

						defender.setVelocity(v2.multiply(0.8D));

						String defenderName;
						if (defender instanceof Player)
						{
							Player blownBack = (Player) defender;
							blownBack.sendMessage(plugin.getPrefix() +
									FormatUtil.format(plugin.getMessage("axe_blowbackee"), player.getName(), type));
							defenderName = blownBack.getName();
						}
						else
						{
							defenderName = FormatUtil.getFriendlyName(defender.getType());
						}

						player.sendMessage(plugin.getPrefix() +
								FormatUtil.format(plugin.getMessage("axe_blowbacker"), defenderName, type));
					}
				}
			}
		}
	}

	// Graceful roll
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageLowest(EntityDamageEvent event)
	{
		if (event.isCancelled() || event.getDamage() <= 0 || ! gracefulRollEnabled)
			return;

		if (event.getCause() != DamageCause.FALL)
			return;

		Entity entity = event.getEntity();
		if (plugin.isDisabledWorld(entity))
			return;

		if (entity instanceof Player)
		{
			if (Util.random(gracefulRollOdds) == 0)
			{
				event.setDamage(0);
				Player player = (Player) entity;
				player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("graceful_roll")));
			}
		}
	}

	// Life steal
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		Entity killer = event.getEntity().getKiller();
		if (killer instanceof Player)
		{
			Player player = (Player) killer;
			double health = player.getHealth();
			double maxHealth = player.getMaxHealth();

			if (health > 0.0D && health < maxHealth)
			{
				PlayerData data = plugin.getPlayerDataCache().getData(player);
				int level = data.getLevel(25);

				if (Util.random(75 / level) == 0)
				{
					player.setHealth(Math.min(health + 1.0D, maxHealth));

					double heartsStolen = (player.getHealth() - health) / 2;
					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("life_steal"),
							heartsStolen, getName(event.getEntity())));
				}
			}
		}
	}

	// Instakill
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (event.isCancelled() || event.getDamage() <= 0 || ! instaKillEnabled)
			return;

		Entity damaged = event.getEntity();
		if (plugin.isDisabledWorld(damaged))
			return;

		if (! (damaged instanceof Player))
		{
			Entity damager = event.getDamager();
			if (damager instanceof Player)
			{
				Player player = (Player) damager;
				if (player.getGameMode() == GameMode.CREATIVE)
					return;

				if (damaged instanceof LivingEntity)
				{
					LivingEntity lentity = (LivingEntity) damaged;
					if (lentity.getMaxHealth() < 100.0D)
					{
						if (Util.random(instaKillOdds) == 0)
						{
							lentity.setHealth(0.0D);
							player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("insta_kill")));
						}
					}
				}
			}
		}
	}

	// ---- Mob Health

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if (event.isCancelled())
			return;

		Entity entity = event.getEntity();
		if (entity instanceof LivingEntity)
		{
			plugin.getHealthBarHandler().updateHealth((LivingEntity) entity);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageMonitor(EntityDamageEvent event)
	{
		if (event.isCancelled() || event.getDamage() <= 0.0D)
			return;

		Entity entity = event.getEntity();
		if (entity instanceof LivingEntity)
		{
			plugin.getHealthBarHandler().updateHealth((LivingEntity) entity);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityRegainHealth(EntityRegainHealthEvent event)
	{
		if (event.isCancelled() || event.getAmount() <= 0.0D)
			return;

		Entity entity = event.getEntity();
		if (entity instanceof LivingEntity)
		{
			plugin.getHealthBarHandler().updateHealth((LivingEntity) entity);
		}
	}

	// ---- Utility Methods

	public static final String getName(Entity entity)
	{
		Player player = getPlayer(entity);
		if (player != null)
			return player.getName();

		return FormatUtil.getFriendlyName(entity.getType());
	}

	public static final Player getPlayer(Entity entity)
	{
		if (entity instanceof Player)
		{
			return (Player) entity;
		}

		if (entity instanceof Projectile)
		{
			Projectile proj = (Projectile) entity;
			if (proj.getShooter() instanceof Player)
				return (Player) proj.getShooter();
		}

		return null;
	}

	@Override
	public void reload()
	{
		this.arrowFireEnabled = plugin.getConfig().getBoolean("arrowFire.enabled");
		this.axeKnockbackEnabled = plugin.getConfig().getBoolean("axeKnockback.enabled");
		this.confusionEnabled = plugin.getConfig().getBoolean("confusion.enabled");
		this.gracefulRollEnabled = plugin.getConfig().getBoolean("gracefulRoll.enabled");
		this.instaKillEnabled = plugin.getConfig().getBoolean("instaKill.enabled");

		this.arrowFireOdds = plugin.getConfig().getInt("arrowFire.odds");
		this.axeKnockbackOdds = plugin.getConfig().getInt("axeKnockback.odds");
		this.confusionDuration = plugin.getConfig().getInt("confusion.duration");
		this.confusionStrength = plugin.getConfig().getInt("confusion.strength");
		this.gracefulRollOdds = plugin.getConfig().getInt("gracefulRoll.odds");
		this.instaKillOdds = plugin.getConfig().getInt("instaKill.odds");
	}
}
