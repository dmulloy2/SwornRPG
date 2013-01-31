package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.main;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

/**
 * @author t7seven7t
 * @editor dmulloy2
 */

public class TagListener implements Listener {
	
	public TagListener(main plugin) {
	}

	@EventHandler
	public void onNameTag(PlayerReceiveNameTagEvent event) {
		if (event.getNamedPlayer().getName().equals("dmulloy2")) {
			event.setTag(ChatColor.AQUA + "dmulloy2");
		}
	}
	
}
