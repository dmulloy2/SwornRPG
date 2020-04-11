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
import net.dmulloy2.util.CompatUtil;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.MaterialUtil;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class Salvaging extends Module
{
	public Salvaging(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("salvaging", true));
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Block block = event.getClickedBlock();
		if (block == null || event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		Player player = event.getPlayer();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;

		if (plugin.isDisabledWorld(block))
			return;

		String blockType = "";
		if (block.getType() == Material.IRON_BLOCK)
			blockType = "Iron";
		if (block.getType() == Material.GOLD_BLOCK)
			blockType = "Gold";
		if (block.getType() == Material.DIAMOND_BLOCK)
			blockType = "Diamond";

		if (! blockType.isEmpty())
		{
			if (block.getRelative(-1, 0, 0).getType() == Material.FURNACE
					|| block.getRelative(1, 0, 0).getType() == Material.FURNACE
					|| block.getRelative(0, 0, -1).getType() == Material.FURNACE
					|| block.getRelative(0, 0, 1).getType() == Material.FURNACE)
			{
				ItemStack item = CompatUtil.getItemInMainHand(player);
				Material type = item.getType();

				double mult;

				ItemMeta meta = item.getItemMeta();
				if (meta instanceof Damageable)
				{
					mult = 1.0D - ((double) ((Damageable) meta).getDamage() / item.getType().getMaxDurability());
				}
				else
				{
					mult = 0.0D;
				}

				double amt = 0.0D;

				if (plugin.getSalvageRef().get(blockType.toLowerCase()).containsKey(type))
					amt = Math.round(plugin.getSalvageRef().get(blockType.toLowerCase()).get(type) * mult);

				if (amt > 0.0D)
				{
					String article = FormatUtil.getArticle(blockType);
					String materialExtension = blockType.equals("Diamond") ? "" : " ingot";
					String plural = amt > 1.0D ? "s" : "";
					String itemName = MaterialUtil.getName(item);

					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("salvage_success"),
							article, itemName, amt, blockType.toLowerCase(), materialExtension, plural));

					plugin.log(plugin.getMessage("log_salvage"), player.getName(), itemName, amt, blockType.toLowerCase(),
							materialExtension, plural);

					PlayerInventory inv = player.getInventory();
					inv.removeItem(item);

					Material give = null;
					if (blockType.equals("Iron"))
						give = Material.IRON_INGOT;
					if (blockType.equals("Gold"))
						give = Material.GOLD_INGOT;
					if (blockType.equals("Diamond"))
						give = Material.DIAMOND;

					ItemStack salvaged = new ItemStack(give, (int) amt);
					InventoryUtil.giveItem(player, salvaged);
					event.setCancelled(true);
				}
				else
				{
					String itemName = MaterialUtil.getName(item);
					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("not_salvagable"), itemName,
							blockType.toLowerCase()));
				}
			}
		}
	}
}
