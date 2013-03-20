package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdMatch extends SwornRPGCommand
{
	public CmdMatch (SwornRPG plugin)
	{
		super(plugin);
		this.name = "match";
		this.aliases.add("matchplayer");
		this.description = "Match a string with a player";
		this.requiredArgs.add("string");
		this.permission = PermissionType.CMD_MATCH.permission;
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		Player match = Util.matchPlayer(args[0]);
		if (match != null)
		{
			sendpMessage("&aMatch found! '" + match.getName() + "'");;
		}
		else
		{
			OfflinePlayer offlinematch = Util.matchOfflinePlayer(args[0]);
			if (offlinematch != null)
			{
				sendpMessage("&aMatch found! '" + offlinematch.getName() + "'");
			}
			else
			{
				sendpMessage("&cError, no match was found for '" + args[0] + "'");
			}
		}
	}
}