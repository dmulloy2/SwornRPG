package net.dmulloy2.swornrpg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;

import lombok.Getter;

import net.dmulloy2.swornrpg.listeners.TagListener;
import net.dmulloy2.swornrpg.util.TooBigException;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

public class TagManager
{
	public SwornRPG plugin;
	public TagManager(SwornRPG plugin)
	{
		this.plugin = plugin;
	}
	
	private FileConfiguration tagsConfig = null;
    private File tagsConfigFile = null;
	
	private @Getter HashMap<String, String> tagChanges = new HashMap<String, String>();
	
	public void load()
	{
		/**Check for TagAPI**/
		if (plugin.getPluginManager().isPluginEnabled("TagAPI"))
		{
			/**If found, enable Tags**/
			plugin.getPluginManager().registerEvents(new TagListener(plugin), plugin);
			plugin.outConsole(plugin.getMessage("log_tag_found"));
			for (Player player : plugin.getServer().getOnlinePlayers()) 
			{
				String oldName = player.getName();
				String newName = getDefinedName(oldName);
				if (!newName.equals(oldName)) 
				{
					try 
					{
						addTagChange(oldName, newName);
					} 
					catch (TooBigException e) 
					{
						plugin.outConsole(Level.SEVERE, plugin.getMessage("log_tag_error"), e.getMessage());
					}
					TagAPI.refreshPlayer(player);
				}
			}
		}
		else
		{
			plugin.outConsole(plugin.getMessage("log_tag_notfound"));
		}
	}
	
	/**Tags Stuff**/
    public void addTagChange(String oldName, String newName)
    {
        tagChanges.put(oldName, newName);
        gettagsConfig().set("tags." + oldName, newName);
        savetagsConfig();
        Player player = Util.matchPlayer(oldName);
        if (player != null) 
        {
            TagAPI.refreshPlayer(player);
        }
    }
    
    public void removeTagChange(String oldName) 
    {
        tagChanges.remove(oldName);
        gettagsConfig().set("tags." + oldName, null);
        savetagsConfig();
        Player player = Util.matchPlayer(oldName);
        if (player != null) 
        {
            TagAPI.refreshPlayer(player);
        }
    }
    
    public boolean hasChanged(String name) 
    {
        return tagChanges.containsKey(name);
    }
    
    public String getName(String name) 
    {
        return tagChanges.get(name);
    }
    
    public String getDefinedName(String oldName)
    {
        String newName = gettagsConfig().getString("tags." + oldName);
        return newName == null ? oldName : newName;
    }
    
    /**Tags Configuration**/
    public void reloadtagsConfig() 
    {
        if (tagsConfigFile == null) 
        {
        	tagsConfigFile = new File(plugin.getDataFolder(), "tags.yml");
        }
        tagsConfig = YamlConfiguration.loadConfiguration(tagsConfigFile);
     
        InputStream defConfigStream = plugin.getResource("tags.yml");
        if (defConfigStream != null) 
        {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            tagsConfig.setDefaults(defConfig);
        }
    }
    
    public FileConfiguration gettagsConfig() 
    {
        if (tagsConfig == null) 
        {
            reloadtagsConfig();
        }
        return tagsConfig;
    }
    
    public void savetagsConfig() 
    {
        if (tagsConfig == null || tagsConfigFile == null) 
        {
        	return;
        }
        try 
        {
        	gettagsConfig().save(tagsConfigFile);
        } 
        catch (IOException ex) 
        {
        	plugin.outConsole(Level.SEVERE, plugin.getMessage("log_tag_save"), tagsConfigFile);
        }
    }
}
