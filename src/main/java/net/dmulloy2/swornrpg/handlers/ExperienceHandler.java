package net.dmulloy2.swornrpg.handlers;

import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.events.PlayerLevelupEvent;
import net.dmulloy2.swornrpg.events.PlayerXpGainEvent;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.InventoryUtil;
import net.dmulloy2.swornrpg.util.ItemUtil;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles the gaining of xp
 * 
 * @author dmulloy2
 */

public class ExperienceHandler
{
	private final SwornRPG plugin;
	public ExperienceHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
	}

	/**
	 * Handles the gaining of XP for {@link Player}s
	 * 
	 * @param player
	 *        - {@link Player} who gained xp
	 * @param xp
	 *        - Amount of xp gained
	 * @param message
	 *        - Message to be sent to the player
	 */
	public void onXPGain(Player player, int xp, String message)
	{
		/** Disabled World Check **/
		if (plugin.isDisabledWorld(player))
			return;

		PlayerXpGainEvent event = new PlayerXpGainEvent(player, xp, message);
		plugin.getPluginManager().callEvent(event);

		if (event.isCancelled())
			return;

		/** Send the Message **/
		if (! message.isEmpty())
			player.sendMessage(message);

		/** Add the xp gained to their overall xp **/
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		int xpgained = event.getXpGained();
		data.setPlayerxp(data.getPlayerxp() + xpgained);
		data.setTotalxp(data.getTotalxp() + xpgained);

		/** Levelup check **/
		int currentXp = data.getPlayerxp();
		int xpneeded = data.getXpNeeded();
		int newlevel = xp / xpneeded;
		int oldlevel = data.getLevel();

		if ((currentXp - xpneeded) >= 0)
		{
			/** If so, level up **/
			onLevelup(player, oldlevel, newlevel);
		}
	}

	/**
	 * Handles the leveling up of {@link Player}s
	 * 
	 * @param player
	 *        - {@link Player} who leveled up
	 * @param oldLevel
	 *        - Old level
	 * @param newLevel
	 *        - New level
	 */
	public void onLevelup(Player player, int oldLevel, int newLevel)
	{
		/** Disabled World Check **/
		if (plugin.isDisabledWorld(player))
			return;

		PlayerLevelupEvent event = new PlayerLevelupEvent(player, oldLevel, newLevel);
		plugin.getPluginManager().callEvent(event);

		if (event.isCancelled())
			return;

		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());

		/** Prior Skill Data **/
		int oldFrenzy = plugin.getAbilityHandler().getFrenzyDuration(data.getLevel());
		int oldSuperPickaxe = plugin.getAbilityHandler().getSuperPickaxeDuration(data.getLevel());
		int oldUnlimitedAmmo = plugin.getAbilityHandler().getUnlimitedAmmoCooldown(data.getLevel());

		/** Prepare data for the next level **/
		if (data.getLevel() < 250)
		{
			data.setLevel(data.getLevel() + 1);
			data.setXpneeded(data.getXpNeeded() + (data.getXpNeeded() / 4));
		}

		data.setPlayerxp(0);

		/** New Skill Data **/
		int newFrenzy = plugin.getAbilityHandler().getFrenzyDuration(data.getLevel());
		int newSuperPickaxe = plugin.getAbilityHandler().getSuperPickaxeDuration(data.getLevel());
		int newUnlimitedAmmo = plugin.getAbilityHandler().getUnlimitedAmmoCooldown(data.getLevel());

		/** Send messages **/
		int level = data.getLevel();
		if (level == 250)
		{
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("level_cap")));
		}
		else
		{
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup"), level));
		}

		plugin.debug(plugin.getMessage("log_levelup"), player.getName(), level);

		/** Rewards **/
		if (plugin.getConfig().getBoolean("levelingRewards.enabled"))
		{
			Economy economy = plugin.getEconomy();
			if (economy != null)
			{
				int money = level * plugin.getConfig().getInt("levelingRewards.money");

				economy.depositPlayer(player.getName(), money);

				player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_money"), economy.format(money)));
			}

			List<String> configItems = plugin.getConfig().getStringList("levelingRewards.items");
			if (! configItems.isEmpty())
			{
				for (String configItem : configItems)
				{
					ItemStack item = ItemUtil.readItem(configItem);
					if (item != null)
					{
						item.setAmount(item.getAmount() * level);

						InventoryUtil.addItems(player.getInventory(), item);

						String itemName = FormatUtil.getFriendlyName(item.getType());
						player.sendMessage(plugin.getPrefix()
								+ FormatUtil.format(plugin.getMessage("levelup_items"), item.getAmount(), itemName));
					}
				}
			}
		}

		/** Tell Players if Skill(s) went up **/
		double frenzy = newFrenzy - oldFrenzy;
		double spick = newSuperPickaxe - oldSuperPickaxe;
		double ammo = newUnlimitedAmmo - oldUnlimitedAmmo;

		player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_skills")));
		if (frenzy > 0)
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_frenzy"), frenzy));
		if (spick > 0)
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_spick"), spick));
		if (ammo > 0)
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_ammo"), ammo));
	}
}