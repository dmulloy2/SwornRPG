/**
 * (c) 2015 dmulloy2
 */
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
	private String serverAccount;
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
	public final void handleXpGain(Player player, int xpGained, String message)
	{
		// Send the message
		if (! message.isEmpty())
			player.sendMessage(message);

		// Add gained xp to their total xp
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		data.setPlayerxp(data.getPlayerxp() + xpGained);
		data.setTotalxp(data.getTotalxp() + xpGained);

		// Levelup check
		if (data.getXpNeeded() - data.getPlayerxp() <= 0)
			handleLevelUp(player);
	}

	/**
	 * Handles leveling up for players
	 *
	 * @param player {@link Player} to level up
	 */
	public final void handleLevelUp(Player player)
	{
		PlayerData data = plugin.getPlayerDataCache().getData(player);

		// Old skill data
		int oldFrenzy = TimeUtil.toSeconds(plugin.getAbilityHandler().getFrenzyDuration(data.getLevel()));
		int oldSuperPickaxe = TimeUtil.toSeconds(plugin.getAbilityHandler().getSuperPickaxeDuration(data.getLevel()));
		int oldUnlimitedAmmo = TimeUtil.toSeconds(plugin.getAbilityHandler().getUnlimitedAmmoDuration(data.getLevel()));

		// Increment level, reset needed xp
		if (levelCap == -1 || data.getLevel() < levelCap)
		{
			data.setLevel(data.getLevel() + 1);
			data.setXpneeded(data.getXpNeeded() + (data.getXpNeeded() / 4));
		}

		data.setPlayerxp(0);

		// New skill data
		int newFrenzy = TimeUtil.toSeconds(plugin.getAbilityHandler().getFrenzyDuration(data.getLevel()));
		int newSuperPickaxe = TimeUtil.toSeconds(plugin.getAbilityHandler().getSuperPickaxeDuration(data.getLevel()));
		int newUnlimitedAmmo = TimeUtil.toSeconds(plugin.getAbilityHandler().getUnlimitedAmmoDuration(data.getLevel()));

		// Send levelup message
		int level = data.getLevel();
		if (levelCap != -1 && level >= levelCap)
		{
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("level_cap")));
		}
		else
		{
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup"), level));
		}

		// plugin.debug(plugin.getMessage("log_levelup"), player.getName(), level);

		// Rewards
		if (rewardsEnabled)
		{
			VaultHandler handler = plugin.getVaultHandler();
			if (handler.isEnabled())
			{
				giveLevelupCash(player, level);
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

		// Tell players which skills went up
		int frenzy = newFrenzy - oldFrenzy;
		int superPickaxe = newSuperPickaxe - oldSuperPickaxe;
		int unlimitedAmmo = newUnlimitedAmmo - oldUnlimitedAmmo;

		player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_skills")));
		if (frenzy > 0)
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_frenzy"), frenzy));
		if (superPickaxe > 0)
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_spick"), superPickaxe));
		if (unlimitedAmmo > 0)
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_ammo"), unlimitedAmmo));
	}

	private final void giveLevelupCash(Player player, int level)
	{
		if (! plugin.isVaultEnabled())
			return;

		double money = rewardMoney * level;

		VaultHandler handler = plugin.getVaultHandler();
		if (! serverAccount.isEmpty())
		{
			if (handler.hasAccount(serverAccount))
			{
				if (handler.has(serverAccount, money))
				{
					handler.withdraw(serverAccount, money);
					plugin.getLogHandler().log("{0} withdrawn from account {1} for {2}.", money, serverAccount, player.getName());
				}
				else
				{
					player.sendMessage(FormatUtil.format("&cError: &4The server account does not have enough money!"));
					return;
				}
			}
		}

		handler.depositPlayer(player, money);
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("levelup_money"), handler.format(money)));
	}

	/**
	 * Recalculates a player's statistics based upon the current xp gaining
	 * algorithm. Currently not used and must be tweaked.
	 *
	 * @param player {@link Player} to recalculate stats for
	 */
	public final void recalculateStats(Player player)
	{
		/* PlayerData data = plugin.getPlayerDataCache().getData(player);
		int totalXp = data.getTotalxp();

		totalXp *= 4;

		int level = 0;
		int xp = 0;
		int xpNeeded = 100;

		while (true)
		{
			level++;
			totalXp -= xp;
			xp += xpNeeded;
			xpNeeded += (xpNeeded / 4);

			if (totalXp <= 0)
			{
				break;
			}
		}

		data.setLevel(level);
		data.setTotalxp(totalXp);
		data.setXpneeded(xpNeeded);
		data.setPlayerxp(0); */
	}

	@Override
	public void reload()
	{
		this.rewardItems = ItemParser.parse(plugin, plugin.getConfig().getStringList("levelingRewards.items"));
		this.serverAccount = plugin.getConfig().getString("levelingRewards.serverAccount", "");
		this.rewardsEnabled = plugin.getConfig().getBoolean("levelingRewards.enabled");
		this.rewardMoney = plugin.getConfig().getDouble("levelingRewards.money");
		this.levelCap = plugin.getConfig().getInt("levelCap", -1);
	}
}