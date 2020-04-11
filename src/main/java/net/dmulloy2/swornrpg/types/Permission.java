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
import net.dmulloy2.types.IPermission;

/**
 * @author dmulloy2
 */

@Getter
public enum Permission implements IPermission
{
	ABILITIES,
	ABILITIES_OTHERS,
	ADDXP,
	ADMINCHAT,
	ADMINSAY,
	COORDSTOGGLE,
	DENY,
	DIVORCE,
	EJECT,
	FRENZY,
	HAT,
	HIGHCOUNCIL,
	LEADERBOARD,
	LEVEL,
	LEVEL_OTHERS,
	LEVEL_RESET,
	LORE,
	MARRY,
	NAME,
	PROPOSE,
	RELOAD,
	RIDE,
	SITDOWN,
	SPOUSE,
	SPOUSE_OTHERS,
	STAFFLIST,
	STANDUP,
	SUPERPICKAXE,
	TAG,
	TAG_OTHERS,
	TAG_RESET,
	TAG_RESET_OTHERS,
	UNLIMITEDAMMO,
	UNRIDE,
	VERSION,
	
	STAFF(false),
	UPDATE_NOTIFY(false),
	;

	private String node;
	Permission(boolean command)
	{
		this.node = toString().toLowerCase().replaceAll("_", ".");
		
		if (command) 
			node = "cmd." + node;
	}

	Permission()
	{
		this(true);
	}
}
