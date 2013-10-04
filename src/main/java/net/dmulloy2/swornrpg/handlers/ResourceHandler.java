package net.dmulloy2.swornrpg.handlers;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.io.FileResourceLoader;

/**
 * @author dmulloy2
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
			plugin.getLogger().log(Level.SEVERE, "Could not find resource bundle: messages.properties");
		}
	}
	
	public final ResourceBundle getMessages() 
	{
		return messages;
	}
}