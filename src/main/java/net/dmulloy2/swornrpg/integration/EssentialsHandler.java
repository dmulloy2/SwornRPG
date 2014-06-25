/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.swornrpg.integration;

import lombok.Getter;
import net.dmulloy2.handlers.IntegrationHandler;
import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.plugin.PluginManager;

import com.earth2me.essentials.Essentials;

/**
 * @author dmulloy2
 */

public class EssentialsHandler extends IntegrationHandler
{
	private @Getter boolean enabled;
	private @Getter Essentials essentials;

	private final SwornRPG plugin;
	public EssentialsHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.setup();
	}

	@Override
	public void setup()
	{
		try
		{
			PluginManager pm = plugin.getServer().getPluginManager();
			if (pm.getPlugin("Essentials") != null)
			{
				essentials = (Essentials) pm.getPlugin("Essentials");
				enabled = true;

				plugin.getLogHandler().log(plugin.getMessage("log_integration_essentials"));
			}
		}
		catch (Throwable ex)
		{
			enabled = false;
		}
	}
}