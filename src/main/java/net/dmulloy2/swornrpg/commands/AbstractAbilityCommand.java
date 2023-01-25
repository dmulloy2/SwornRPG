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

/**
 * @author dmulloy2
 */

public abstract class AbstractAbilityCommand extends SwornRPGCommand
{
	protected Ability ability;
	public AbstractAbilityCommand(SwornRPG plugin, Ability ability)
	{
		super(plugin);
		this.ability = ability;
		this.mustBePlayer = true;
	}

	@Override
	public final void perform()
	{
		plugin.getAbilityHandler().commandActivation(player, ability);
	}
}
