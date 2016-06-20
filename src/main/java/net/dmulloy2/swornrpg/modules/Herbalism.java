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
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.CropState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.NetherWarts;
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
				data.setHerbalism(0);
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

		int level = data.getLevel(75);
		if (Util.random(100 - level) == 0)
		{
			boolean message = false;
			BlockState state = block.getState();
			Material type = state.getType();
			MaterialData dat = state.getData();
			if (dat instanceof NetherWarts)
			{
				((NetherWarts) dat).setState(NetherWartsState.RIPE);
				state.update();
				message = true;
			}
			else if (dat instanceof Crops)
			{
				((Crops) dat).setState(CropState.RIPE);
				state.update();
				message = true;
			}
			else if (dat instanceof CocoaPlant)
			{
				((CocoaPlant) dat).setSize(CocoaPlantSize.LARGE);
				state.update();
				message = true;
			}
			else if (type == Material.SAPLING)
			{
				Sapling sapling = (Sapling) block.getState().getData();
				TreeSpecies species = sapling.getSpecies();
				TreeType tree = TreeType.TREE;
				switch (species)
				{
					case ACACIA:
						tree = TreeType.ACACIA;
						break;
					case BIRCH:
						tree = Util.random(3) == 0 ? TreeType.TALL_BIRCH : TreeType.BIRCH;
						break;
					case DARK_OAK:
						tree = TreeType.DARK_OAK;
						break;
					case GENERIC:
						tree = Util.random(3) == 0 ? TreeType.BIG_TREE : TreeType.TREE;
						break;
					case JUNGLE:
						tree = Util.random(3) == 0 ? TreeType.COCOA_TREE : TreeType.SMALL_JUNGLE;
						break;
					case REDWOOD:
						tree = Util.random(5) == 0 ? TreeType.MEGA_REDWOOD : 
							   Util.random(3) == 0 ? TreeType.TALL_REDWOOD : TreeType.REDWOOD;
						break;
					default:
						tree = TreeType.TREE;
						break;
				}

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
		BlockState state = block.getState();
		switch (state.getType())
		{
			case CACTUS:
			case MELON_BLOCK:
			case PUMPKIN:
				return true;
			case CROPS:
				return ((Crops) state.getData()).getState() == CropState.RIPE;
			case NETHER_WARTS:
				return ((NetherWarts) state.getData()).getState() == NetherWartsState.RIPE;
			case COCOA:
				return ((CocoaPlant) state.getData()).getSize() == CocoaPlantSize.LARGE;
			default:
				return false;
		}
	}

	private boolean isGrowable(Block block)
	{
		BlockState state = block.getState();
		Material material = block.getType();
		MaterialData data = state.getData();

		return data instanceof NetherWarts || data instanceof Crops || data instanceof CocoaPlant
				|| material == Material.SAPLING || material == Material.RED_MUSHROOM || material == Material.BROWN_MUSHROOM;
	}
}
