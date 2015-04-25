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
