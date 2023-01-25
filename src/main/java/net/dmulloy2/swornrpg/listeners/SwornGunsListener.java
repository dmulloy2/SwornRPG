package net.dmulloy2.swornrpg.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.dmulloy2.swornguns.events.SwornGunFireEvent;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;

public class SwornGunsListener implements Listener
{
	private final SwornRPG plugin;

	public SwornGunsListener(SwornRPG plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onSwornGunFire(SwornGunFireEvent event)
	{
		PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
		if (data.isUnlimitedAmmoEnabled())
		{
			event.setAmmoNeeded(0);
		}
	}
}
