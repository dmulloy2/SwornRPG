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
package net.dmulloy2.swornrpg.io;

import java.io.File;

import org.bukkit.OfflinePlayer;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.io.NitriteDataCache;
import net.dmulloy2.swornrpg.types.PlayerData;

/**
 * @author dmulloy2
 */

public class PlayerDataCache extends NitriteDataCache<PlayerData>
{
	public PlayerDataCache(SwornPlugin plugin)
	{
		super(
			plugin,
			new File(plugin.getDataFolder(), "players.db"),
			PlayerData.class
		);
	}

	@Override
	public PlayerData newData(OfflinePlayer player)
	{
		PlayerData data = new PlayerData();

		// Defaults
		data.setDeathCoordsEnabled(true);
		data.setXpneeded(100);

		return data;
	}

	@Override
	protected void onDataLoad(PlayerData data, OfflinePlayer player)
	{
		data.setLastKnownBy(player.getName());
	}
}
