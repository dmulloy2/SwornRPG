package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

/**
 * @author dmulloy2
 */

public class TagListener implements Listener 
{
   	private final SwornRPG plugin;
    public TagListener(final SwornRPG plugin) 
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerReceiveNametag(PlayerReceiveNameTagEvent event) 
    {
        String name = event.getNamedPlayer().getName();
        if (plugin.getTagManager().hasChanged(name)) 
        {
        	String tag = FormatUtil.format(plugin.getTagManager().getName(name));
            event.setTag(tag);
        }
    }
}