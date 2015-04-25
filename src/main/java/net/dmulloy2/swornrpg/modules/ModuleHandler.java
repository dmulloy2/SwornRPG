/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornrpg.modules;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.types.Reloadable;

import org.bukkit.scheduler.BukkitTask;

/**
 * @author dmulloy2
 */

public class ModuleHandler implements Reloadable
{
	private final List<Module> modules;

	private final SwornRPG plugin;
	public ModuleHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.modules = new ArrayList<>();
		this.load();
	}

	private void load()
	{
		// Load default modules
		modules.add(new BlockRedemption(plugin));
		modules.add(new Enchanting(plugin));
		modules.add(new Fishing(plugin));
		modules.add(new Herbalism(plugin));
		modules.add(new MinecraftXP(plugin));
		modules.add(new MobKills(plugin));
		modules.add(new OnlineTime(plugin));
		modules.add(new PlayerKills(plugin));
		modules.add(new RareDrops(plugin));
		modules.add(new Salvaging(plugin));
		modules.add(new Taming(plugin));

		// TODO Add support for custom modules

		// Schedule tickable modules
		for (Module module : modules)
		{
			if (module.isEnabled() && module instanceof TickableModule)
			{
				TickableModule tickable = (TickableModule) module;
				int interval = tickable.getInterval();

				BukkitTask task;
				if (tickable.isAsync())
					task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, tickable, interval, interval);
				else
					task = plugin.getServer().getScheduler().runTaskTimer(plugin, tickable, interval, interval);

				tickable.setTaskId(task.getTaskId());
			}
		}
	}

	@Override
	public void reload()
	{
		for (Module module : modules)
		{
			module.loadSettings();

			// Special case for tickable modules
			if (module instanceof TickableModule)
			{
				TickableModule tickable = (TickableModule) module;
				if (tickable.isScheduled() && ! tickable.isEnabled())
					tickable.cancel();
			}
		}
	}
}