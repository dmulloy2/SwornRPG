package net.dmulloy2.swornrpg.PermissionsInterface;

import org.bukkit.entity.Player;

public class PermissionInterface
{
  public static boolean checkPermission(Player player, String command)
  {
	  return player.hasPermission(command) || player.isOp();
  }
}