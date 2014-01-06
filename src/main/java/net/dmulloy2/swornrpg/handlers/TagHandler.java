package net.dmulloy2.swornrpg.handlers;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;
import org.kitteh.tag.TagAPI;

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
		this.convert();

		if (plugin.getPluginManager().isPluginEnabled("TagAPI"))
		{
			plugin.outConsole(plugin.getMessage("log_tag_found"));
			registerEvents();
		}
		else
		{
			plugin.outConsole(plugin.getMessage("log_tag_notfound"));
		}
	}

	/**
	 * Sets a given player's tag
	 * 
	 * @param player
	 *        - {@link Player} to set tag for
	 * @param tag
	 *        - New tag
	 */
	public final void setTag(Player player, String tag)
	{
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		data.setTag(tag);

		TagAPI.refreshPlayer(player);
	}

	/**
	 * Removes a player's custom tag
	 * 
	 * @param player
	 *        - {@link Player} to remove tag from
	 */
	public final void removeTag(Player player)
	{
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		data.setTag(null);

		TagAPI.refreshPlayer(player);
	}

	/**
	 * Returns whether or not a player has a custom tag
	 * 
	 * @param player - {@link Player} to check
	 */
	public final boolean hasTag(Player player)
	{
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		return data.getTag() != null;
	}

	/**
	 * Registers the tag events
	 */
	private final void registerEvents()
	{
		try
		{
			Class.forName("org.kitteh.tag.AsyncPlayerReceiveNameTagEvent");
			plugin.getPluginManager().registerEvents(new TagListener(plugin), plugin);
		}
		catch (ClassNotFoundException e)
		{
			plugin.getLogHandler().log(Level.WARNING, "Detected an outdated TagAPI version, defaulting to deprecated event.");
			plugin.getServer().getPluginManager().registerEvents(new DeprecatedTagListener(plugin), plugin);
		}
	}

	/**
	 * Converts tags to the new format
	 */
	private final void convert()
	{
		final File tagsFile = new File(plugin.getDataFolder(), "tags.yml");
		if (tagsFile.exists())
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					long start = System.currentTimeMillis();

					plugin.outConsole("Running Tag conversion task...");

					YamlConfiguration fc = YamlConfiguration.loadConfiguration(tagsFile);

					Map<String, Object> tags = fc.getConfigurationSection("tags").getValues(true);

					for (Entry<String, Object> entry : tags.entrySet())
					{
						PlayerData data = plugin.getPlayerDataCache().getData(entry.getKey());
						if (data != null)
						{
							data.setTag((String) entry.getValue());
						}
					}

					tagsFile.delete();

					plugin.outConsole("Tag conversion task complete! [{0}ms]", System.currentTimeMillis() - start);
				}
			}.runTaskLater(plugin, 20L);
		}
	}

	@SuppressWarnings("deprecation")
	public class DeprecatedTagListener implements Listener 
	{
	   	private final SwornRPG plugin;
	    public DeprecatedTagListener(SwornRPG plugin) 
	    {
	        this.plugin = plugin;
	    }

		@EventHandler
	    public void onPlayerReceiveNametag(org.kitteh.tag.PlayerReceiveNameTagEvent event) 
	    {
	    	Player player = event.getNamedPlayer();
	    	if (plugin.getTagHandler().hasTag(player))
	    	{
	    		PlayerData data = plugin.getPlayerDataCache().getData(player);
	    		if (data.getTag() != null)
	    		{
	    			event.setTag(FormatUtil.format(data.getTag()));
	    		}
	    	}
	    }
	}

	public class TagListener implements Listener 
	{
	   	private final SwornRPG plugin;
	    public TagListener(SwornRPG plugin) 
	    {
	        this.plugin = plugin;
	    }

		@EventHandler
	    public void onPlayerReceiveNametag(AsyncPlayerReceiveNameTagEvent event) 
	    {
	    	Player player = event.getNamedPlayer();
	    	if (plugin.getTagHandler().hasTag(player))
	    	{
	    		PlayerData data = plugin.getPlayerDataCache().getData(player);
	    		if (data.getTag() != null)
	    		{
	    			event.setTag(FormatUtil.format(data.getTag()));
	    		}
	    	}
	    }
	}
}