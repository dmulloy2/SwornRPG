package net.dmulloy2.swornrpg.listeners;

import java.util.logging.Level;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.GameMode;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * @author orange451
 * @editor dmulloy2
 */

public class EntityListener implements Listener 
{

	public SwornRPG plugin;
	
	public EntityListener(SwornRPG plugin)
	{
		this.plugin = plugin;
	}

	/**Axe blowback and Arrow fire**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageByEntityEvent event)
	{
		try 
		{
			if (event.getDamage() <= 0) 
				return;
			
			Entity att = event.getDamager();
			Entity defender = event.getEntity();
	
			if (att instanceof Arrow)
			{
				if (plugin.arrowfire == true)
				{
					if (Util.random(10) == 0) 
					{
						defender.setFireTicks(128);
						if ((((Arrow)att).getShooter() instanceof Player))
							((Player)((Arrow)att).getShooter()).sendMessage(FormatUtil.format(plugin.getMessage("fire_damage"))); 
						if (((Player)defender) instanceof Player)
							((Player)defender).sendMessage(FormatUtil.format(plugin.getMessage("fire_damage")));
					}
				}
			}
			else if (att instanceof Player)
			{
				Player p = (Player)att;
				String gun = p.getItemInHand().getType().toString().toLowerCase();
				if (gun == null || gun.contains("air"))
				{
					int rand = Util.random(20);
					if (rand == 0)
					{
						if (defender instanceof Player)
						{
							Player d = (Player)defender;
							d.addPotionEffect(PotionEffectType.CONFUSION.createEffect(60, 1));
						}
					}
				}
				if (gun.contains("_axe")) 
				{
					if (plugin.axekb == true)
					{
						int randomBlowBack = Util.random(9);
						if (randomBlowBack == 0) 
						{
							double distance = Util.point_distance(att.getLocation(), defender.getLocation());
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
							
							String inhand = gun.replaceAll("_", " ");
							if (defender instanceof Player)
								((Player)defender).sendMessage(FormatUtil.format(plugin.prefix + plugin.getMessage("axe_blowbackee"), ((Player)att).getName(), inhand));
							if (att instanceof Player)
								((Player)att).sendMessage(FormatUtil.format(plugin.prefix + plugin.getMessage("axe_blowbacker"), ((Player)defender).getName(), inhand));
						}
					}
				}
			}
		}
		catch (Exception localException)
		{
			if (plugin.debug) plugin.outConsole(plugin.getMessage("log_error_damage"), localException.getMessage());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage2(EntityDamageEvent event)
	{
		/**Mob Health (Damage)**/
		Entity entity = event.getEntity();
		if (entity instanceof Player)
		{
			try 
			{
				plugin.getPlayerHealthBar().updateHealth((Player)entity);
			}
			catch (NoSuchMethodException | IllegalStateException e)
			{
				plugin.outConsole(Level.SEVERE, plugin.getMessage("log_health_error"), e.getMessage());
			}
		}
		else if (entity instanceof LivingEntity)
		{
			plugin.updateHealthTag(entity);
		}
		
		if (event.getCause() == DamageCause.FALL)
		{
			if (event.getDamage() == 0)
				return;
			
			if (!(event.getEntity() instanceof Player))
				return;
			
			int rand = Util.random(50);
			if (rand == 0)
			{
				event.setDamage(0);
				((Player) event.getEntity()).sendMessage(FormatUtil.format(plugin.prefix + plugin.getMessage("graceful_roll")));
			}
		}
	}
	
	/**Mob Health (Regain)**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityRegainHealth(EntityRegainHealthEvent event)
	{
		Entity entity = event.getEntity();
		if (entity instanceof Player)
		{
			try 
			{
				plugin.getPlayerHealthBar().updateHealth((Player)entity);
			}
			catch (NoSuchMethodException | IllegalStateException e)
			{
				plugin.outConsole(Level.SEVERE, plugin.getMessage("log_health_error"), e.getMessage());
			}
		}
		else if (entity instanceof LivingEntity)
		{
			plugin.updateHealthTag(entity);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		/**Succor**/
		if (event.getEntity().getKiller() instanceof Player)
		{
			Player player = (Player)event.getEntity().getKiller();
			if (player == null)
				return;
			
			int health = player.getHealth();
			if (health < 20)
			{
				PlayerData data = plugin.getPlayerDataCache().getData(player);
				int level = data.getLevel();
				if (level > 25) level = 25;
				if (level == 0) level = 1;
				
				int rand = Util.random(75/level);
				if (rand == 0)
				{
					player.setHealth(player.getHealth() + 1);
				}
			}
			// Hunting Drops Coming Soon... Maybe O.o
		}
	}
	
	/**Mob Health (Spawn)**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		Entity entity = event.getEntity();
		plugin.updateHealthTag(entity);
	}
	
	/**Insta-Kill**/
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof Player)
			return;
		
		if (!(event.getDamager() instanceof Player))
			return;
		
		if (event.getEntity() instanceof LivingEntity)
		{
			LivingEntity entity = (LivingEntity)event.getEntity();
			if (((Player)event.getDamager()).getGameMode() == GameMode.CREATIVE)
				return;
		
			int rand = Util.random(100);
			if (rand == 0)
			{
				entity.setHealth(0);
				((Player)event.getDamager()).sendMessage(FormatUtil.format(plugin.getMessage("insta_kill")));
			}
		}
	}
}