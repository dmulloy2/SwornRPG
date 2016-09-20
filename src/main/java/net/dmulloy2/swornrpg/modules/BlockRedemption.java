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
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.MaterialUtil;
import net.dmulloy2.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class BlockRedemption extends Module
{
	private List<Material> redemptionBlacklist;
	private int universalChance;

	public BlockRedemption(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("redemptionEnabled", true));
		this.redemptionBlacklist = MaterialUtil.fromStrings(plugin.getConfig().getStringList("redemptionBlacklist"));
		this.universalChance = plugin.getConfig().getInt("redemptionChance", -1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		final Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		Block block = event.getBlock();
		if (plugin.isDisabledWorld(block))
			return;

		Material material = block.getType();
		if (! material.isBlock() || redemptionBlacklist.contains(material))
			return;

		PlayerData data = plugin.getPlayerDataCache().getData(player);

		int chance = universalChance != -1 ? universalChance : 300 / data.getLevel(100);
		if (Util.random(chance) == 0)
		{
			final ItemStack itemStack = new ItemStack(material);
			MaterialData materialData = block.getState().getData();
			if (materialData != null)
				itemStack.setData(materialData);

			class DelayedGiveTask extends BukkitRunnable
			{
				@Override
				public void run()
				{
					InventoryUtil.giveItem(player, itemStack);

					String itemName = MaterialUtil.getName(itemStack);
					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("building_redeem"), itemName));
				}
			}

			// Run the next tick to hopefully fix duplication
			new DelayedGiveTask().runTaskLater(plugin, 20L);
		}
	}
}
