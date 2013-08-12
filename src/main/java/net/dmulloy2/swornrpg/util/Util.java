package net.dmulloy2.swornrpg.util;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;

/**
 * @author dmulloy2
 */

public class Util 
{
	public static Player matchPlayer(String pl)
	{
		List<Player> players = Bukkit.matchPlayer(pl);
		
		if (players.size() >= 1)
			return players.get(0);
		
		return null;
	}
	
	public static OfflinePlayer matchOfflinePlayer(String pl)
	{
		if (matchPlayer(pl) != null)
			return matchPlayer(pl);
		
		for (OfflinePlayer o : Bukkit.getOfflinePlayers())
		{
			if (o.getName().equalsIgnoreCase(pl))
				return o;
		}
		
		return null;
	}
	
	public static boolean isBanned(OfflinePlayer p)
	{
		for (OfflinePlayer banned : Bukkit.getBannedPlayers()) 
		{
			if (p.getName().equalsIgnoreCase(banned.getName()))
				return true;
		}
		return false;
	}

	public static int random(int x)
	{
		Random rand = new Random();
		return rand.nextInt(x);
	}

	public static int pointDistance(Location loc1, Location loc2)
	{
		int p1x = (int) loc1.getX();
		int p1y = (int) loc1.getY();
		int p1z = (int) loc1.getZ();
	
		int p2x = (int) loc2.getX();
		int p2y = (int) loc2.getY();
		int p2z = (int) loc2.getZ();
		
		return (int) magnitude(p1x, p1y, p1z, p2x, p2y, p2z);
	}
	
	public static double magnitude(int x1, int y1, int z1, int x2, int y2, int z2) 
	{
		int xdist = x1 - x2;
		int ydist = y1 - y2;
		int zdist = z1 - z2;
		return Math.sqrt(xdist * xdist + ydist * ydist + zdist * zdist); 
	}
	
	public static IEssentials getEssentials()
	{
		if (Bukkit.getPluginManager().isPluginEnabled("Essentials"))
		{
			Plugin essPlugin = Bukkit.getPluginManager().getPlugin("Essentials");
			return (IEssentials)essPlugin;
		}
		
		return null;
	}
	
	public static User getEssentialsUser(Player player)
	{
		IEssentials ess = getEssentials();
		if (ess != null)
		{
			return ess.getUser(player.getName());
		}
		
		return null;
	}
	
	public static boolean isValidMaterial(String string)
	{
		string = string.toLowerCase();
		for (Material mat : Material.values())
		{
			if (mat.toString().equalsIgnoreCase(string))
				return true;
		}
		
		return false;
	}
	
	public static int parseInt(String string)
	{
		try
		{
			return Integer.parseInt(string);
		}
		catch (NumberFormatException e)
		{
			return -1;
		}
	}
}