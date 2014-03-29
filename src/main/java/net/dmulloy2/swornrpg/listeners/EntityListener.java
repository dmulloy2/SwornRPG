package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.types.Reloadable;
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

	/** Axe blowback and Arrow fire **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageMonitor(EntityDamageByEntityEvent event)
	{
		if (event.getDamage() <= 0 || event.isCancelled())
			return;

		Entity damager = event.getDamager();
		Entity defender = event.getEntity();

		if (plugin.isDisabledWorld(damager))
			return;

		if (damager instanceof Arrow)
		{
			if (arrowFireEnabled)
			{
				if (Util.random(arrowFireOdds) == 0)
				{
					defender.setFireTicks(5 * 20);
					if (defender instanceof Player)
					{
						((Player) defender).sendMessage(plugin.getPrefix() +
								FormatUtil.format(plugin.getMessage("fire_damage")));
					}

					Arrow arrow = (Arrow) damager;
					if (arrow.getShooter() instanceof Player)
					{
						((Player) arrow.getShooter()).sendMessage(plugin.getPrefix() +
								FormatUtil.format(plugin.getMessage("fire_damage")));
					}

				}
			}
		}
		else if (damager instanceof Player)
		{
			Player player = (Player) damager;
			ItemStack inHand = player.getItemInHand();

			/** Confusion **/
			if (inHand == null || inHand.getType() == Material.AIR)
			{
				if (confusionEnabled)
				{
					if (Util.random(20) == 0)
					{
						if (defender instanceof Player)
						{
							((Player) defender).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, confusionDuration,
									confusionStrength));
						}
					}
				}
			}

			/** Axe Blowback **/
			String type = FormatUtil.getFriendlyName(inHand.getType());
			if (type.toLowerCase().contains("axe"))
			{
				if (axeKnockbackEnabled)
				{
					if (Util.random(axeKnockbackOdds) == 0)
					{
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
							((Player) defender).sendMessage(plugin.getPrefix() +
									FormatUtil.format(plugin.getMessage("axe_blowbackee"), player.getName(), type));
							defenderName = ((Player) defender).getName();
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

	/** Graceful Roll **/
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
				((Player) entity).sendMessage(plugin.getPrefix() + 
						FormatUtil.format(plugin.getMessage("graceful_roll")));
			}
		}
	}

	/** Succor **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		Entity killer = event.getEntity().getKiller();
		if (killer instanceof Player)
		{
			Player player = (Player) killer;
			double health = player.getHealth();
			if (health + 1.0D <= 20.0D)
			{
				PlayerData data = plugin.getPlayerDataCache().getData(player);

				int level = data.getLevel(25);
				if (Util.random(75 / level) == 0)
				{
					player.setHealth(player.getHealth() + 1.0D);
				}
			}
		}
	}

	/** Insta-Kill **/
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

	// ---- Mob Health ---- //

	/** Mob Health (Spawn) **/
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

	/** Mob Health (Damage) **/
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