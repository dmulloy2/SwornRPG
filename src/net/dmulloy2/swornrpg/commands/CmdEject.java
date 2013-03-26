package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdEject extends SwornRPGCommand
{
	public CmdEject (SwornRPG plugin)
	{
		super(plugin);
		this.name = "eject";
		this.description = "Remove a player from your head";
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (player.getPassenger() != null)
		{
			Entity target = player.getPassenger();
			if (target instanceof Player)
			{
				Player targetp = (Player)player.getPassenger();
				PlayerData data = getPlayerData(targetp);
				data.setRiding(false);
			}
			player.eject();
			PlayerData data = getPlayerData(player);
			data.setVehicle(false);
		}
		else
		{
			sendpMessage(plugin.getMessage("no_passenger"));
		}
	}
}