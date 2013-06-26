package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.permissions.PermissionType;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
		PlayerData data = getPlayerData(player);
		if (data.isSitting())
		{
			err(plugin.getMessage("unride_sitting"));
			return;
		}
		
		if (player.getVehicle() == null)
		{
			err(plugin.getMessage("not_riding"));
			return;
		}

		Entity target = player.getVehicle();
		if (target instanceof Player)
		{
			Player targetp = (Player)player.getVehicle();
			PlayerData data1 = getPlayerData(targetp);
			data1.setVehicle(false);
		}
		player.leaveVehicle();
		data.setRiding(false);
	}
}