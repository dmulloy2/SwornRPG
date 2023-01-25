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
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Sapling;

/**
 * @author dmulloy2
 */

public class Herbalism extends Module
{
	private int xpGain;

	public Herbalism(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("levelingMethods.herbalism.enabled", true));
		this.xpGain = plugin.getConfig().getInt("levelingMethods.herbalism.xpgain");
	}

	// Herbalism gain
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		if (plugin.isDisabledWorld(player))
			return;

		if (isApplicable(event.getBlock()))
		{
			PlayerData data = plugin.getPlayerDataCache().getData(player);
			int herbalism = data.getHerbalism();
			if (herbalism >= 10)
			{
				int xp = xpGain * 10;
				String message = FormatUtil.format(plugin.getPrefix() + plugin.getMessage("herbalism_gain"), xp);
				plugin.getExperienceHandler().handleXpGain(player, xp, message);
				data.setHerbalism(herbalism - 10);
			}
			else
			{
				data.setHerbalism(herbalism + 1);
			}
		}
	}

	// Instant growth
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		if (plugin.isDisabledWorld(player))
			return;

		Block block = event.getBlock();
		if (! isGrowable(block))
			return;

		PlayerData data = plugin.getPlayerDataCache().getData(player);

		int level = data.getLevel(100);
		if (Util.random(200 / level) == 0)
		{
			boolean message = false;
			Material type = block.getType();
			BlockData dat = block.getBlockData();
			if (dat instanceof Ageable crops)
			{
				crops.setAge(crops.getMaximumAge());
			}
			else if (type.name().contains("SAPLING"))
			{
				// TODO this will probably get removed at some point
				// but there isn't a great way to go from material -> tree type
				Sapling sapling = (Sapling) block.getState().getData();
				TreeSpecies species = sapling.getSpecies();
				TreeType tree = switch (species) {
					case ACACIA -> TreeType.ACACIA;
					case BIRCH -> Util.random(3) == 0 ? TreeType.TALL_BIRCH : TreeType.BIRCH;
					case DARK_OAK -> TreeType.DARK_OAK;
					case GENERIC -> Util.random(3) == 0 ? TreeType.BIG_TREE : TreeType.TREE;
					case JUNGLE -> Util.random(3) == 0 ? TreeType.COCOA_TREE : TreeType.SMALL_JUNGLE;
					case REDWOOD -> Util.random(5) == 0 ? TreeType.MEGA_REDWOOD :
							Util.random(3) == 0 ? TreeType.TALL_REDWOOD : TreeType.REDWOOD;
					default -> TreeType.TREE;
				};

				block.setType(Material.AIR);
				block.getWorld().generateTree(block.getLocation(), tree);
				message = true;
			}
			else if (type == Material.RED_MUSHROOM)
			{
				block.setType(Material.AIR);
				block.getWorld().generateTree(block.getLocation(), TreeType.RED_MUSHROOM);
				message = true;
			}
			else if (type == Material.BROWN_MUSHROOM)
			{
				block.setType(Material.AIR);
				block.getWorld().generateTree(block.getLocation(), TreeType.BROWN_MUSHROOM);
				message = true;
			}

			if (message)
				player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("insta_growth")));
		}
	}

	private boolean isApplicable(Block block)
	{
		BlockData data = block.getBlockData();
		if (data instanceof Ageable crop)
		{
			return crop.getAge() == crop.getMaximumAge();
		}

		return block.getType() == Material.CACTUS || block.getType() == Material.MELON
		       || block.getType() == Material.PUMPKIN;
	}

	private boolean isGrowable(Block block)
	{
		Material material = block.getType();
		BlockData data = block.getBlockData();

		return data instanceof Ageable || material.name().contains("SAPLING")
		       || material == Material.RED_MUSHROOM || material == Material.BROWN_MUSHROOM;
	}
}
