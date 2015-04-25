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

import net.dmulloy2.swornrpg.SwornRPG;

/**
 * A module that runs at a specified interval.
 * @author dmulloy2
 */

public abstract class TickableModule extends Module implements Runnable
{
	private int taskId = -1;

	public TickableModule(SwornRPG plugin)
	{
		super(plugin);
	}

	/**
	 * Gets this module's interval (in seconds).
	 * @return This module's interval
	 */
	public abstract int getInterval();

	/**
	 * Whether or not this module is async. Defaults to false.
	 * @return True if async, false if not.
	 */
	public boolean isAsync()
	{
		return false;
	}

	/**
	 * Gets this module's task id.
	 * @return This module's task id, or -1 if not scheduled.
	 */
	public final int getTaskId()
	{
		return taskId;
	}

	/**
	 * Sets this module's task id.
	 * @param taskId Task id
	 */
	public final void setTaskId(int taskId)
	{
		this.taskId = taskId;
	}

	/**
	 * Cancels this module's task.
	 */
	public final void cancel()
	{
		if (taskId != -1)
			plugin.getServer().getScheduler().cancelTask(taskId);
	}

	/**
	 * Whether or not this task is scheduled.
	 * @return True if scheduled, false if not.
	 */
	public final boolean isScheduled()
	{
		return taskId != -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ModuleType getType()
	{
		return ModuleType.TICKABLE;
	}
}
