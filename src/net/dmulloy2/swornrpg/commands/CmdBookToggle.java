package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

/**
 * @author dmulloy2
 */

public class CmdBookToggle extends SwornRPGCommand
{
	public CmdBookToggle (SwornRPG plugin)
	{
		super(plugin);
		this.name = "deathcoords";
		this.description = "Toggle the reception of death coordinates";
		this.aliases.add("deathbook");
		this.aliases.add("deathmail");
		this.optionalArgs.add("enable");
		this.optionalArgs.add("disable");
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
				sendpMessage(plugin.getMessage("deathbook_enabled"));
			}
			else
			{
				data.setDeathbookdisabled(true);
				sendpMessage(plugin.getMessage("deathbook_disabled"));
			}
		}
		else if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("enable"))
			{
				data.setDeathbookdisabled(false);
				sendpMessage(plugin.getMessage("deathbook_enabled"));
			}
			else if (args[0].equalsIgnoreCase("disable"))
			{
				data.setDeathbookdisabled(true);
				sendpMessage(plugin.getMessage("deathbook_disabled"));
			}
			else
			{
				sendpMessage(plugin.getMessage("invalidargs") + "&c(/deathbook [enable/disable])");
			}
		}			
	}
}