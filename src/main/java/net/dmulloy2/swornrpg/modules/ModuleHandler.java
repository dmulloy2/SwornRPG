/**
 * SwornRPG - a Bukkit plugin
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.swornrpg.modules;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornapi.types.Reloadable;

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
				scheduleModule((TickableModule) module);
		}
	}

	private void scheduleModule(TickableModule tickable)
	{
		int interval = tickable.getInterval();

		BukkitTask task;
		if (tickable.isAsync())
			task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, tickable, interval, interval);
		else
			task = plugin.getServer().getScheduler().runTaskTimer(plugin, tickable, interval, interval);

		tickable.setTaskId(task.getTaskId());
	}

	@Override
	public void reload()
	{
		for (Module module : modules)
		{
			module.loadSettings();

			// Special case for tickable modules
			if (module instanceof TickableModule tickable)
			{
				if (tickable.isScheduled())
					tickable.cancel();

				if (tickable.isEnabled())
					scheduleModule(tickable);
			}
		}
	}
}
