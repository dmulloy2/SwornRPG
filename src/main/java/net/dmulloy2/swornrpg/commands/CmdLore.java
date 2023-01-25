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
package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornapi.util.CompatUtil;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.MaterialUtil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class CmdLore extends SwornRPGCommand
{
	public CmdLore(SwornRPG plugin)
	{
		super(plugin);
		this.name = "lore";
		this.addRequiredArg("lore");
		this.description = "Set the lore of your in-hand item";
		this.permission = Permission.LORE;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		ItemStack inHand = CompatUtil.getItemInMainHand(player);
		if (inHand == null || inHand.getType() == Material.AIR)
		{
			err("You must have an item in your hand to do this!");
			return;
		}

		// Join and fix spacing
		String str = FormatUtil.join(" ", args)
				.replace("\"", "");

		String[] split = str.split("\\|");
		List<String> lore = new ArrayList<>();
		for (String line : split)
			lore.add(FormatUtil.format(line));

		// Apply
		ItemMeta meta = inHand.getItemMeta();
		meta.setLore(lore);
		inHand.setItemMeta(meta);

		sendpMessage("&eYou have set your &b{0}&e''s lore to \"&r{1}&e\"", MaterialUtil.getName(inHand), lore);
	}
}
