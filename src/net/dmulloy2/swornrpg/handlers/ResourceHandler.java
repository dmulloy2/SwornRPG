package net.dmulloy2.swornrpg.handlers;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.FileResourceLoader;

/**
 * @author t7seven7t
 */

public class ResourceHandler 
{
	private ResourceBundle messages;
	
	public ResourceHandler(SwornRPG plugin, ClassLoader classLoader) 
	{
		try
		{
			messages = ResourceBundle.getBundle("messages", Locale.getDefault(), new FileResourceLoader(classLoader, plugin));
		}
		catch (MissingResourceException ex) 
		{
			plugin.outConsole("SEVERE: Could not find messages.properties!");
		}
	}
	
	public ResourceBundle getMessages() 
	{
		return messages;
	}
}
