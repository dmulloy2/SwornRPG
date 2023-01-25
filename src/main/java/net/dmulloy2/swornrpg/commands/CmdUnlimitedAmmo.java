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
import net.dmulloy2.swornrpg.types.Ability;
import net.dmulloy2.swornrpg.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdUnlimitedAmmo extends AbstractAbilityCommand
{
	public CmdUnlimitedAmmo(SwornRPG plugin)
	{
		super(plugin, Ability.UNLIMITED_AMMO);
		this.name = "unlimitedammo";
		this.aliases.add("ammo");
		this.description = "Activate unlimited ammo ability";
		this.permission = Permission.UNLIMITEDAMMO;
	}
}
