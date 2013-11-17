package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;

/**
 * @author dmulloy2
 */

public class CmdCoordsToggle extends SwornRPGCommand
{
	public CmdCoordsToggle(SwornRPG plugin)
	{
		super(plugin);
		this.name = "coordstoggle";
		this.description = "Toggle death coordinate messages";
		this.permission = Permission.COORDSTOGGLE;

		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		PlayerData data = getPlayerData(player);
		if (data.isDeathCoordsEnabled())
		{
			data.setDeathCoordsEnabled(false);
			sendpMessage(plugin.getMessage("deathcoords_disabled"));
		}
		else
		{
			data.setDeathCoordsEnabled(true);
			sendpMessage(plugin.getMessage("deathcoords_enabled"));
		}	
	}
}