/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.swornrpg.handlers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import lombok.Getter;
import net.dmulloy2.swornrpg.SwornRPG;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * @author dmulloy2
 */

public class VaultHandler
{
	private @Getter Economy economy;
	private @Getter Permission permission;
	
	private final SwornRPG plugin;
	public VaultHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.setup();
	}

	private final void setup()
	{
		if (plugin.getServer().getPluginManager().getPlugin("Vault") != null)
		{
			RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null)
			{
				economy = economyProvider.getProvider();
				if (economy != null)
				{
					plugin.getLogHandler().log(plugin.getMessage("log_vault_economy"), economy.getName());
				}
			}

			RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
			if (permissionProvider != null)
			{
				permission = permissionProvider.getProvider();
				if (permission != null)
				{
					plugin.getLogHandler().log(plugin.getMessage("log_vault_permissions"), permission.getName());
				}
			}
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