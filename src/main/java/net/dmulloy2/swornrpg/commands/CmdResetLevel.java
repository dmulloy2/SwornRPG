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

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */

public class CmdResetLevel extends SwornRPGCommand
{
	public CmdResetLevel(SwornRPG plugin)
	{
		super(plugin);
		this.name = "resetlevel";
		this.aliases.add("levelr");
		this.addOptionalArg("player");
		this.description = "Reset a player's level";
		this.permission = Permission.LEVEL_RESET;

		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		OfflinePlayer target = getTarget(0, true);
		if (target == null)
			return;

		PlayerData data = getPlayerData(target);
		if (data == null)
		{
			err(getMessage("player_not_found"), args[0]);
			return;
		}

		data.setPlayerxp(0);
		data.setLevel(0);
		data.setTotalxp(0);
		data.setXpneeded(100);

		sendpMessage(getMessage("level_reset_resetter"), target.getName());
		if (target.isOnline())
		{
			sendpMessage(target.getPlayer(), getMessage("level_reset_reset"));
		}
	}
}
