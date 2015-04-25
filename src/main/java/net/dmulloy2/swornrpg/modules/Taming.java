/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornrpg.modules;

import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTameEvent;

/**
 * @author dmulloy2
 */

public class Taming extends Module
{
	private int xpGain;

	public Taming(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("levelingMethods.taming.enabled", true));
		this.xpGain = plugin.getConfig().getInt("levelingMethods.taming.xpgain");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityTame(EntityTameEvent event)
	{
		AnimalTamer owner = event.getOwner();
		if (owner instanceof Player)
		{
			Player player = (Player) owner;
			if (! plugin.isDisabledWorld(player))
			{
				// XP Gain
				String mobname = FormatUtil.getFriendlyName(event.getEntity().getType());
				String article = FormatUtil.getArticle(mobname);

				String message = plugin.getPrefix() +
						FormatUtil.format(plugin.getMessage("taming_gain"), xpGain, article, mobname);
				plugin.getExperienceHandler().handleXpGain(player, xpGain, message);

				// Wolf/Ocelot's Pal
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

				// Taming Bomb!
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
								Tameable tameable = (Tameable) entity;
								if (! tameable.isTamed())
								{
									tameable.setOwner(player);
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
}