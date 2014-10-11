package net.dmulloy2.swornrpg.handlers;

import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.integration.VaultHandler;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.types.ItemParser;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.TimeUtil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles the gaining of xp
 *
 * @author dmulloy2
 */

public class ExperienceHandler implements Reloadable
{
	private List<ItemStack> rewardItems;
	private boolean rewardsEnabled;
	private double rewardMoney;
	private int levelCap;

	private final SwornRPG plugin;
	public ExperienceHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.reload();
	}

	/**
	 * Handles xp gaining for players
	 *
	 * @param player {@link Player} who gained xp
	 * @param xpGained Amount of xp gained
	 * @param message Message to be sent to the player
	 */
	public void handleXpGain(Player player, int xpGained, String message)
	{
		/** Disabled World Check **/
		if (plugin.isDisabledWorld(player))
			return;

		/** Send the Message **/
		if (! message.isEmpty())
			player.sendMessage(message);

		/** Add the xp gained to their overall xp **/
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		data.setPlayerxp(data.getPlayerxp() + xpGained);
		data.setTotalxp(data.getTotalxp() + xpGained);

		/** Levelup check **/
		if (data.getXpNeeded() - data.getPlayerxp() <= 0)
		{
			handleLevelUp(player);
		}
	}

	/**
	 * Handles leveling up for players
	 *
	 * @param player {@link Player} to level up
	 */
	public final void handleLevelUp(Player player)
	{
		/** Disabled World Check **/
		if (plugin.isDisabledWorld(player))
			return;

		PlayerData data = plugin.getPlayerDataCache().getData(player);

		/** Prior Skill Data **/
		int oldFrenzy = TimeUtil.toSeconds(plugin.getAbilityHandler().getFrenzyDuration(data.getLevel()));
		int oldSuperPickaxe = TimeUtil.toSeconds(plugin.getAbilityHandler().getSuperPickaxeDuration(data.getLevel()));
		int oldUnlimitedAmmo = TimeUtil.toSeconds(plugin.getAbilityHandler().getUnlimitedAmmoDuration(data.getLevel()));

		/** Prepare data for the next level **/
		if (levelCap == -1 || data.getLevel() < levelCap)
		{
			data.setLevel(data.getLevel() + 1);
			data.setXpneeded(data.getXpNeeded() + (data.getXpNeeded() / 4));
		}

		data.setPlayerxp(0);

		/** New Skill Data **/
		int newFrenzy = TimeUtil.toSeconds(plugin.getAbilityHandler().getFrenzyDuration(data.getLevel()));
		int newSuperPickaxe = TimeUtil.toSeconds(plugin.getAbilityHandler().getSuperPickaxeDuration(data.getLevel()));
		int newUnlimitedAmmo = TimeUtil.toSeconds(plugin.getAbilityHandler().getUnlimitedAmmoDuration(data.getLevel()));

		/** Send messages **/
		int level = data.getLevel();
		if (levelCap != -1 && level >= levelCap)
		{
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("level_cap")));
		}
		else
		{
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup"), level));
		}

		plugin.debug(plugin.getMessage("log_levelup"), player.getName(), level);

		/** Rewards **/
		if (rewardsEnabled)
		{
			VaultHandler handler = plugin.getVaultHandler();
			if (handler.getEconomy() != null)
			{
				double money = rewardMoney * level;

				handler.depositPlayer(player, money);

				player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_money"),
						handler.getEconomy().format(money)));
			}

			if (! rewardItems.isEmpty())
			{
				for (ItemStack item : rewardItems)
				{
					item = item.clone();
					item.setAmount(item.getAmount() * level);

					InventoryUtil.giveItem(player, item);

					String itemName = FormatUtil.getFriendlyName(item.getType());
					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_items"), item.getAmount(), itemName));
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

	/**
	 * Recalculates a player's statistics based upon the current xp gaining
	 * algorithm. Currently not used and must be tweaked.
	 *
	 * @param player {@link Player} to recalculate stats for
	 */
	public final void recalculateStats(Player player)
	{
//		PlayerData data = plugin.getPlayerDataCache().getData(player);
//		int totalXp = data.getTotalxp();
//
//		totalXp *= 4;
//
//		int level = 0;
//		int xp = 0;
//		int xpNeeded = 100;
//
//		while (true)
//		{
//			level++;
//			totalXp -= xp;
//			xp += xpNeeded;
//			xpNeeded += (xpNeeded / 4);
//
//			if (totalXp <= 0)
//			{
//				break;
//			}
//		}
//
//		data.setLevel(level);
//		data.setTotalxp(totalXp);
//		data.setXpneeded(xpNeeded);
//		data.setPlayerxp(0);
	}

	@Override
	public void reload()
	{
		this.rewardItems = ItemParser.parse(plugin, plugin.getConfig().getStringList("levelingRewards.items"));
		this.rewardsEnabled = plugin.getConfig().getBoolean("levelingRewards.enabled");
		this.rewardMoney = plugin.getConfig().getDouble("levelingRewards.money");
		this.levelCap = plugin.getConfig().getInt("levelCap", -1);
	}
}