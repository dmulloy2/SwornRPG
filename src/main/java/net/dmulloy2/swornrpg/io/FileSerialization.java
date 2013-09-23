package net.dmulloy2.swornrpg.io;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * @author dmulloy2
 */

public class FileSerialization 
{
	public static <T extends ConfigurationSerializable> void save(T instance, File file) 
	{
		try 
		{
			if (file.exists())
				file.delete();
			
			file.createNewFile();
			
			FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
			for (Entry<String, Object> entry : instance.serialize().entrySet()) 
			{
				fc.set(entry.getKey(), entry.getValue());
			}
			
			fc.save(file);
		} 
		catch (Exception ex) 
		{
			System.err.println("[SwornRPG] Exception ocurred while attempting to save file: " + file.getName());
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends ConfigurationSerializable> T load(File file, Class<T> clazz) 
	{
		try
		{
			if (!file.exists())
				return null;
			
			FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
			Map<String, Object> map = fc.getValues(true);
			
			return (T) ConfigurationSerialization.deserializeObject(map, clazz);
		}
		catch (Exception ex) 
		{
			// The file is most likely corrupt
			System.err.println("[SwornRPG] Could not load file: " + file.getName());
			System.out.println("[SwornRPG] Attempting to rename file!");
			
			// Attempt to rename it to <filename>.dat_bad
			File newFile = new File(file.getParentFile(), file.getName() + "_bad");
			if (file.renameTo(newFile))
			{
				System.out.println("[SwornRPG] Renamed bad file to: " + newFile.getName());
			}
			else
			{
				System.err.println("[SwornRPG] Could not rename bad file!");
			}
			
			// Delete the file regardless of whether or not it could be renamed.
			// Every instance i have seen this, the file was just a bunch of null characters.
			file.delete();
			
			return null;
		}
	}
}