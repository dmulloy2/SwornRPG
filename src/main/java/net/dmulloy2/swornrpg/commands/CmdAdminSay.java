package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdAdminSay extends SwornRPGCommand
{
	public CmdAdminSay(SwornRPG plugin)
	{
		super(plugin);
		this.name = "adminsay";
		this.aliases.add("asay");
		this.addRequiredArg("message");
		this.description = "Alternate admin say command";
		this.permission = Permission.ADMINSAY;
	}

	@Override
	public void perform()
	{
		String message = FormatUtil.join(" ", args);
		plugin.getServer().broadcastMessage(FormatUtil.format(plugin.getMessage("admin_say"), message));
	}
}