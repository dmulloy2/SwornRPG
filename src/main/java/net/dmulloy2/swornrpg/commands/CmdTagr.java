package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdTagr extends SwornRPGCommand
{
	public CmdTagr (SwornRPG plugin)
	{
		super(plugin);
		this.name = "tagr";
		this.description = "Reset a player's tag";
		this.aliases.add("tagreset");
		this.optionalArgs.add("player");
		this.permission = PermissionType.CMD_TAG_RESET.permission;
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (!plugin.getPluginManager().isPluginEnabled("TagAPI"))
		{
			sendpMessage(plugin.getMessage("no_tagapi"));
			if (plugin.debug) plugin.outConsole(plugin.getMessage("log_tagapi_null"));
			return;
		}
		
		if (args.length == 0) 
		{
			plugin.getTagManager().removeTagChange(sender.getName());
			sendpMessage(plugin.getMessage("tag_reset_self"));
		}
		
		if (args.length == 1)
		{
			if (args[0].length() > 16) 
			{
				sendpMessage(plugin.getMessage("username_too_large"));
				return;
			}
			Player target = Util.matchPlayer(args[0]);
			plugin.getTagManager().removeTagChange(target.getName());
			sendpMessage(plugin.getMessage("tag_reset_resetter"), target.getName());
			sendMessageTarget(plugin.getMessage("tag_reset_resetee"), target);
		}
	}
}