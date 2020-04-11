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
import net.dmulloy2.util.FormatUtil;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdEject extends SwornRPGCommand
{
	public CmdEject(SwornRPG plugin)
	{
		super(plugin);
		this.name = "eject";
		this.description = "Remove a player from your head";
		this.permission = Permission.EJECT;
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (player.getPassengers().isEmpty())
		{
			err(getMessage("no_passenger"));
			return;
		}

		for (Entity passenger : player.getPassengers())
		{
			String name;
			if (passenger instanceof Player)
			{
				name = passenger.getName();
			}
			else
			{
				String type = FormatUtil.getFriendlyName(passenger.getType());
				String article = FormatUtil.getArticle(type);

				name = article + " " + type;
			}

			if (player.eject())
			{
				sendMessage(getMessage("eject_successful"), name);
			}
			else
			{
				err(getMessage("eject_error"), name);
			}
		}
	}
}
