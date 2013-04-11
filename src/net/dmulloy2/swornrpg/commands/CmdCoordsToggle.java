package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

/**
 * @author dmulloy2
 */

public class CmdCoordsToggle extends SwornRPGCommand
{
	public CmdCoordsToggle (SwornRPG plugin)
	{
		super(plugin);
		this.name = "deathcoords";
		this.description = "Toggle the reception of death coordinates";
		this.aliases.add("deathbook");
		this.aliases.add("deathmail");
		this.aliases.add("coordstoggle");
		this.optionalArgs.add("on");
		this.optionalArgs.add("off");
		this.mustBePlayer = true;
	}
	
	public void perform()
	{
		final PlayerData data = getPlayerData(player);
		if (args.length == 0)
		{
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
		else if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("on"))
			{
				data.setDeathbookdisabled(false);
				sendpMessage(plugin.getMessage("deathcoords_enabled"));
			}
			else if (args[0].equalsIgnoreCase("off"))
			{
				data.setDeathbookdisabled(true);
				sendpMessage(plugin.getMessage("deathcoords_disabled"));
			}
			else
			{
				sendpMessage(plugin.getMessage("invalidargs") + "&c(/deathbook &6[on/off]&c)");
			}
		}			
	}
}