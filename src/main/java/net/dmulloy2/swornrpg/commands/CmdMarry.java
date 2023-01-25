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

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdMarry extends SwornRPGCommand
{
	public CmdMarry(SwornRPG plugin)
	{
		super(plugin);
		this.name = "marry";
		this.addRequiredArg("player");
		this.description = "Marry another player";
		this.permission = Permission.MARRY;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		if (! plugin.getConfig().getBoolean("marriage"))
		{
			err(plugin.getMessage("command_disabled"));
			return;
		}

		PlayerData data = getPlayerData(player);
		if (! data.getProposals().contains(args[0]))
		{
			err("&c{0} &4hasn''t proposed!", args[0]);
			return;
		}

		Player target = Util.matchPlayer(args[0]);
		if (target == null)
		{
			err(getMessage("player_not_found"), args[0]);
			return;
		}

		data.setSpouse(target.getName());

		PlayerData data1 = getPlayerData(target);
		data1.setSpouse(player.getName());

		plugin.getServer().broadcastMessage(FormatUtil.format(getMessage("marry"), player.getName(), target.getName()));
		data.getProposals().remove(target.getName());
	}
}
