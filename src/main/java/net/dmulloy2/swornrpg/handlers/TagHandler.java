package net.dmulloy2.swornrpg.handlers;

import java.util.logging.Level;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

		if (plugin.getPluginManager().isPluginEnabled("TagAPI"))
		{
			plugin.outConsole(plugin.getMessage("log_integration_tagapi"));
			registerEvents();
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
	 * @param player
	 *        - {@link Player} to check
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
			plugin.getPluginManager().registerEvents(new TagListener(), plugin);
		}
		catch (ClassNotFoundException e)
		{
			plugin.getLogHandler().log(Level.WARNING, "Detected an outdated TagAPI version, defaulting to deprecated event.");
			plugin.getServer().getPluginManager().registerEvents(new DeprecatedTagListener(), plugin);
		}
	}

	@SuppressWarnings("deprecation")
	public class DeprecatedTagListener implements Listener
	{
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