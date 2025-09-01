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
package net.dmulloy2.swornrpg.modules;

import lombok.Getter;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornapi.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

@Getter
public class OnlineTime extends TickableModule
{
	private int xpGain;
	private int interval;
	private boolean async;

	public OnlineTime(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("levelingMethods.onlineTime.enabled", false));
		this.xpGain = plugin.getConfig().getInt("levelingMethods.onlineTime.xpgain");
		this.interval = plugin.getConfig().getInt("levelingMethods.onlineTime.interval", 60) * 20;
		this.async = plugin.getConfig().getBoolean("levelingMethods.onlineTime.async", false);
	}


	@Override
	public void run()
	{
		for (Player player : plugin.getServer().getOnlinePlayers())
		{
			plugin.getExperienceHandler().handleXpGain(player, xpGain, "");
		}
	}
}
