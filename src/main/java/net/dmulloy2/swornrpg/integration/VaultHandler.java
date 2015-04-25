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
package net.dmulloy2.swornrpg.integration;

import java.util.logging.Level;

import net.dmulloy2.integration.DependencyProvider;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.util.Util;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

/**
 * @author dmulloy2
 */

@SuppressWarnings("deprecation")
public class VaultHandler extends DependencyProvider<Vault>
{
	private Economy economy;
	private Permission permission;

	public VaultHandler(SwornRPG plugin)
	{
		super(plugin, "Vault");
	}

	@Override
	public void onEnable()
	{
		if (! isEnabled())
			return;

		try
		{
			ServicesManager sm = handler.getServer().getServicesManager();
			RegisteredServiceProvider<Economy> economyProvider = sm.getRegistration(Economy.class);
			if (economyProvider != null)
				economy = economyProvider.getProvider();

			RegisteredServiceProvider<Permission> permissionProvider = sm.getRegistration(Permission.class);
			if (permissionProvider != null)
				permission = permissionProvider.getProvider();
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "onEnable()"));
		}
	}

	public final boolean hasAccount(String name)
	{
		if (! isEnabled())
			return false;

		return economy != null ? economy.hasAccount(name) : false;
	}

	public final boolean has(String name, double amount)
	{
		if (! isEnabled())
			return false;

		return economy != null ? economy.has(name, amount) : false;
	}

	public final boolean depositPlayer(Player player, double amount)
	{
		if (! isEnabled() && economy != null)
			return false;

		try
		{
			return economy.depositPlayer(player, amount).transactionSuccess();
		}
		catch (NoSuchMethodError ex)
		{
			return economy.depositPlayer(player.getName(), amount).transactionSuccess();
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "depositPlayer({0}, {1})", player.getName(), amount));
		}

		return false;
	}

	public final boolean withdraw(String name, double amount)
	{
		if (! isEnabled())
			return false;

		return economy != null ? economy.withdrawPlayer(name, amount).transactionSuccess() : false;
	}

	public final String format(double amount)
	{
		if (! isEnabled())
			return Double.toString(amount);

		return economy != null ? economy.format(amount) : Double.toString(amount);
	}

	public final String getGroup(Player player)
	{
		if (! isEnabled())
			return null;

		return permission != null ? permission.getPrimaryGroup(player) : null;
	}
}
