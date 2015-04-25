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
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdPropose extends SwornRPGCommand
{
	public CmdPropose(SwornRPG plugin)
	{
		super(plugin);
		this.name = "propose";
		this.addRequiredArg("player");
		this.description = "Propose marriage to a player";
		this.permission = Permission.PROPOSE;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		if (! plugin.getConfig().getBoolean("marriage"))
		{
			err(getMessage("command_disabled"));
			return;
		}

		PlayerData data = getPlayerData(player);
		Player target = Util.matchPlayer(args[0]);
		if (target == null)
		{
			err(getMessage("player_not_found"), args[0]);
			return;
		}

		String targetp = target.getName();
		PlayerData data1 = getPlayerData(target);
		if (data.getSpouse() != null)
		{
			err(getMessage("polygamy"));
		}
		else if (targetp.equals(sender.getName()))
		{
			err(getMessage("cannot_marry_self"));
		}
		else if (data1.getSpouse() != null)
		{
			err(getMessage("target_is_married"), targetp);
		}
		else if (data1.getProposals().contains(player.getName()))
		{
			err(getMessage("already_proposed"), targetp);
		}
		else
		{
			data1.getProposals().add(player.getName());
			sendpMessage(getMessage("you_have_proposed"), targetp);
			sendpMessage(target, getMessage("send_marriage_request"), sender.getName());
		}
	}
}
