package net.dmulloy2.swornrpg.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import net.dmulloy2.io.FileSerialization;
import net.dmulloy2.io.IOUtil;
import net.dmulloy2.io.UUIDFetcher;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.types.Versioning;
import net.dmulloy2.types.Versioning.Version;
import net.dmulloy2.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

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
		this.convertToUUID();
	}

	// ---- Data Getters

	private final PlayerData getData(String key)
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
			return data;
		}
		catch (Throwable ex)
		{
			plugin.log(Level.WARNING, "Failed to load player data for {0}!", key);
			return null;
		}
	}

	public final void save()
	{
		long start = System.currentTimeMillis();
		plugin.log("Saving {0} to disk...", folderName);

		for (Entry<String, PlayerData> entry : getAllLoadedPlayerData().entrySet())
		{
			File file = new File(folder, getFileName(entry.getKey()));
			FileSerialization.save(entry.getValue(), file);
		}

		plugin.log("Players saved! [{0} ms]", System.currentTimeMillis() - start);
	}

	public final void cleanupData()
	{
		// Get all online players into an array list
		List<String> online = new ArrayList<>();
		for (Player player : Util.getOnlinePlayers())
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
			String fileName = IOUtil.trimFileExtension(file, extension);
			if (! isFileLoaded(fileName))
				data.put(fileName, loadData(fileName));
		}

		return Collections.unmodifiableMap(data);
	}

	// ---- UUID Conversion

	private final void convertToUUID()
	{
		File updated = new File(folder, ".updated");
		if (updated.exists())
			return;

		if (Versioning.getVersion() == Version.MC_16)
			return;

		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Checking for unconverted files");

		Map<String, PlayerData> data = getUnconvertedData();
		if (data.isEmpty())
		{
			try
			{
				updated.createNewFile();
			} catch (Throwable ex) { }
			return;
		}

		plugin.getLogHandler().log("Converting {0} files!", data.size());

		try
		{
			List<String> names = new ArrayList<String>(data.keySet());
			ImmutableList.Builder<List<String>> builder = ImmutableList.builder();
			int namesCopied = 0;
			while (namesCopied < names.size())
			{
				builder.add(ImmutableList.copyOf(names.subList(namesCopied, Math.min(namesCopied + 100, names.size()))));
				namesCopied += 100;
			}

			List<UUIDFetcher> fetchers = new ArrayList<UUIDFetcher>();
			for (List<String> namesList : builder.build())
			{
				fetchers.add(new UUIDFetcher(namesList));
			}

			ExecutorService e = Executors.newFixedThreadPool(3);
			List<Future<Map<String, UUID>>> results = e.invokeAll(fetchers);

			File archive = new File(folder.getParentFile(), "archive");
			if (! archive.exists())
				archive.mkdir();

			for (Future<Map<String, UUID>> result : results)
			{
				Map<String, UUID> uuids = result.get();
				for (Entry<String, UUID> entry : uuids.entrySet())
				{
					try
					{
						// Get and update
						String name = entry.getKey();
						String uniqueId = entry.getValue().toString();
						PlayerData dat = data.get(name);
						dat.setLastKnownBy(name);

						// Archive the old file
						File file = new File(folder, getFileName(name));
						Files.move(file, new File(archive, file.getName()));

						// Create and save new file
						File newFile = new File(folder, getFileName(uniqueId));
						FileSerialization.save(dat, newFile);
					}
					catch (Throwable ex)
					{
						plugin.getLogHandler().log(Level.WARNING, "Failed to convert " + entry.getKey());
					}
				}
			}
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "converting to UUID-based lookups!"));
			return;
		}

		plugin.getLogHandler().log("Successfully converted to UUID-based lookups! Took {0} ms!", System.currentTimeMillis() - start);
	}

	private final Map<String, PlayerData> getUnconvertedData()
	{
		Map<String, PlayerData> data = new HashMap<String, PlayerData>();

		File[] files = folder.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				String name = file.getName();
				return name.contains(extension) && name.length() != 40;
			}
		});

		for (File file : files)
		{
			String fileName = IOUtil.trimFileExtension(file, extension);
			PlayerData loaded = loadData(fileName);
			loaded.setLastKnownBy(fileName);
			data.put(fileName, loaded);
		}

		return Collections.unmodifiableMap(data);
	}

	// ---- Util

	private final String getKey(OfflinePlayer player)
	{
		if (Versioning.getVersion() == Version.MC_16)
			return player.getName();

		return player.getUniqueId().toString();
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