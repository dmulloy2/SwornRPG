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

import net.dmulloy2.commands.Command;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public abstract class SwornRPGCommand extends Command
{
	protected final SwornRPG plugin;
	public SwornRPGCommand(SwornRPG plugin)
	{
		super(plugin);
		this.plugin = plugin;
	}

	protected final String getMessage(String msg)
	{
		return plugin.getMessage(msg);
	}

	// ---- Player Management

	protected PlayerData getPlayerData(OfflinePlayer target)
	{
		return plugin.getPlayerDataCache().getData(target);
	}

	protected final OfflinePlayer getTarget(int arg, boolean others)
	{
		OfflinePlayer target = null;
		if (args.length > arg && others)
		{
			target = Util.matchPlayer(args[arg]);
			if (target == null)
			{
				target = Util.matchOfflinePlayer(args[arg]);
				if (target == null || ! target.hasPlayedBefore())
				{
					err(getMessage("player_not_found"), args[arg]);
					return null;
				}
			}
		}
		else
		{
			if (sender instanceof Player)
			{
				target = player;
			}
			else
			{
				err(getMessage("console_level"));
				return null;
			}
		}

		return target;
	}
}
