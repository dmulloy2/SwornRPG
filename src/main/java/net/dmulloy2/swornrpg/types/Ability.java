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
package net.dmulloy2.swornrpg.types;

import lombok.Getter;

import org.bukkit.Material;

/**
 * @author dmulloy2
 */

@Getter
public enum Ability
{
	FRENZY(Material.IRON_SWORD, Material.DIAMOND_SWORD),
	SUPER_PICKAXE(Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE),
	UNLIMITED_AMMO;

	private final Material[] materials;
	Ability(Material... materials)
	{
		this.materials = materials;
	}

	public boolean isValidMaterial(Material mat)
	{
		if (materials == null || materials.length == 0)
			return false;

		for (Material material : materials)
		{
			if (material == mat)
				return true;
		}

		return false;
	}
}
