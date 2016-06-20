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

import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ListUtil;

/**
 * @author dmulloy2
 */

public class MobKills extends Module
{
	private int xpGain;
	private Map<Integer, List<String>> tiers = new HashMap<>();

	public MobKills(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("levelingMethods.mobKills.enabled", true));
		this.xpGain = plugin.getConfig().getInt("levelingMethods.mobKills.xpgain", 5);

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
			tiers.put(3, ListUtil.toList("wither", "ender dragon", "elder guardian"));
			tiers.put(2, ListUtil.toList("creeper", "enderman", "iron golem", "skeleton", "blaze", "zombie", "spider",
					"ghast", "magma cube", "witch", "guardian", "shulker"));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event)
	{
		Entity died = event.getEntity();
		if (died instanceof LivingEntity && ! (died instanceof Player)) // Players are handled separately
		{
			Player killer = ((LivingEntity) died).getKiller();
			if (killer != null)
			{
				// Applicability checks
				if (isFactionsApplicable(killer, true) || plugin.isDisabledWorld(killer) || plugin.isCamping(killer))
					return;

				// Determine the correct tier
				String mobName = FormatUtil.getFriendlyName(died.getType());
				if (isElder(died)) mobName = "elder " + mobName;

				int multiplier = 1;
				for (Entry<Integer, List<String>> entry : tiers.entrySet())
				{
					if (entry.getValue().contains(mobName))
						multiplier = entry.getKey();
				}

				int xp = xpGain * multiplier;
				if (xp == 0) return;

				String article = FormatUtil.getArticle(mobName);
				String message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("mob_kill"), xp, article, mobName);
				plugin.getExperienceHandler().handleXpGain(killer, xp, message);
			}
		}
	}

	/**
	 * Whether or not an Entity is in an elevated state. Currently this only
	 * checks for elder guardians
	 * 
	 * @param entity Entity to check
	 * @return True if it is, false if not
	 */
	private boolean isElder(Entity entity)
	{
		if (entity instanceof Guardian)
		{
			return ((Guardian) entity).isElder();
		}

		return false;
	}
}
