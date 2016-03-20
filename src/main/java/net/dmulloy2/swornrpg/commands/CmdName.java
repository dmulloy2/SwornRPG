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

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.util.CompatUtil;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class CmdName extends SwornRPGCommand
{
	public CmdName(SwornRPG plugin)
	{
		super(plugin);
		this.name = "name";
		this.aliases.add("iname");
		this.addRequiredArg("name");
		this.description = "Set the name of your in-hand item";
		this.permission = Permission.NAME;
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
		String name = FormatUtil.join(" ", args);
		name = name.replaceAll("_", " ");
		name = name.replaceAll("\"", "");

		// Format
		name = FormatUtil.format(name);

		// Apply
		ItemMeta meta = inHand.getItemMeta();
		meta.setDisplayName(name);
		inHand.setItemMeta(meta);

		sendpMessage("&eYou have set your &b{0}&e''s name to \"&r{1}&e\"", FormatUtil.getFriendlyName(inHand.getType()), name);
	}
}
