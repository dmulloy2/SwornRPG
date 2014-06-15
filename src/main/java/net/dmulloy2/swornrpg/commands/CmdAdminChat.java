package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdAdminChat extends SwornRPGCommand
{
	public CmdAdminChat(SwornRPG plugin)
	{
		super(plugin);
		this.name = "a";
		this.aliases.add("adminchat");
		this.requiredArgs.add("message");
		this.description = "Talk in admin-only chat";
		this.permission = Permission.ADMINCHAT;
	}

	@Override
	public void perform()
	{
		String name = getName(sender);
		String message = FormatUtil.join(" ", args);
		String node = plugin.getPermissionHandler().getPermissionString(permission);
		plugin.getServer().broadcast(FormatUtil.format(plugin.getMessage("admin_chat"), name, message), node);
	}
}