package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
	public void onEntityDamage(EntityDamageEvent event)
	{
		try 
		{
			if (event.getDamage() <= 0) 
				return;
			
			Entity att = ((EntityDamageByEntityEvent)event).getDamager();
			LivingEntity defender = (LivingEntity)event.getEntity();
	
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
				if (plugin.axekb == true)
				{
					Player p = (Player)att;
					String gun = p.getItemInHand().getType().toString().toLowerCase();
					if (gun.contains("_axe")) 
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
							((Player)defender).sendMessage(FormatUtil.format(plugin.getMessage("axe_blowbackee"), ((Player)att).getName(), inhand));
							((Player)att).sendMessage(FormatUtil.format(plugin.getMessage("axe_blowbacker"), ((Player)defender).getName(), inhand));
						}
					}
				}
			}
		}
		catch (Exception localException)
		{
		}
	}
}