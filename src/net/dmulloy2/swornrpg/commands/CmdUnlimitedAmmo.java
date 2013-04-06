package net.dmulloy2.swornrpg.commands;

import org.bukkit.plugin.PluginManager;

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
		PluginManager pm = plugin.getServer().getPluginManager();
		if (pm.isPluginEnabled("PVPGunPlus"))
		{
			int level = data.getLevel();
			final int duration = (20*(plugin.ammobaseduration + (level*plugin.ammomultiplier)));
			data.setUnlimtdammo(true);
			sendpMessage(plugin.getMessage("ammo_now_unlimited"));
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				@Override
				public void run()
				{
					data.setUnlimtdammo(false);
					sendpMessage(plugin.getMessage("ammo_nolonger_unlimited"));
					data.setAmmocooling(true);
					int cooldown = (duration*plugin.ammocooldown);
					data.setAmmocd(cooldown);
					if (plugin.debug) plugin.outConsole(player.getName() + " has an ammo cooldown of " + cooldown);
				}
			},duration);
		}
	}
}