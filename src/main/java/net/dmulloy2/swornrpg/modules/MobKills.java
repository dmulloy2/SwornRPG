/**
 * (c) 2015 dmulloy2
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