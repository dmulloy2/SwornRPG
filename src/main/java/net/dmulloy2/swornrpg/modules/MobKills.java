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

import java.util.Arrays;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * @author dmulloy2
 */

public class MobKills extends Module
{
	private int xpGain;

	// TODO: Move this to configuration
	private static final List<String> tier3 = Arrays.asList(
			"wither", "ender dragon");
	private static final List<String> tier2 = Arrays.asList(
			"creeper", "enderman", "iron golem", "skeleton", "blaze", "zombie", "spider", "ghast", "magma cube", "witch", "slime");

	public MobKills(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("levelingMethods.mobKills.enabled", true));
		this.xpGain = plugin.getConfig().getInt("levelingMethods.mobKills.xpgain");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event)
	{
		Entity died = event.getEntity();
		if (died instanceof LivingEntity && ! (died instanceof Player))
		{
			Player killer = ((LivingEntity) died).getKiller();
			if (killer != null)
			{
				if (plugin.isSwornNationsEnabled() && plugin.getSwornNationsHandler().isApplicable(killer, true))
					return;

				if (plugin.isDisabledWorld(killer) || plugin.isCamping(killer))
					return;

				String mobName = FormatUtil.getFriendlyName(event.getEntity().getType());

				// Determine tier
				int xp = xpGain;
				if (tier3.contains(mobName.toLowerCase()))
					xp *= 3;
				else if (tier2.contains(mobName.toLowerCase()))
					xp *= 2;

				String article = FormatUtil.getArticle(mobName);
				String message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("mob_kill"), xp, article, mobName);
				plugin.getExperienceHandler().handleXpGain(killer, xp, message);
			}
		}
	}
}
