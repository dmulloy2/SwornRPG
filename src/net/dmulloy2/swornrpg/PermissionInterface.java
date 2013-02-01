package net.dmulloy2.swornrpg;

import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */

public class PermissionInterface
{
  public static boolean checkPermission(Player player, String command)
  {
	  return player.hasPermission(command) || player.isOp();
  }
}