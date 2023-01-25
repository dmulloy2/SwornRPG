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
import net.dmulloy2.swornrpg.types.PlayerData;

/**
 * @author dmulloy2
 */

public class CmdCoordsToggle extends SwornRPGCommand
{
	public CmdCoordsToggle(SwornRPG plugin)
	{
		super(plugin);
		this.name = "coordstoggle";
		this.description = "Toggle death coordinate messages";
		this.permission = Permission.COORDSTOGGLE;

		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		PlayerData data = getPlayerData(player);
		if (data.isDeathCoordsEnabled())
		{
			data.setDeathCoordsEnabled(false);
			sendpMessage(plugin.getMessage("deathcoords_disabled"));
		}
		else
		{
			data.setDeathCoordsEnabled(true);
			sendpMessage(plugin.getMessage("deathcoords_enabled"));
		}	
	}
}
