package net.dmulloy2.swornrpg;

import org.bukkit.command.CommandSender;

/**
 * @author t7seven7t
 */

public class Perms
{
	public static boolean has(CommandSender player, String command)
	{
		return player.hasPermission(command) || player.isOp();
	}
}