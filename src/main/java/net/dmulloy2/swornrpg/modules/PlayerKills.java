/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornrpg.modules;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * @author dmulloy2
 */

public class PlayerKills extends Module
{
	private int killerXpGain;
	private int killedXpLoss;

	public PlayerKills(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("levelingMethods.playerKills.enabled", true));
		this.killerXpGain = plugin.getConfig().getInt("levelingMethods.playerKills.xpgain");
		this.killedXpLoss = plugin.getConfig().getInt("levelingMethods.playerKills.xploss");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Player killed = event.getEntity();

		// Figure out their killer
		Player killer = plugin.getKiller(killed);
		if (killer != null)
		{
			// Suicide check
			if (killed.getName().equals(killer.getName()))
				return;

			if (isFactionsApplicable(killed, false) || isFactionsApplicable(killer, false))
				return;

			if (plugin.isDisabledWorld(killed))
				return;

			// Prevent multiple deaths
			PlayerData data = plugin.getPlayerDataCache().getData(killed);
			if (System.currentTimeMillis() - data.getTimeOfLastDeath() <= 60L)
				return;

			data.setTimeOfLastDeath(System.currentTimeMillis());

			// Killer xp gain
			String message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("pvp_kill_msg"), killerXpGain, killed.getName());
			plugin.getExperienceHandler().handleXpGain(killer, killerXpGain, message);

			// Killed xp loss
			message = plugin.getPrefix() + FormatUtil.format(plugin.getMessage("pvp_death_msg"), killedXpLoss, killer.getName());
			plugin.getExperienceHandler().handleXpGain(killed, - killedXpLoss, message);
		}
	}
}