package net.dmulloy2.swornrpg.io;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.OfflinePlayer;

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
		
		if (!folder.exists())
			folder.mkdir();
		
		this.data = new ConcurrentHashMap<String, PlayerData>(64, 0.75f, 64);
		this.plugin = plugin;
	}

	public PlayerData getData(final String key) 
	{
		PlayerData value = this.data.get(key);
		if (value == null) 
		{
			File file = new File(folder, getFileName(key));
			if (file.exists())
			{
				value = loadData(key);
				addData(key, value);
			}
		}
		
		return value;
	}
	
	public PlayerData getData(final OfflinePlayer player)
	{
		return getData(player.getName());
	}
	
	public Map<String, PlayerData> getAllLoadedPlayerData()
	{
		return Collections.unmodifiableMap(data);
	}
	
	public Map<String, PlayerData> getAllPlayerData()
	{
		Map<String, PlayerData> data = new HashMap<String, PlayerData>();
		data.putAll(this.data);
		for (File file : folder.listFiles())
			if (file.getName().contains(extension))
			{
				String fileName = trimFileExtension(file);
				if (!isFileAlreadyLoaded(fileName, data))
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
		PlayerData value = new PlayerData();
		addData(key, value);
		return value;
	}
	
	public PlayerData newData(final OfflinePlayer player) 
	{
		return newData(player.getName());
	}
	
	private void cleanupData() 
	{
		for (String key : getAllLoadedPlayerData().keySet())
			if (!Util.matchOfflinePlayer(key).isOnline())
				removeData(key);
	}
	
	private PlayerData loadData(final String key) 
	{
		File file = new File(folder, getFileName(key));
		
		synchronized(file)
		{
			return FileSerialization.load(new File(folder, getFileName(key)), PlayerData.class);
		}
	}
	
	public void save() 
	{
		plugin.outConsole("Saving {0} to disk...", folderName);
		long start = System.currentTimeMillis();
		for (Entry<String, PlayerData> entry : getAllLoadedPlayerData().entrySet()) 
		{
			File file = new File(folder, getFileName(entry.getKey()));
			
			synchronized(file)
			{
				FileSerialization.save(entry.getValue(), new File(folder, getFileName(entry.getKey())));
			}
		}
		cleanupData();
		plugin.outConsole("Players saved! [{0} ms]", (System.currentTimeMillis() - start));
	}
	
	private boolean isFileAlreadyLoaded(final String fileName, final Map<String, PlayerData> map) 
	{
		for (String key : map.keySet())
			if (key.equals(fileName))
				return true;
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