/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornrpg.modules;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.util.FormatUtil;

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