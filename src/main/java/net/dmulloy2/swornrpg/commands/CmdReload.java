package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdReload extends SwornRPGCommand
{
	public CmdReload(SwornRPG plugin)
	{
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.description = "Reload SwornRPG";
		this.permission = Permission.CMD_RELOAD;
		this.mustBePlayer = false;
		this.usesPrefix = true;
	}
	
	@Override
	public void perform()
	{
		sendpMessage("&aReloading Configuration...");
		
		plugin.reload();
		
		sendpMessage("&aReload Complete!");
	}
}