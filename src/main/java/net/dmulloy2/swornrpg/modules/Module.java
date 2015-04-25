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

import lombok.Getter;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Basic module class.
 * @author dmulloy2
 */

@Getter
public abstract class Module implements Listener
{
	protected boolean enabled;

	protected final SwornRPG plugin;
	public Module(SwornRPG plugin)
	{
		this.plugin = plugin;

		// Load settings
		loadSettings();
	}

	/**
	 * Sets the enabled state of this module.
	 * @param isEnabled True if enabled, false if not
	 */
	public final void setEnabled(boolean isEnabled)
	{
		if (enabled != isEnabled)
		{
			if (isEnabled)
				plugin.getServer().getPluginManager().registerEvents(this, plugin);
			else
				HandlerList.unregisterAll(this);
		}

		this.enabled = isEnabled;
	}

	/**
	 * Loads settings for this module.
	 */
	public abstract void loadSettings();

	/**
	 * Gets this module's type.
	 * @return This module's type.
	 */
	public ModuleType getType()
	{
		return ModuleType.BASIC;
	}

	// ---- Useful Methods

	protected final boolean isFactionsApplicable(Player player, boolean safeZone)
	{
		return plugin.isSwornNationsEnabled() && plugin.getSwornNationsHandler().isApplicable(player, safeZone);
	}

	protected final PlayerData getData(OfflinePlayer player)
	{
		return plugin.getPlayerDataCache().getData(player);
	}

	protected final String getName(Entity entity)
	{
		Player player = getPlayer(entity);
		if (player != null)
			return player.getName();

		return FormatUtil.getFriendlyName(entity.getType());
	}

	protected final Player getPlayer(Entity entity)
	{
		if (entity instanceof Player)
		{
			return (Player) entity;
		}

		if (entity instanceof Projectile)
		{
			Projectile proj = (Projectile) entity;
			if (proj.getShooter() instanceof Player)
				return (Player) proj.getShooter();
		}

		return null;
	}
}
