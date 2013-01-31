package net.dmulloy2.swornrpg.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.dmulloy2.swornrpg.main;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Util {
	/**
	 * @author orange451
	 * @editor dmulloy2
	 */
  public static main plugin;
  public static World world;
  public static Server server;

  public static void Initialize(main plugin)
  {
    Util.plugin = plugin;
    server = plugin.getServer();
    world = (World)server.getWorlds().get(0);
  }

  public static Player MatchPlayer(String player) {
    List<Player> players = server.matchPlayer(player);

    if (players.size() == 1) {
      return (Player)players.get(0);
    }
    return null;
  }

  public static List<Player> Who()
  {
    Player[] players = server.getOnlinePlayers();
    List<Player> players1 = new ArrayList<Player>();
    for (int i = 0; i < players.length; i++) {
      players1.add(players[i]);
    }
    return players1;
  }

  public static double magnitude(int x1, int y1, int z1, int x2, int y2, int z2) {
    int xdist = x1 - x2;
    int ydist = y1 - y2;
    int zdist = z1 - z2;
    return Math.sqrt(xdist * xdist + ydist * ydist + zdist * zdist);
  }

  public static int point_distance(Location loc1, Location loc2) {
    int p1x = (int)loc1.getX();
    int p1y = (int)loc1.getY();
    int p1z = (int)loc1.getZ();

    int p2x = (int)loc2.getX();
    int p2y = (int)loc2.getY();
    int p2z = (int)loc2.getZ();
    return (int)magnitude(p1x, p1y, p1z, p2x, p2y, p2z);
  }

  public static int random(int x) {
    Random rand = new Random();
    return rand.nextInt(x);
  }

  public static double lengthdir_x(double len, double dir) {
    return len * Math.cos(Math.toRadians(dir));
  }

  public static double lengthdir_y(double len, double dir) {
    return -len * Math.sin(Math.toRadians(dir));
  }

  public static double point_direction(double x1, double y1, double x2, double y2) {
    double d;
    try {
      d = Math.toDegrees(Math.atan((y2 - y1) / (x2 - x1)));
    }
    catch (Exception e)
    {
      d = 0.0D;
    }
    if ((x1 > x2) && (y1 > y2))
    {
      return -d + 180.0D;
    }
    if ((x1 < x2) && (y1 > y2))
    {
      return -d;
    }
    if (x1 == x2)
    {
      if (y1 > y2)
        return 90.0D;
      if (y1 < y2)
        return 270.0D;
    }
    if ((x1 > x2) && (y1 < y2))
    {
      return -d + 180.0D;
    }
    if ((x1 < x2) && (y1 < y2))
    {
      return -d + 360.0D;
    }
    if (y1 == y2)
    {
      if (x1 > x2)
        return 180.0D;
      if (x1 < x2)
        return 0.0D;
    }
    return 0.0D;
  }
}