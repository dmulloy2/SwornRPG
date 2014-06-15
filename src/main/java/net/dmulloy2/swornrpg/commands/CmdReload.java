package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.types.Reloadable;

/**
 * @author dmulloy2
 */

public class CmdReload extends SwornRPGCommand implements Reloadable
{
	public CmdReload(SwornRPG plugin)
	{
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.description = "Reload SwornRPG";
		this.permission = Permission.RELOAD;

		this.usesPrefix = true;
	}
	
	@Override
	public void perform()
	{
		reload();
	}

	@Override
	public void reload()
	{
		long start = System.currentTimeMillis();

		sendpMessage("&aReloading Configuration...");
		
		plugin.reload();
		
		sendpMessage("&aReload Complete! (Took {0} ms)", System.currentTimeMillis() - start);
	}
}