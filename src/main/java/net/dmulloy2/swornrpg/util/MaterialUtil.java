package net.dmulloy2.swornrpg.util;

/**
 * Util dealing with the loss of item id's
 * 
 * @author dmulloy2
 */

public class MaterialUtil
{
	/**
	 * Returns whether or not a String can be parsed as an Integer
	 * 
	 * @param string - String to check
	 * @return Whether or not a String can be parsed as an Integer
	 */
	public static boolean isInteger(String string)
	{
		int ret = -1;
		try
		{
			ret = Integer.parseInt(string);
		}
		catch (Exception e)
		{
			//
		}
		
		return ret != -1;
	}
	
	/**
	 * Returns the {@link org.bukkit.Material} from a given string
	 * 
	 * @param string - String to get the Material from
	 * @return The {@link org.bukkit.Material} from a given string
	 */
	public static org.bukkit.Material getMaterial(String string)
	{
		if (isInteger(string))
		{
			return getMaterial(Integer.parseInt(string));
		}
		else
		{
			return org.bukkit.Material.matchMaterial(string);
		}
	}

	/**
	 * Returns the {@link org.bukkit.Material} from a given integer
	 * 
	 * @param id - Integer to get the Material from
	 * @return The {@link org.bukkit.Material} from a given integer
	 */
	public static org.bukkit.Material getMaterial(int id)
	{
		return net.dmulloy2.swornrpg.types.Material.getMaterial(id).getMaterial();
	}

	/**
	 * Returns whether or not a valid material was supplied
	 * 
	 * @param string - String to check
	 * @return Whether or not a valid material was supplied
	 */
	public static boolean isValidMaterial(String string)
	{
		string = string.toLowerCase();
		for (org.bukkit.Material mat : org.bukkit.Material.values())
		{
			if (mat.toString().equalsIgnoreCase(string))
				return true;
		}
		
		return false;
	}
}