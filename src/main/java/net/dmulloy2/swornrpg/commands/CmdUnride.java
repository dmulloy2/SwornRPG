package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;

/**
 * @author dmulloy2
 */

public class CmdUnride extends SwornRPGCommand
{
	public CmdUnride(SwornRPG plugin)
	{
		super(plugin);
		this.name = "unride";
		this.description = "Get off of a player's head";
		this.permission = Permission.UNRIDE;

		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (! player.isInsideVehicle())
		{
			err(plugin.getMessage("not_riding"));
			return;
		}

		Entity vehicle = player.getVehicle();
		
		player.teleport(vehicle.getLocation().add(0.5D, 1.0D, 0.5D));
		
		if (vehicle instanceof Arrow)
		{
			vehicle.remove();
		}
		else
		{
			player.leaveVehicle();
		}
		
		sendMessage(getMessage("unride_successful"));
	}
}