package net.dmulloy2.swornrpg;

import org.bukkit.command.CommandSender;

/**
 * @author t7seven7t
 */

public class PermissionInterface
{
	public static boolean checkPermission(CommandSender player, String command)
	{
		return player.hasPermission(command) || player.isOp();
	}
}