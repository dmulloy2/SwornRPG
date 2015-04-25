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
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author dmulloy2
 */

public class BlockListener implements Listener, Reloadable
{
	private boolean ironDoorProtection;

	private final SwornRPG plugin;
	public BlockListener(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.reload(); // Load configuration
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreakHighest(BlockBreakEvent event)
	{
		if (! ironDoorProtection || event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		Block block = event.getBlock();
		if (plugin.isDisabledWorld(block))
			return;

		// Iron door protection
		if (block.getType() == Material.IRON_DOOR_BLOCK)
		{
			event.setCancelled(true);

			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("iron_door_protect")));
			plugin.debug(plugin.getMessage("log_irondoor_protect"), player.getName(), Util.locationToString(block.getLocation()));
		}
	}

	@Override
	public void reload()
	{
		this.ironDoorProtection = plugin.getConfig().getBoolean("ironDoorProtection");
	}
}
