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
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.Util;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */

public class CmdDivorce extends SwornRPGCommand
{
	public CmdDivorce(SwornRPG plugin)
	{
		super(plugin);
		this.name = "divorce";
		this.description = "Divorce your spouse";
		this.permission = Permission.DIVORCE;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		PlayerData data = getPlayerData(player);

		String spouse = data.getSpouse();
		if (spouse == null)
		{
			err(plugin.getMessage("not_married"));
			return;
		}

		OfflinePlayer target = Util.matchOfflinePlayer(spouse);
		if (target == null)
		{
			sendpMessage(plugin.getMessage("divorce_plaintiff"), spouse);
			data.setSpouse(null);
			return;
		}

		PlayerData targetData = getPlayerData(target);
		if (targetData == null)
		{
			sendpMessage(plugin.getMessage("divorce_plaintiff"), spouse);
			data.setSpouse(null);
			return;
		}

		data.setSpouse(null);
		targetData.setSpouse(null);
		if (target.isOnline())
			sendpMessage(target.getPlayer(), plugin.getMessage("divorce_defendant"));

		plugin.getServer().broadcastMessage(FormatUtil.format(plugin.getMessage("divorce_broadcast"), player.getName(), spouse));
	}
}
