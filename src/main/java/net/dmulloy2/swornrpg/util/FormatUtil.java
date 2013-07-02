package net.dmulloy2.swornrpg.util;

import java.text.MessageFormat;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

/**
 * @author dmulloy2
 */

public class FormatUtil 
{
	public static String format(String format, Object... objects)
	{
		String ret = MessageFormat.format(format, objects);
//		ret = WordUtils.capitalize(ret, new char[]{'.'});
		return ChatColor.translateAlternateColorCodes('&', ret);
	}
	
	public static String getFriendlyName(Material mat)
	{
		return getFriendlyName(mat.toString());
	}
	
	public static String getFriendlyName(EntityType entityType)
	{
		return getFriendlyName(entityType.toString());
	}
	
	public static String getFriendlyName(String string)
	{
		String ret = string.toLowerCase();
		ret = ret.replaceAll("_", " ");
		return (WordUtils.capitalize(ret));
	}
	
	public static String getArticle(String string)
	{
		string = string.toLowerCase();
		if (string.startsWith("a") || string.startsWith("e") || string.startsWith("i") || string.startsWith("o") || string.startsWith("u"))
		{
			return "an";
		}
		
		return "a";
	}
}