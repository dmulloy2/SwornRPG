package net.dmulloy2.swornrpg.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;

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

		this.cache = new ConcurrentHashMap<String, PlayerData>(64, 0.75F, 64);
		this.plugin = plugin;
	}

	// ---- Data Getters

	/**
	 * @deprecated Should only be used for internal purposes
	 */
	public final PlayerData getData(String key)
	{
		// Check cache first
		PlayerData data = cache.get(key);
		if (data == null)
		{
			// Attempt to load it
			File file = new File(folder, getFileName(key));
			if (file.exists())
			{
				data = loadData(key);
				if (data == null)
				{
					// Corrupt data :(
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

		// Update variables
		data.setLastKnownBy(player.getName());
		data.setUniqueId(player.getUniqueId().toString());

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

	public final PlayerData newData(String key)
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

	public final PlayerData newData(Player player)
	{
		return newData(getKey(player));
	}

	private final PlayerData loadData(String key)
	{
		File file = new File(folder, getFileName(key));

		try
		{
			PlayerData data = FileSerialization.load(file, PlayerData.class);
			data.setLastKnownBy(key);
			return data;
		}
		catch (Throwable ex)
		{
			plugin.outConsole(Level.WARNING, "Failed to load player data for {0}!", key);
			return null;
		}
	}

	public final void save()
	{
		long start = System.currentTimeMillis();
		plugin.outConsole("Saving {0} to disk...", folderName);

		for (Entry<String, PlayerData> entry : getAllLoadedPlayerData().entrySet())
		{
			File file = new File(folder, getFileName(entry.getKey()));
			FileSerialization.save(entry.getValue(), file);
		}

		plugin.outConsole("Players saved! [{0} ms]", System.currentTimeMillis() - start);
	}

	public final void cleanupData()
	{
		// Get all online players into an array list
		List<String> online = new ArrayList<String>();
		for (Player player : plugin.getServer().getOnlinePlayers())
			online.add(player.getName());

		// Actually cleanup the data
		for (String key : getAllLoadedPlayerData().keySet())
			if (! online.contains(key))
				cache.remove(key);

		// Clear references
		online.clear();
		online = null;
	}

	// ---- Mass Getters

	public final Map<String, PlayerData> getAllLoadedPlayerData()
	{
		return Collections.unmodifiableMap(cache);
	}

	public final Map<String, PlayerData> getAllPlayerData()
	{
		Map<String, PlayerData> data = new HashMap<String, PlayerData>();
		data.putAll(cache);

		File[] files = folder.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				return file.getName().contains(extension);
			}
		});

		for (File file : files)
		{
			String fileName = FormatUtil.trimFileExtension(file, extension);
			if (! isFileLoaded(fileName))
				data.put(fileName, loadData(fileName));
		}

		return Collections.unmodifiableMap(data);
	}

	// ---- Util

	private final String getKey(OfflinePlayer player)
	{
		return player.getName();
	}

	private final String getFileName(String key)
	{
		return key + extension;
	}

	private final boolean isFileLoaded(String fileName)
	{
		return cache.keySet().contains(fileName);
	}
}