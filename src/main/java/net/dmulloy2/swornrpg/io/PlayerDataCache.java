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
	private final SwornRPG plugin;

	private ConcurrentMap<String, PlayerData> data;

	public PlayerDataCache(SwornRPG plugin)
	{
		this.folder = new File(plugin.getDataFolder(), folderName);

		if (! folder.exists())
			folder.mkdir();

		this.data = new ConcurrentHashMap<String, PlayerData>(64, 0.75f, 64);
		this.plugin = plugin;
	}

	public PlayerData getData(final String key)
	{
		PlayerData value = data.get(key);
		if (value == null)
		{
			File file = new File(folder, getFileName(key));
			if (file.exists())
			{
				value = loadData(key);
				if (value == null)
				{
					file.delete();
					return null;
				}

				addData(key, value);
			}
		}

		return value;
	}

	public PlayerData getData(Player player)
	{
		PlayerData data = getData(player.getName());
		if (data == null)
			data = newData(player);

		return data;
	}

	public Map<String, PlayerData> getAllLoadedPlayerData()
	{
		return Collections.unmodifiableMap(data);
	}

	public Map<String, PlayerData> getAllPlayerData()
	{
		Map<String, PlayerData> data = new HashMap<String, PlayerData>();
		data.putAll(this.data);

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
			String fileName = trimFileExtension(file);
			if (! isFileAlreadyLoaded(fileName, data))
				data.put(fileName, loadData(fileName));
		}

		return Collections.unmodifiableMap(data);
	}

	private void removeData(final String key)
	{
		data.remove(key);
	}

	private void addData(final String key, final PlayerData value)
	{
		data.put(key, value);
	}

	public PlayerData newData(final String key)
	{
		// Construct
		PlayerData value = new PlayerData();

		// Default values
		value.setDeathCoordsEnabled(true);
		value.setXpneeded(100);

		// Add and return
		addData(key, value);
		return value;
	}

	public PlayerData newData(final OfflinePlayer player)
	{
		return newData(player.getName());
	}

	public void cleanupData()
	{
		// Get all online players into an array list
		List<String> online = new ArrayList<String>();
		for (Player player : plugin.getServer().getOnlinePlayers())
			online.add(player.getName());

		// Actually cleanup the data
		for (String key : getAllLoadedPlayerData().keySet())
			if (! online.contains(key))
				removeData(key);

		// Clear references
		online.clear();
		online = null;
	}

	private PlayerData loadData(final String key)
	{
		File file = new File(folder, getFileName(key));

		try
		{
			return FileSerialization.load(file, PlayerData.class);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.WARNING, "Could not load PlayerData for \"{0}\": {1}", key, e.getMessage());
			return null;
		}
	}

	public void save()
	{
		plugin.outConsole("Saving {0} to disk...", folderName);
		long start = System.currentTimeMillis();
		for (Entry<String, PlayerData> entry : getAllLoadedPlayerData().entrySet())
		{
			File file = new File(folder, getFileName(entry.getKey()));
			FileSerialization.save(entry.getValue(), file);
		}

		plugin.outConsole("Players saved! [{0} ms]", System.currentTimeMillis() - start);
	}

	public void save(boolean cleanup)
	{
		save();
		if (cleanup)
			cleanupData();
	}

	private boolean isFileAlreadyLoaded(final String fileName, final Map<String, PlayerData> map)
	{
		for (String key : map.keySet())
		{
			if (key.equals(fileName))
				return true;
		}

		return false;
	}

	private String trimFileExtension(final File file)
	{
		int index = file.getName().lastIndexOf(extension);
		return index > 0 ? file.getName().substring(0, index) : file.getName();
	}

	private String getFileName(final String key)
	{
		return key + extension;
	}

	public int getFileListSize()
	{
		return folder.listFiles().length;
	}

	public int getCacheSize()
	{
		return data.size();
	}
}