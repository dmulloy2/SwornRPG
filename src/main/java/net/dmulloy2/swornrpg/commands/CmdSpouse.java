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

public class CmdSpouse extends SwornRPGCommand
{
	public CmdSpouse(SwornRPG plugin)
	{
		super(plugin);
		this.name = "spouse";
		this.addOptionalArg("player");
		this.description = "Check a player's spouse";
		this.permission = Permission.SPOUSE;
	}

	@Override
	public void perform()
	{
		if (! plugin.getConfig().getBoolean("marriage"))
		{
			err(getMessage("command_disabled"));
			return;
		}

		OfflinePlayer target = getTarget(0, hasPermission(Permission.SPOUSE_OTHERS));
		if (target == null)
			return;

		PlayerData data = getPlayerData(target);
		if (data == null)
		{
			err(getMessage("player_not_found"), args[0]);
			return;
		}

		String spouse = data.getSpouse();

		String name = target.getName();
		if (target.getName().equals(sender.getName()))
			name = "You";

		String verb = "is";
		if (target.getName().equals(sender.getName()))
			verb = "are";

		if (spouse != null)
		{
			sendpMessage(getMessage("spouse_married"), name, verb, spouse);
		}
		else
		{
			sendpMessage(getMessage("spouse_not_married"), name, verb);
		}
	}
}
