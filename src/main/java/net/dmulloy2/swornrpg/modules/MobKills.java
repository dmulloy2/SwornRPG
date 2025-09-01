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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornrpg.SwornRPG;

/**
 * @author dmulloy2
 */

public class MobKills extends Module
{
	private int xpGain;
	private Map<Integer, List<String>> tiers;

	public MobKills(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		this.xpGain = plugin.getConfig().getInt("levelingMethods.mobKills.xpgain", 5);
		setEnabled(xpGain != 0 && plugin.getConfig().getBoolean("levelingMethods.mobKills.enabled", true));

		this.tiers = new HashMap<>();
		
		if (plugin.getConfig().isSet("mobTiers"))
		{
			Set<String> keys = plugin.getConfig().getConfigurationSection("mobTiers").getKeys(false);
			for (String key : keys)
			{
				try
				{
					int tier = Integer.parseInt(key);
					List<String> names = plugin.getConfig().getStringList("mobTiers." + tier);
					tiers.put(tier, names);
				}
				catch (NumberFormatException ex)
				{
					plugin.getLogHandler().log(Level.WARNING, "\"{0}\" is not a number in mobTiers");
				}
			}
		}
		else
		{
			tiers.put(3, List.of("wither", "ender dragon", "elder guardian"));
			tiers.put(2, List.of("creeper", "enderman", "iron golem", "skeleton", "blaze", "zombie",
					"spider", "ghast", "magma cube", "witch", "guardian", "shulker"));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event)
	{
		LivingEntity died = event.getEntity();
		if (died instanceof Player)
			return;

		Player killer = died.getKiller();
		if (killer == null)
			return;

		// Applicability checks
		if (isFactionsApplicable(killer, true) || plugin.isDisabledWorld(killer) || plugin.isCamping(killer))
			return;

		// Determine the correct tier
		String mobName = died.getType().toString().replace("_", " ").toLowerCase();

		int multiplier = 1;
		for (Entry<Integer, List<String>> entry : tiers.entrySet())
		{
			if (entry.getValue().contains(mobName))
				multiplier = entry.getKey();
		}

		int xp = xpGain * multiplier;

		String article = FormatUtil.getArticle(mobName);
		String message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("mob_kill"), xp, article, FormatUtil.capitalize(mobName));
		plugin.getExperienceHandler().handleXpGain(killer, xp, message);
	}
}
