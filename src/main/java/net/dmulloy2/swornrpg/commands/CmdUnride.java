package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;

/**
 * @author dmulloy2
 */

public class CmdUnride extends SwornRPGCommand
{
	public CmdUnride (SwornRPG plugin)
	{
		super(plugin);
		this.name = "unride";
		this.description = "Get off of a player's head";
		this.permission = PermissionType.CMD_RIDE.permission;
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (player.getVehicle() == null)
		{
			err(plugin.getMessage("not_riding"));
			return;
		}
		
		sendMessage(getMessage("unride_successful"));
		
		player.leaveVehicle();
	}
}