package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.util.FormatUtil;

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
		String name = getName(sender);
		String message = FormatUtil.join(" ", args);
		String node = plugin.getPermissionHandler().getPermissionString(permission);
		plugin.getServer().broadcast(FormatUtil.format(plugin.getMessage("council_chat"), name, message), node);
	}
}