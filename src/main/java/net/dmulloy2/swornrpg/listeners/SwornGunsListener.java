package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornguns.events.SwornGunsFireEvent;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SwornGunsListener implements Listener
{
	private final SwornRPG plugin;
	public SwornGunsListener(SwornRPG plugin)
	{
		this.plugin = plugin;
	}
	
	/**Unlimited Ammo!**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerShoot(SwornGunsFireEvent event)
	{
		Player player = event.getShooterAsPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		if (data.isUnlimitedAmmoEnabled())
		{
			event.setAmountAmmoNeeded(0);
		}
	}
}