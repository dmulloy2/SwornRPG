package net.dmulloy2.swornrpg.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.orange451.pvpgunplus.events.PVPGunPlusFireGunEvent;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

public class PVPGunPlusListener implements Listener
{
	private SwornRPG plugin;
	public PVPGunPlusListener(SwornRPG plugin)
	{
		this.plugin = plugin;
	}
	
	/**Unlimited Ammo!**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerShoot(PVPGunPlusFireGunEvent event)
	{
		if (plugin.getServer().getPluginManager().isPluginEnabled("PVPGunPlus"))
		{
			Player player = event.getShooterAsPlayer();
			PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
			if (data.isUnlimtdammo())
			{
				event.setAmountAmmoNeeded(0);
			}
		}
	}
}
