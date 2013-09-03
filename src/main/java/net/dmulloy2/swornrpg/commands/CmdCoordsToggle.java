package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

/**
 * @author dmulloy2
 */

public class CmdCoordsToggle extends SwornRPGCommand
{
	public CmdCoordsToggle(SwornRPG plugin)
	{
		super(plugin);
		this.name = "deathcoords";
		this.description = "Toggle death coordinates";
		this.aliases.add("deathbook");
		this.aliases.add("deathmail");
		this.aliases.add("coordstoggle");
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		final PlayerData data = getPlayerData(player);
		if (data.isDeathbookdisabled())
		{
			data.setDeathbookdisabled(false);
			sendpMessage(plugin.getMessage("deathcoords_enabled"));
		}
		else
		{
			data.setDeathbookdisabled(true);
			sendpMessage(plugin.getMessage("deathcoords_disabled"));
		}	
	}
}