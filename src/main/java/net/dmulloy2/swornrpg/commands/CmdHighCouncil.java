package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdHighCouncil extends SwornRPGCommand
{
	public CmdHighCouncil(SwornRPG plugin)
	{
		super(plugin);
		this.name = "hc";
		this.aliases.add("highcouncil");
		this.requiredArgs.add("message");
		this.description = "Talk in council chat";
		this.permission = Permission.HIGHCOUNCIL;
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

		String node = plugin.getPermissionHandler().getPermissionString(permission);
		plugin.getServer().broadcast(FormatUtil.format(plugin.getMessage("council"), getName(sender), message.toString()), node);
	}
}