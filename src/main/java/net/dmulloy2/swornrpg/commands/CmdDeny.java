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

public class CmdDeny extends SwornRPGCommand
{
	public CmdDeny(SwornRPG plugin)
	{
		super(plugin);
		this.name = "deny";
		this.addOptionalArg("player");
		this.description = "Deny a player's proposal";
		this.permission = Permission.DENY;
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
		if (data.getProposals().isEmpty())
		{
			err(getMessage("no_proposal"));
			return;
		}

		if (args.length == 0)
		{
			for (String reject : data.getProposals())
			{
				data.getProposals().remove(reject);

				Player target = Util.matchPlayer(reject);
				if (target != null)
				{
					sendpMessage(target, getMessage("deny_rejcted"), player.getName());
				}
			}

			sendpMessage(getMessage("deny_sender"), "all");
		}
		else
		{
			String reject = args[0];
			if (! data.getProposals().contains(reject))
			{
				err(getMessage("no_proposal"));
				return;
			}

			data.getProposals().remove(reject);

			Player target = Util.matchPlayer(reject);
			if (target != null)
			{
				sendpMessage(target, getMessage("deny_rejcted"), player.getName());
				sendpMessage(getMessage("deny_sender"), target.getName() + "''s proposal");
			}
		}
	}
}
