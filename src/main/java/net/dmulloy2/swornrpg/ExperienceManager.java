package net.dmulloy2.swornrpg;

import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.events.PlayerLevelupEvent;
import net.dmulloy2.swornrpg.events.PlayerXpGainEvent;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.InventoryWorkaround;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class ExperienceManager 
{
	public SwornRPG plugin;
	public ExperienceManager(SwornRPG plugin)
	{
		this.plugin = plugin;
	}
	
	/**When a player gains XP**/
	public void onXPGain(Player player, int xp, String message)
	{
		/**Disabled World Check**/
		if (plugin.isDisabledWorld(player))
			return;
		
		PlayerXpGainEvent event = new PlayerXpGainEvent(player, xp, message);
		plugin.getPluginManager().callEvent(event);
		
		if (event.isCancelled())
			return;
		
		/**Send the Message**/
		if (message != "")
			player.sendMessage(message);

		/**Add the xp gained to their overall xp**/
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		int xpgained = event.getXpGained();
		data.setPlayerxp(data.getPlayerxp() + xpgained);
		data.setTotalxp(data.getTotalxp() + xpgained);
		
		/**Levelup check**/
		int currentXp = data.getPlayerxp();
		int xpneeded = data.getXpneeded();
		int newlevel = (xp/xpneeded);
		int oldlevel = data.getLevel();
		
		if ((currentXp - xpneeded) >= 0)
		{
			/**If so, level up**/
			onLevelup(player, oldlevel, newlevel);
		}
	}
	
	/**When a player levels up**/
	public void onLevelup(Player player, int oldLevel, int newLevel)
	{
		/**Disabled World Check**/
		if (plugin.isDisabledWorld(player))
			return;
		
		PlayerLevelupEvent event = new PlayerLevelupEvent(player, oldLevel, newLevel);
		plugin.getPluginManager().callEvent(event);
		
		if (event.isCancelled())
			return;
		
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		
		/**Prior Skill Data**/
		int oldfrenzy = (plugin.getFrenzyd() + (data.getLevel()*plugin.getFrenzym()));
		int oldspick = (plugin.getSpbaseduration() + (data.getLevel()*plugin.getSuperpickm()));
		int oldammo = (plugin.getAmmobaseduration() + (data.getLevel()*plugin.getAmmomultiplier()));
		
		/**Prepare data for the next level**/
		if (data.getLevel() < 250)
		{
			data.setLevel(data.getLevel() + 1); // set the level cap at 250, seems fair enough
			data.setXpneeded(data.getXpneeded() + (data.getXpneeded()/4));
		}
		
		data.setPlayerxp(0);
		
		/**New Skill Data**/
		int newfrenzy = (plugin.getFrenzyd() + (data.getLevel()*plugin.getFrenzym()));
		int newspick = (plugin.getSpbaseduration() + (data.getLevel()*plugin.getSuperpickm()));
		int newammo = (plugin.getAmmobaseduration() + (data.getLevel()*plugin.getAmmomultiplier()));
		
		/**Send messages**/
		int level = data.getLevel();
		if (level == 250)
		{
			player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("level_cap")));
		}
		else
		{
			player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("levelup"), level));
		}

		plugin.debug(plugin.getMessage("log_levelup"), player.getName(), level);
		
		/**Award money if enabled**/
		if (! plugin.isMoney())
		{
			/**Vault Check**/
			PluginManager pm = plugin.getServer().getPluginManager();
			if (pm.isPluginEnabled("Vault"))
			{
				Economy economy = plugin.getEconomy();
				if (economy != null)
				{
					int money = level*plugin.getBasemoney();
					economy.depositPlayer(player.getName(), money);
					
					player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("levelup_money"), economy.format(money)));
				}
			}
		}
		
		/**Award items if enabled**/
		if (! plugin.isItems())
		{
			int rewardamt = level*plugin.getItemperlevel();
			ItemStack item = new ItemStack(plugin.getItemreward(), rewardamt);
			InventoryWorkaround.addItems(player.getInventory(), item);
			
			String itemName = FormatUtil.getFriendlyName(item.getType());
			player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("levelup_items"), rewardamt, itemName));
		}
		
		/**Tell Players if Skill(s) went up**/
		double frenzy = newfrenzy - oldfrenzy;
		double spick = newspick - oldspick;
		double ammo = newammo - oldammo;
		
		player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("levelup_skills")));
		if (frenzy > 0)
			player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("levelup_frenzy"), String.valueOf(frenzy)));
		if (spick > 0)
			player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("levelup_spick"), String.valueOf(spick)));
		if (ammo > 0)
			player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("levelup_ammo"), String.valueOf(ammo)));
	}
}