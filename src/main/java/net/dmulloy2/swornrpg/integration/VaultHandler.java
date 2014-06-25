/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.swornrpg.integration;

import lombok.Getter;
import net.dmulloy2.handlers.IntegrationHandler;
import net.dmulloy2.swornrpg.SwornRPG;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

/**
 * @author dmulloy2
 */

public class VaultHandler extends IntegrationHandler
{
	private @Getter boolean enabled;
	private @Getter Economy economy;
	private @Getter Permission permission;
	
	private final SwornRPG plugin;
	public VaultHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.setup();
	}

	@Override
	public final void setup()
	{
		try
		{
			PluginManager pm = plugin.getPluginManager();
			if (pm.getPlugin("Vault") != null)
			{
				ServicesManager sm = plugin.getServer().getServicesManager();
				RegisteredServiceProvider<Economy> economyProvider = sm.getRegistration(Economy.class);
				if (economyProvider != null)
				{
					economy = economyProvider.getProvider();
					if (economy != null)
					{
						plugin.getLogHandler().log(plugin.getMessage("log_vault_economy"), economy.getName());
					}
				}

				RegisteredServiceProvider<Permission> permissionProvider = sm.getRegistration(Permission.class);
				if (permissionProvider != null)
				{
					permission = permissionProvider.getProvider();
					if (permission != null)
					{
						plugin.getLogHandler().log(plugin.getMessage("log_vault_permissions"), permission.getName());
					}
				}

				enabled = true;
			}
		}
		catch (Throwable ex)
		{
			enabled = false;
		}
	}

	@SuppressWarnings("deprecation") // Backwards Compatibility
	public final void depositPlayer(Player player, double amount)
	{
		if (economy != null)
		{
			try
			{
				economy.depositPlayer(player, amount);
			}
			catch (Throwable ex)
			{
				economy.depositPlayer(player.getName(), amount);
			}
		}
	}
}