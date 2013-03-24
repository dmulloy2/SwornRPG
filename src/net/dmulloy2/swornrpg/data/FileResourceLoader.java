package net.dmulloy2.swornrpg.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.dmulloy2.swornrpg.SwornRPG;

/**
 * @author t7seven7t
 */
public class FileResourceLoader extends ClassLoader 
{
	private final transient File dataFolder;

	public FileResourceLoader(final ClassLoader classLoader, final SwornRPG plugin)
	{
		super(classLoader);
		this.dataFolder = plugin.getDataFolder();
	}

	@Override
	public URL getResource(final String string)
	{
		final File file = new File(dataFolder, string);
		if (file.exists())
		{
			try
			{
				return file.toURI().toURL();
			}
			catch (MalformedURLException ex)
			{
			}
		}
		return super.getResource(string);
	}
	
	@Override
	public InputStream getResourceAsStream(final String string)
	{
		final File file = new File(dataFolder, string);
		if (file.exists())
		try
		{
			return new FileInputStream(file);
		}
		catch (FileNotFoundException ex)
		{
		}
		return super.getResourceAsStream(string);
	}	
}