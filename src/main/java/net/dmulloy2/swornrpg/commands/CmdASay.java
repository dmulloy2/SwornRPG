package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdASay extends SwornRPGCommand
{
	public CmdASay(SwornRPG plugin)
	{
		super(plugin);
		this.name = "asay";
		this.aliases.add("adm");
		this.requiredArgs.add("message");
		this.description = "Alternate admin say command";
		this.permission = Permission.CMD_ASAY;
		
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

		plugin.getServer().broadcastMessage(FormatUtil.format(plugin.getMessage("admin_say"), message.toString()));
	}
}