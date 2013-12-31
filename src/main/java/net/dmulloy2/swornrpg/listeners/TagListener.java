package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

/**
 * @author dmulloy2
 */

public class TagListener implements Listener 
{
   	private final SwornRPG plugin;
    public TagListener(SwornRPG plugin) 
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerReceiveNametag(PlayerReceiveNameTagEvent event) 
    {
    	Player player = event.getNamedPlayer();
    	if (plugin.getTagHandler().hasChangedTag(player))
    	{
    		PlayerData data = plugin.getPlayerDataCache().getData(player);
    		if (data.getTag() != null)
    		{
    			event.setTag(FormatUtil.format(data.getTag()));
    		}
    	}
    }
}