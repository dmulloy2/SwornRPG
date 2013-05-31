package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;

public class CmdReload extends SwornRPGCommand
{
	public CmdReload(SwornRPG plugin)
	{
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.description = "Reload SwornRPG";
		this.permission = PermissionType.CMD_RELOAD.permission;
		this.mustBePlayer = false;
		this.usesPrefix = true;
	}
	
	public void perform()
	{
		sendpMessage("&aReloading Configuration...");
		plugin.reload();
		sendpMessage("&aReload Complete!");
	}
}
