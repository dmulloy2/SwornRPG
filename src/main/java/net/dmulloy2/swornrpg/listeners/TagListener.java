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
    public void onPlayerReceiveNametag(PlayerReceiveNameTagEvent event) 
    {
        String name = event.getNamedPlayer().getName();
        if (plugin.getTagManager().hasChanged(name)) 
        {
        	String tag = plugin.getTagManager().getName(name);
        	tag = ChatColor.translateAlternateColorCodes('&', tag);
            event.setTag(tag);
        }
    }
}