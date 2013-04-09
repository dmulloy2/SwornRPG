package net.dmulloy2.swornrpg.data;

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
			System.err.println("Exception ocurred while attempting to save file: " + file.getName());
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
			System.err.println("Exception ocurred while attempting to load file: " + file.getName());
			if (file.renameTo(new File(file.getParent(), file.getName() + "_bad"))) 
			{
				System.out.println("Renamed bad file: " + file.getName() + " to: " + file.getName() + "_bad");
			}
			
			return null;
		}
	}
}