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

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.BlockDrop;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * @author dmulloy2
 */

public class Fishing extends Module
{
	private int xpGain;
	private boolean drops;

	public Fishing(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("levelingMethods.fishing.enabled", true));
		this.xpGain = plugin.getConfig().getInt("levelingMethods.fishing.xpgain");
		this.drops = plugin.getConfig().getBoolean("fishDropsEnabled");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerFish(PlayerFishEvent event)
	{
		Entity caught = event.getCaught();
		if (caught == null || caught.getType() != EntityType.DROPPED_ITEM)
			return;

		Player player = event.getPlayer();
		if (plugin.isDisabledWorld(player))
			return;

		// Fishing xp gain
		String message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("fishing_gain"), xpGain);
		plugin.getExperienceHandler().handleXpGain(event.getPlayer(), xpGain, message);

		// Fish drops
		if (! drops || player.getGameMode() != GameMode.SURVIVAL)
			return;

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		int level = data.getLevel(10);

		List<BlockDrop> drops = new ArrayList<>();
		for (int i = 0; i < level; i++)
		{
			if (plugin.getFishDropsMap().containsKey(i))
			{
				for (BlockDrop fishDrop : plugin.getFishDropsMap().get(i))
				{
					if (fishDrop.getMaterial() != null && Util.random(fishDrop.getChance()) == 0)
						drops.add(fishDrop);
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
}
