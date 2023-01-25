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
package net.dmulloy2.swornrpg.integration;

import java.util.logging.Level;

import net.dmulloy2.swornapi.integration.DependencyProvider;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornapi.util.Util;

import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

/**
 * @author dmulloy2
 */

public class EssentialsHandler extends DependencyProvider<Essentials>
{
	public EssentialsHandler(SwornRPG plugin)
	{
		super(plugin, "Essentials");
	}

	public final boolean sendMail(Player player, String mail)
	{
		if (! isEnabled())
			return false;

		try
		{
			User user = getDependency().getUser(player);
			user.addMail(mail);
			return true;
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING,
					Util.getUsefulStack(ex, String.format("sendMail(%s, %s)", player.getName(), mail)));
		}

		return false;
	}
}
