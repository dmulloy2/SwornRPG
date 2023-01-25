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
import net.dmulloy2.swornapi.types.Reloadable;

/**
 * @author dmulloy2
 */

public class CmdReload extends SwornRPGCommand implements Reloadable
{
	public CmdReload(SwornRPG plugin)
	{
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.description = "Reload SwornRPG";
		this.permission = Permission.RELOAD;

		this.usesPrefix = true;
	}
	
	@Override
	public void perform()
	{
		reload();
	}

	@Override
	public void reload()
	{
		long start = System.currentTimeMillis();

		sendpMessage("&aReloading Configuration...");
		
		plugin.reload();
		
		sendpMessage("&aReload Complete! (Took {0} ms)", System.currentTimeMillis() - start);
	}
}
