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
package net.dmulloy2.swornrpg.modules;

import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.Util;

import org.bukkit.entity.*;
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
		if (owner instanceof Player player)
		{
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
					else if (event.getEntity() instanceof Cat)
					{
						Cat cat = (Cat) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.CAT);
						cat.setOwner(player);
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
							if (entity instanceof Tameable tameable)
							{
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
