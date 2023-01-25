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
import net.dmulloy2.swornapi.util.FormatUtil;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLevelChangeEvent;

/**
 * @author dmulloy2
 */

public class MinecraftXP extends Module
{
	private int xpGain;

	public MinecraftXP(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("levelingMethods.mcXpGain.enabled", true));
		this.xpGain = plugin.getConfig().getInt("levelingMethods.mcXpGain.xpgain");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLevelChange(PlayerLevelChangeEvent event)
	{
		Player player = event.getPlayer();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;

		if (isFactionsApplicable(player, true))
			return;

		if (plugin.isDisabledWorld(player) || plugin.isCamping(player))
			return;

		// Only give xp for single level changes
		int oldLevel = event.getOldLevel();
		int newLevel = event.getNewLevel();
		if (newLevel - oldLevel != 1)
			return;

		String message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("mc_xp_gain"), xpGain);
		plugin.getExperienceHandler().handleXpGain(player, xpGain, message);
	}
}
