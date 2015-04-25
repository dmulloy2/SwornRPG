/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornrpg.modules;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;

/**
 * @author dmulloy2
 */

public class Enchanting extends Module
{
	private int xpGain;

	public Enchanting(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("levelingMethods.enchanting.enabled", true));
		this.xpGain = plugin.getConfig().getInt("levelingMethods.enchanting.xpgain");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerEnchant(EnchantItemEvent event)
	{
		int cost = event.getExpLevelCost();
		if (cost < 15)
			return;

		Player player = event.getEnchanter();
		if (player == null || plugin.isDisabledWorld(player))
			return;

		int xp = xpGain + (cost / 2);

		String message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("enchant_gain"), xp);
		plugin.getExperienceHandler().handleXpGain(player, xp, message);
	}
}