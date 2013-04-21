package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;

public class CmdVersion extends SwornRPGCommand
{
	public CmdVersion(SwornRPG plugin)
	{
		super(plugin);
		this.name = "version";
		this.aliases.add("v");
		this.description = "Display SwornRPG version";
		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		sendMessage("&4==== &6SwornRPG &4====");
		sendMessage("&6Author: &edmulloy2");
		sendMessage("&6Loaded Version: &e{0}", plugin.getDescription().getFullName());
		sendMessage("&6Update Available: &e{0}", plugin.updateNeeded() ? "true" : "false");
		sendMessage("&6Download:&e http://dev.bukkit.org/server-mods/swornrpg");
	}
}
