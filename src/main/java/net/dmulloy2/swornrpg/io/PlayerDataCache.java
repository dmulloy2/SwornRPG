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
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import net.dmulloy2.io.FileSerialization;
import net.dmulloy2.io.IOUtil;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class PlayerDataCache
{
	private final File folder;
	private final String extension = ".dat";
	private final String folderName = "players";

	private final ConcurrentMap<String, PlayerData> cache;

	private final SwornRPG plugin;
	public PlayerDataCache(SwornRPG plugin)
	{
		this.folder = new File(plugin.getDataFolder(), folderName);
		if (! folder.exists())
			folder.mkdirs();

		this.cache = new ConcurrentHashMap<>(64, 0.75F, 64);
		this.plugin = plugin;
	}

	// ---- Data Getters

	private PlayerData getData(String key)
	{
		// Check cache first
		PlayerData data = cache.get(key);
		if (data == null)
		{
			// Attempt to load it
			File file = new File(folder, getFileName(key));
			if (file.exists())
			{
				data = loadData(file);
				if (data == null)
				{
					// Corrupt data :(
					if (! file.renameTo(new File(folder, file.getName() + "_bad")))
						file.delete();
					return null;
				}

				// Cache it
				cache.put(key, data);
			}
		}

		return data;
	}

	public final PlayerData getData(Player player)
	{
		PlayerData data = getData(getKey(player));

		// Online players always have data
		if (data == null)
			data = newData(player);

		// Update last known by
		data.setLastKnownBy(player.getName());

		// Return
		return data;
	}

	public final PlayerData getData(OfflinePlayer player)
	{
		// Slightly different handling for Players
		if (player.isOnline())
			return getData(player.getPlayer());

		// Attempt to get by name
		return getData(getKey(player));
	}

	// ---- Data Management

	private PlayerData newData(String key)
	{
		// Construct
		PlayerData data = new PlayerData();

		// Default values
		data.setDeathCoordsEnabled(true);
		data.setXpneeded(100);

		// Cache and return
		cache.put(key, data);
		return data;
	}

	private PlayerData newData(Player player)
	{
		return newData(getKey(player));
	}

	private PlayerData loadData(File file)
	{
		try
		{
			return FileSerialization.load(file, PlayerData.class, true);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading data from {0}", file.getName()));
			return null;
		}
	}

	public final void save()
	{
		long start = System.currentTimeMillis();
		plugin.log("Saving players to disk...");

		for (Entry<String, PlayerData> entry : getAllLoadedPlayerData().entrySet())
		{
			try
			{
				File file = new File(folder, getFileName(entry.getKey()));
				FileSerialization.save(entry.getValue(), file);
			}
			catch (Throwable ex)
			{
				plugin.log(Level.WARNING, Util.getUsefulStack(ex, "saving data for {0}", entry.getKey()));
			}
		}

		plugin.log("Players saved. Took {0} ms.", System.currentTimeMillis() - start);
	}

	public final void cleanupData()
	{
		// Get all online players into a  list
		List<String> online = new ArrayList<>();
		for (Player player : Util.getOnlinePlayers())
			online.add(getKey(player));

		// Actually cleanup the data
		for (String key : getAllLoadedPlayerData().keySet())
			if (! online.contains(key))
				cache.remove(key);

		online.clear();
	}

	// ---- Mass Getters

	private Map<String, PlayerData> getAllLoadedPlayerData()
	{
		return Collections.unmodifiableMap(cache);
	}

	public final Map<String, PlayerData> getAllPlayerData()
	{
		Map<String, PlayerData> data = new HashMap<String, PlayerData>();
		data.putAll(cache);

		File[] files = folder.listFiles(file -> file.getName().endsWith(extension));
		for (File file : files)
		{
			String fileName = IOUtil.trimFileExtension(file, extension);
			if (isFileLoaded(fileName))
				continue;

			PlayerData loaded = loadData(file);
			if (loaded != null)
				data.put(fileName, loaded);
		}

		return Collections.unmodifiableMap(data);
	}

	// ---- Util

	private String getKey(OfflinePlayer player)
	{
		return player.getUniqueId().toString();
	}

	private String getFileName(String key)
	{
		return key + extension;
	}

	private boolean isFileLoaded(String fileName)
	{
		return cache.containsKey(fileName);
	}
}
