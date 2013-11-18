package net.dmulloy2.swornrpg.types;

import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author dmulloy2
 */

@Getter
public class Updater
{
	private boolean updateAvailable;
	private String latestVersion;
	
	private final JavaPlugin plugin;
	public Updater(JavaPlugin plugin)
	{
		this.plugin = plugin;
	}

	public void init()
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				try
				{
					checkForUpdates();
					if (updateAvailable)
					{
						plugin.getLogger().info("Version " + latestVersion + " is now available!");
						plugin.getLogger().info("Get it at: " + getPluginURL());
					}
				}
				catch (Exception e)
				{
					//
				}
			}
		}.runTaskTimerAsynchronously(plugin, 20L, 432000L);
	}

	private final void checkForUpdates()
	{
		latestVersion = getBukkitDevVersion();

		double newVersion = getVersion(latestVersion);
		double oldVersion = getVersion(plugin.getDescription().getVersion());

		updateAvailable = newVersion > oldVersion;
	}

    /** Update Checker **/
   	private final String getBukkitDevVersion()
    {
        try
        {
            URL url = new URL(getPluginURL());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("item");
            Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) 
            {
                Element firstElement = (Element) firstNode;
                NodeList firstElementTagName = firstElement.getElementsByTagName("title");
                Element firstNameElement = (Element) firstElementTagName.item(0);
                NodeList firstNodes = firstNameElement.getChildNodes();
                return firstNodes.item(0).getNodeValue().replaceAll("[a-zA-Z ]", "");
            }
        }
        catch (Exception e) 
        {
        	//
        }

        return plugin.getDescription().getVersion();
    }

   	private final double getVersion(String version)
   	{
   		if (version.indexOf(" ") >= 0)
   		{
   			version = version.substring(0, version.indexOf(" "));
   		}

   		return Double.parseDouble(version);
   	}

   	public final String getPluginURL()
   	{
   		return "http://dev.bukkit.org/bukkit-plugins/" + plugin.getName().toLowerCase()  + "/files.rss";
   	}
}