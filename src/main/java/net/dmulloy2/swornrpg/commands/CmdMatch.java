package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */

public class CmdMatch extends SwornRPGCommand
{
	public CmdMatch(SwornRPG plugin)
	{
		super(plugin);
		this.name = "match";
		this.aliases.add("matchplayer");
		this.requiredArgs.add("string");
		this.description = "Match a string with the closest player";
		this.permission = Permission.MATCH;

		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		OfflinePlayer match = Util.matchOfflinePlayer(args[0]);
		if (match != null)
		{
			sendpMessage(plugin.getMessage("match_successful"), match.getName());
		}
		else
		{
			err(plugin.getMessage("match_failed"), args[0]);
		}
	}
}