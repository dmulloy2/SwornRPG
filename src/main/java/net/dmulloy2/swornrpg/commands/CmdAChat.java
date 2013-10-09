package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdAChat extends SwornRPGCommand
{
	public CmdAChat(SwornRPG plugin)
	{
		super(plugin);
		this.name = "a";
		this.aliases.add("achat");
		this.requiredArgs.add("message");
		this.description = "Talk in admin-only chat";
		this.permission = Permission.CMD_ACHAT;
		
		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		StringBuilder message = new StringBuilder();
		for (String arg : args)
		{
			message.append(arg + " ");
		}

		if (message.lastIndexOf(" ") >= 0)
		{
			message.deleteCharAt(message.lastIndexOf(" "));
		}

		String name = isPlayer() ? player.getName() : "Console";
		String node = plugin.getPermissionHandler().getPermissionString(permission);
		plugin.getServer().broadcast(FormatUtil.format(plugin.getMessage("achat"), name, message.toString()), node);
	}
}