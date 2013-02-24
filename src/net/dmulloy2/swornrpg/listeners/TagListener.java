package net.dmulloy2.swornrpg.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;
import net.dmulloy2.swornrpg.SwornRPG;

/**
 * @author dmulloy2
 */

public class TagListener implements Listener 
{
	
    public SwornRPG plugin;

    public TagListener(SwornRPG plugin) 
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerReceiveNametag(final PlayerReceiveNameTagEvent event) 
    {
        final String name = event.getNamedPlayer().getName();
        if (this.plugin.hasChanged(name)) 
        {
            event.setTag(ChatColor.translateAlternateColorCodes('&', this.plugin.getName(name)));
        }
    }
}