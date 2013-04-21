package net.dmulloy2.swornrpg.commands;

import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

public class CmdUnlimitedAmmo extends SwornRPGCommand
{

	public CmdUnlimitedAmmo(SwornRPG plugin) 
	{
		super(plugin);
		this.name = "ammo";
		this.description = "Unlimited ammo for your gun!";
		this.aliases.add("unlimitedammo");
		this.mustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		PluginManager pm = plugin.getServer().getPluginManager();
		if (!pm.isPluginEnabled("PVPGunPlus"))
		{
			sendpMessage(plugin.getMessage("plugin_not_found"), "PVPGunPlus");
			return;
		}
		final PlayerData data = getPlayerData(player);
		if (data.isUnlimtdammo())
		{
			sendpMessage(plugin.getMessage("already_using_ammo"));
			return;
		}
		if (data.isAmmocooling())
		{
			sendpMessage(plugin.getMessage("ammo_cooling_header"));
			sendpMessage(plugin.getMessage("ammo_cooling_time"), data.getAmmocd());
			return;
		}
		int level = data.getLevel();
		int duration = (20*(plugin.ammobaseduration + (level*plugin.ammomultiplier)));
		data.setUnlimtdammo(true);
		sendpMessage(plugin.getMessage("ammo_now_unlimited"));
		if (plugin.debug) plugin.outConsole("{0} has activated super pickaxe. Duration: {1}", player.getName(), duration);
		new UnlimitedAmmoThread().runTaskLater(plugin, duration);
	}
	
	public class UnlimitedAmmoThread extends BukkitRunnable
	{
		@Override
		public void run()
		{
			PlayerData data = getPlayerData(player);
			int level = data.getLevel();
			int duration = (20*(plugin.ammobaseduration + (level*plugin.ammomultiplier)));
			data.setUnlimtdammo(false);
			sendpMessage(plugin.getMessage("ammo_nolonger_unlimited"));
			data.setAmmocooling(true);
			int cooldown = (duration*plugin.ammocooldown);
			data.setAmmocd(cooldown);
			if (plugin.debug) plugin.outConsole("{0} has a cooldown of {1} for super pickaxe", player.getName(), cooldown);
		}
	}
}