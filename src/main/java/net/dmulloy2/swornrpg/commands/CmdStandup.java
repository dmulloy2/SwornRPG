package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;

/**
 * @author dmulloy2
 */

public class CmdStandup extends SwornRPGCommand
{
	public CmdStandup(SwornRPG plugin)
	{
		super(plugin);
		this.name = "standup";
		this.aliases.add("stand");
		this.description = "Get out of your chair";
		this.permission = Permission.STANDUP;
		
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
		
		if (! (vehicle instanceof Arrow))
		{
			err(getMessage("not_sitting"));
			return;
		}
		
//		player.teleport(vehicle.getLocation().add(0.5D, 1.0D, 0.5D));
		player.teleport(vehicle.getLocation().add(0, 1.0D, 0));
		
		vehicle.remove();
	}
}