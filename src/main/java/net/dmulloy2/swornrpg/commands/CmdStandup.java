package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;

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
		Entity vehicle = player.getVehicle();
		if (vehicle == null)
		{
			err(getMessage("not_sitting"));
			return;
		}
		
		if (vehicle instanceof Arrow)
		{
			player.leaveVehicle();
			vehicle.remove();
			player.teleport(vehicle.getLocation().add(0.5D, 1.0D, 0.5D));
		}
		else
		{
			err(getMessage("not_sitting"));
		}
	}
}