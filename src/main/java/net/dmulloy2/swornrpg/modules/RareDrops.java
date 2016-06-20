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

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.BlockDrop;
import net.dmulloy2.util.Util;

import java.util.concurrent.TimeUnit;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author dmulloy2
 */

public class RareDrops extends Module
{
	private Cache<Location, Boolean> tracked;

	public RareDrops(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("blockDropsEnabled", true));
		if (plugin.getConfig().getBoolean("trackBlockDrops", false))
			tracked = CacheBuilder.newBuilder()
				.expireAfterWrite(5, TimeUnit.MINUTES)
				.build();
		
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreakMonitor(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		Block block = event.getBlock();
		if (plugin.isDisabledWorld(block) || isFactionsApplicable(player, true))
			return;

		Location location = block.getLocation();
		if (tracked != null && tracked.getIfPresent(location) != null)
			return;

		boolean dropped = false;

		Material type = block.getType();
		if (plugin.getBlockDropsMap().containsKey(type))
		{
			for (BlockDrop blockDrop : plugin.getBlockDropsMap().get(type))
			{
				if (Util.random(blockDrop.getChance()) == 0)
				{
					block.getWorld().dropItemNaturally(block.getLocation(), blockDrop.getMaterial().newItemStack(1));
					dropped = true;
				}
			}

			if (plugin.getBlockDropsMap().containsKey(Material.AIR))
			{
				for (BlockDrop blockDrop : plugin.getBlockDropsMap().get(Material.AIR))
				{
					if (Util.random(blockDrop.getChance()) == 0)
					{
						block.getWorld().dropItemNaturally(block.getLocation(), blockDrop.getMaterial().newItemStack(1));
						dropped = true;
					}
				}
			}
		}

		if (dropped && tracked != null)
			tracked.put(location, true);
	}
}
