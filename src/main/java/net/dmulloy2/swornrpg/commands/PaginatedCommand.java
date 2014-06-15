package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;

/**
 * Represents a command that has pagination
 * 
 * @author dmulloy2
 */

public abstract class PaginatedCommand extends net.dmulloy2.commands.PaginatedCommand
{
	protected final SwornRPG plugin;
	public PaginatedCommand(SwornRPG plugin)
	{
		super(plugin);
		this.plugin = plugin;
	}
}