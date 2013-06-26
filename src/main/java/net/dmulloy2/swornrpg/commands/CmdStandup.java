package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;

/**
 * @author dmulloy2
 */

public class CmdStandup extends SwornRPGCommand
{
	public CmdStandup (SwornRPG plugin)
	{
		super(plugin);
		this.name = "standup";
		this.aliases.add("stand");
		this.description = "Get out of your chair";
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		PlayerData data = getPlayerData(player);
		if (! data.isSitting())
		{
			err(plugin.getMessage("not_sitting"));
			return;
		}
		
		Entity vehicle = player.getVehicle();
		if (vehicle instanceof Arrow)
		{
			player.leaveVehicle();
			vehicle.remove();
			player.teleport(vehicle.getLocation().add(0, 1, 0));
			data.setSitting(false);
		}
	}
}