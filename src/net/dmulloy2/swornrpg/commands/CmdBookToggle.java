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
		this.optionalArgs.add("enabled");
		this.optionalArgs.add("disabled");
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
				sendpMessage(plugin.prefix + "&cDeath coordinate messages enabled");
			}
			else
			{
				data.setDeathbookdisabled(true);
				sendpMessage(plugin.prefix + "&cDeath coordinate messages disabled");
			}
		}
		else if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("enabled"))
			{
				data.setDeathbookdisabled(false);
				sendpMessage(plugin.prefix + "&eDeath coordinate messages enabled");
			}
			else if (args[0].equalsIgnoreCase("disabled"))
			{
				data.setDeathbookdisabled(true);
				sendpMessage(plugin.prefix + "&eDeath coordinate messages disabled");
			}
			else
			{
				sendpMessage(plugin.invalidargs + "&c(/deathbook [enabled/disabled])");
			}
		}			
	}
}