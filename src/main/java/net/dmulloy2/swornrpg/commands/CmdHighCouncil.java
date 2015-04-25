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

/**
 * @author dmulloy2
 */

public class CmdHighCouncil extends SwornRPGCommand
{
	public CmdHighCouncil(SwornRPG plugin)
	{
		super(plugin);
		this.name = "hc";
		this.aliases.add("highcouncil");
		this.addRequiredArg("message");
		this.description = "Talk in council chat";
		this.permission = Permission.HIGHCOUNCIL;
	}

	@Override
	public void perform()
	{
		String name = getName(sender);
		String message = FormatUtil.join(" ", args);
		String node = plugin.getPermissionHandler().getPermissionString(permission);
		plugin.getServer().broadcast(FormatUtil.format(plugin.getMessage("council_chat"), name, message), node);
	}
}
