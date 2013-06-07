package net.dmulloy2.swornrpg.handlers;

import java.util.logging.Level;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class LogHandler
{
	private SwornRPG plugin;
	public LogHandler(SwornRPG plugin) 
	{
		this.plugin = plugin;
	}

	public final void log(Level level, String msg, Object... objects)
	{
		plugin.getServer().getLogger().log(level, FormatUtil.format("[SwornRPG] " + msg, objects));		
	}

	public final void log(String msg, Object... objects)
	{
		log(Level.INFO, msg, objects);
	}
}