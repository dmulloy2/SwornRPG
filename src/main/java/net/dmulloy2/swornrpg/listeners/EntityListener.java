package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

public class EntityListener implements Listener
{
	private final SwornRPG plugin;
	public EntityListener(SwornRPG plugin)
	{
		this.plugin = plugin;
	}

	/** Axe blowback and Arrow fire **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageMonitor(EntityDamageByEntityEvent event)
	{
		if (event.getDamage() <= 0 || event.isCancelled())
			return;

		Entity att = event.getDamager();
		Entity defender = event.getEntity();

		if (plugin.isDisabledWorld(att))
			return;

		if (att instanceof Arrow)
		{
			if (plugin.isArrowfire())
			{
				if (Util.random(10) == 0)
				{
					defender.setFireTicks(128);
					if (((Arrow) att).getShooter() instanceof Player)
						((Player) ((Arrow) att).getShooter())
								.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("fire_damage")));
					if (defender instanceof Player)
						((Player) defender).sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("fire_damage")));
				}
			}
		}
		else if (att instanceof Player)
		{
			Player p = (Player) att;
			ItemStack inHand = p.getItemInHand();
			
			/** Confusion **/
			if (inHand == null || inHand.getType() == Material.AIR)
			{
				if (plugin.isConfusion())
				{
					int rand = Util.random(20);
					if (rand == 0)
					{
						if (defender instanceof Player)
						{
							PotionEffect eff = new PotionEffect(PotionEffectType.CONFUSION, plugin.getConfusionduration(), 1);
							((Player) defender).addPotionEffect(eff);
						}
					}
				}
			}
			
			String gun = FormatUtil.getFriendlyName(inHand.getType());

			/** Axe Blowback **/
			if (gun.toLowerCase().contains("axe"))
			{
				if (plugin.isAxekb())
				{
					int randomBlowBack = Util.random(9);
					if (randomBlowBack == 0)
					{
						double distance = Util.pointDistance(att.getLocation(), defender.getLocation());
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

						Vector v = defender.getLocation().add(0.0D, 0.875D, 0.0D).subtract(att.getLocation()).toVector();
						Vector v2 = new Vector(v.getX() * mult, v.getY() * mult, v.getZ() * mult);
						if (v2.getY() > 1.0D)
							v2.setY(1.0D);

						defender.setVelocity(v2.multiply(0.8D));

						String defenderName;
						if (defender instanceof Player)
						{
							((Player) defender).sendMessage(plugin.getPrefix()
									+ FormatUtil.format(plugin.getMessage("axe_blowbackee"), p.getName(), gun));

							defenderName = ((Player) defender).getName();
						}
						else
						{
							defenderName = FormatUtil.getFriendlyName(defender.getType());
						}

						p.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("axe_blowbacker"), defenderName, gun));
					}
				}
			}
		}
	}

	/** Graceful Roll / Health bar **/
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageLowest(EntityDamageEvent event)
	{
		if (event.isCancelled() || event.getDamage() <= 0)
			return;

		Entity entity = event.getEntity();
		if (entity instanceof Player)
		{
			Player player = (Player) entity;

			/** Graceful Roll **/
			if (event.getCause() == DamageCause.FALL)
			{
				if (! plugin.isGracefulroll())
					return;

				int rand = Util.random(plugin.getGracefulrollodds());
				if (rand == 0)
				{
					event.setDamage(0);
					player.sendMessage(FormatUtil.format(plugin.getPrefix() + plugin.getMessage("graceful_roll")));
				}
			}
		}

		/** Health Bar **/
		if (entity instanceof LivingEntity)
		{
			plugin.getHealthBarHandler().updateHealth((LivingEntity) entity);
		}
	}

	/** Mob Health (Regain) **/
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		/** Succor **/
		Entity killer = event.getEntity().getKiller();
		if (killer instanceof Player)
		{
			Player player = (Player) killer;
			double health = player.getHealth();
			if (health + 1.0D <= 20.0D)
			{
				PlayerData data = plugin.getPlayerDataCache().getData(player);
				int level = data.getLevel();
				if (level > 25)
					level = 25;
				if (level == 0)
					level = 1;

				int rand = Util.random(75 / level);
				if (rand == 0)
				{
					player.setHealth(player.getHealth() + 1.0D);
				}
			}
		}
	}

	/** Mob Health (Spawn) **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if (! event.isCancelled())
		{
			Entity entity = event.getEntity();
			if (entity instanceof LivingEntity)
			{
				plugin.getHealthBarHandler().updateHealth((LivingEntity) entity);
			}
		}
	}

	/** Insta-Kill **/
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (event.isCancelled() || event.getDamage() <= 0)
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

					if (Util.random(100) == 0)
					{
						lentity.setHealth(0.0D);

						player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("insta_kill")));
					}
				}
			}
		}
	}
}