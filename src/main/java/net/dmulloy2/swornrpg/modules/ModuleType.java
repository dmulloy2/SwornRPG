/**
 * SwornRPG - a bukkit plugin
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.swornrpg.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the currently supported module types.
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public enum ModuleType
{
	BASIC("basic", Module.class),
	TICKABLE("tickable", TickableModule.class),
	;

	private final String name;
	private final Class<?> clazz;

	/**
	 * Gets a ModuleType by its name. Returns {@link #BASIC} if name is null or
	 * empty.
	 * 
	 * @param name module name, can be null
	 * @return The ModuleType or null if it does not exist.
	 */
	public static ModuleType getByName(String name)
	{
		if (name == null || name.isEmpty())
			return BASIC;

		for (ModuleType type : values())
		{
			if (type.getName().equalsIgnoreCase(name))
				return type;
		}

		return null;
	}
}