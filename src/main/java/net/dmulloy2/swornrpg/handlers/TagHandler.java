package net.dmulloy2.swornrpg.handlers;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.listeners.TagListener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles tags and related features
 * 
 * @author dmulloy2
 */

public class TagHandler
{
    private final SwornRPG plugin;
	public TagHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
	}

	public void load()
	{
		convert();
		
		if (plugin.getPluginManager().isPluginEnabled("TagAPI"))
		{
			plugin.outConsole(plugin.getMessage("log_tag_found"));
			
			plugin.getPluginManager().registerEvents(new TagListener(plugin), plugin);
		}
		else
		{
			plugin.outConsole(plugin.getMessage("log_tag_notfound"));
		}
	}
	
	public void addTagChange(Player player, String tag)
	{
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		
		data.setTag(tag);
	}
	
	public void removeTagChange(Player player)
	{
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		
		data.setTag(null);
	}
	
	public boolean hasChangedTag(Player player)
	{
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		
		return data.getTag() != null;
	}

    public void convert()
    {
    	File tagsFile = new File(plugin.getDataFolder(), "tags.yml");
    	if (tagsFile.exists())
    	{
    		new BukkitRunnable()
    		{
    			@Override
    			public void run()
    			{
    				long start = System.currentTimeMillis();
    			
    				plugin.outConsole("Running Tag conversion task...");

    				File tagsFile = new File(plugin.getDataFolder(), "tags.yml");
    				
    				YamlConfiguration fc = YamlConfiguration.loadConfiguration(tagsFile);
    				
    				Map<String, Object> tags = fc.getConfigurationSection("tags").getValues(true);

    				for (Entry<String, Object> entry : tags.entrySet())
    				{
    					PlayerData data = plugin.getPlayerDataCache().getData(entry.getKey());
    					if (data != null)
    					{
    						data.setTag(entry.getValue().toString());
    					}
    				}
    				
    				tagsFile.delete();
    				
    				plugin.outConsole("Tag conversion task complete! [{0}ms]", System.currentTimeMillis() - start);
    			}
    		}.runTaskLater(plugin, 20L);
    	}
    }
}