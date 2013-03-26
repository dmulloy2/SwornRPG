package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

/**
 * @author dmulloy2
 */

public class CmdTagr extends SwornRPGCommand
{
	PluginManager pm = Bukkit.getPluginManager();
	public CmdTagr (SwornRPG plugin)
	{
		super(plugin);
		this.name = "tagr";
		this.description = "Reset a player's tag";
		this.aliases.add("resettag");
		this.optionalArgs.add("player");
		this.permission = PermissionType.CMD_TAGR.permission;
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (pm.isPluginEnabled("TagAPI"))
		{
			if (args.length == 0) 
			{
				this.plugin.removeTagChange(sender.getName());
				sendpMessage(plugin.getMessage("tag_reset_self"));
			}
			else if (args.length == 1)
			{
				if (args[0].length() > 16) 
				{
					sendpMessage(plugin.getMessage("username_too_large"));
				}
				else 
				{
					Player target = Util.matchPlayer(args[0]);
					this.plugin.removeTagChange(target.getName());
					sendpMessage(plugin.getMessage("tag_reset_resetter"), target.getName());
					sendMessageTarget(plugin.getMessage("tag_reset_resetee"), target);
				}
			}
		}
		else
		{
			sendpMessage(plugin.getMessage("no_tagapi"));
			plugin.outConsole("You must have TagAPI installed to perform Tag related commands. http://dev.bukkit.org/server-mods/tag");
		}
	}
}