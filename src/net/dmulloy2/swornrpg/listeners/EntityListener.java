package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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

	public void onEntityDeath(EntityDeathEvent event)
	{
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event)
	{
		try 
		{
			if (event.getDamage() <= 0) 
			{
				return;
			}
			Entity att = ((EntityDamageByEntityEvent)event).getDamager();
			LivingEntity defender = (LivingEntity)event.getEntity();
			
			//Checks to see if arrow fire is enabled in the config
			if (plugin.arrowfire == true)
			{
				if ((att instanceof Arrow)) 
				{
					if (Util.random(10) == 0) 
					{
						defender.setFireTicks(128);
						//Causes fire damage  
						if ((((Arrow)att).getShooter() instanceof Player))
							((Player)((Arrow)att).getShooter()).sendMessage(ChatColor.GOLD + "Fire Damage!");  
					}
				}
			}
      
			else if ((att instanceof Player))
			{
				Player p = (Player)att;
				String gun = p.getItemInHand().getType().toString().toLowerCase();
				if (gun.contains("_axe"))
				{
					//Checks to see if axe knockback is enabled in the config
					if (plugin.axekb == true)
					{
						//TODO: Either nerf blowback or make the distance configurable
						//Blows the player back
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
							{
								v2.setY(1.0D);
							}
							defender.setVelocity(v2.multiply(0.8D));
							try 
							{ 
								((Player)defender).sendMessage(ChatColor.GRAY + ((Player)att).getName() + " has blown you back with his axe"); 
							}
							catch (Exception localException)
							{
							}
						}
					}
				}
			}
		}
		catch (Exception localException1)
		{
		}
	}
}