package net.dmulloy2.swornrpg.util;
	 
import java.io.File;
import org.bukkit.plugin.PluginDescriptionFile;
import net.dmulloy2.swornrpg.SwornRPG;

/**
 * @author dmulloy2
 */

public class VersionChecker
{
	SwornRPG plugin;
	 
	public VersionChecker(SwornRPG instance)
	{
		this.plugin = instance;
	}
 
	public void versionChecker(PluginDescriptionFile pdfFile)
	{
		String version = plugin.getConfig().getString("Version");
		boolean versionMala = false;
 
		File conf = new File("plugins/" + pdfFile.getName() + "/config.yml");
		if ((!pdfFile.getVersion().equals(version)) || (!conf.exists())) versionMala = true;
		if (versionMala)
		{
			File oldconf = new File("plugins/" + pdfFile.getName() + "/old_config.yml");
			conf.renameTo(oldconf);
			conf.delete();
//			plugin.getConfig().options().copyDefaults(true);
			plugin.saveDefaultConfig();
			System.out.println("[SwornRPG] " + "Your configuration was found to be out of date. Please copy your settings from a file named old_config.yml to a new file named config.yml");
		}
	}
}
