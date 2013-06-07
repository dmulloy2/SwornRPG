package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdASay extends SwornRPGCommand
{
	public CmdASay (SwornRPG plugin)
	{
		super(plugin);
		this.name = "asay";
		this.aliases.add("adm");
		this.description = "Alternate admin say command";
		this.requiredArgs.add("message");
		this.permission = PermissionType.CMD_ASAY.permission;
		
		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		StringBuilder message = new StringBuilder();
		for (int i=0; i<args.length; i++)
		{
			message.append(args[i] + " ");
		}
		
		plugin.getServer().broadcastMessage(FormatUtil.format(plugin.getMessage("admin_say"), message.toString()));
	}
}