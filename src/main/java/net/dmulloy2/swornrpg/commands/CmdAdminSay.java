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
import net.dmulloy2.swornapi.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdAdminSay extends SwornRPGCommand
{
	public CmdAdminSay(SwornRPG plugin)
	{
		super(plugin);
		this.name = "adminsay";
		this.aliases.add("asay");
		this.addRequiredArg("message");
		this.description = "Alternate admin say command";
		this.permission = Permission.ADMINSAY;
	}

	@Override
	public void perform()
	{
		String message = FormatUtil.join(" ", args);
		plugin.getServer().broadcastMessage(FormatUtil.format(plugin.getMessage("admin_say"), message));
	}
}
