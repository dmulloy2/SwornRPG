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

public class CmdLevel extends SwornRPGCommand
{
	public CmdLevel (SwornRPG plugin)
	{
		super(plugin);
		this.name = "level";
		this.description = "Check a player's level";
		this.addOptionalArg("player");
		this.permission = Permission.LEVEL;
	}
	
	@Override
	public void perform()
	{
		OfflinePlayer target = getTarget(0, hasPermission(Permission.LEVEL_OTHERS));
		if (target == null)
			return;
		
		PlayerData data = getPlayerData(target);
		if (data == null)
		{
			err(plugin.getMessage("player_not_found"), args[0]);
			return;
		}

		int level = data.getLevel();
		int nextlevel = level+1;
		int totalxp = data.getTotalxp();
		int xptonext = data.getXpNeeded() - data.getPlayerxp();

		String name, title;
		String senderp = sender.getName();
		String targetp = target.getName();

		if (targetp.equals(senderp))
		{
			name = "You are";
			title = senderp;
		}
		else
		{
			name = targetp + " is";
			title = targetp;
		}

		sendMessage(plugin.getMessage("level_header"), title);
		sendMessage(plugin.getMessage("level_info"), name, level, totalxp);
		sendMessage(plugin.getMessage("level_xptonext"), name, xptonext, nextlevel);

		StringBuilder bar = new StringBuilder();
		bar.append("&3[");

		int scale = 20;
		int bars = Math.round(scale - ((xptonext * scale) / data.getXpNeeded()));
		for (int i = 0; i < bars; i++)
		{
			bar.append("&b=");
		}
		
		int left = scale - bars;
		for (int ii = 0; ii < left; ii++)
		{
			bar.append("&e=");
		}

		bar.append("&3]");

		sendMessage(bar.toString());
	}
}
