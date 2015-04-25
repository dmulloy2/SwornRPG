/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornrpg.modules;

import lombok.Getter;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

@Getter
public class OnlineTime extends TickableModule
{
	private int xpGain;
	private int interval;
	private boolean async;

	public OnlineTime(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("levelingMethods.onlineTime.enabled", false));
		this.xpGain = plugin.getConfig().getInt("levelingMethods.onlineTime.xpgain");
		this.interval = plugin.getConfig().getInt("levelingMethods.onlineTime.interval", 60) * 20;
		this.async = plugin.getConfig().getBoolean("levelingMethods.onlineTime.async", false);
	}


	@Override
	public void run()
	{
		for (Player player : Util.getOnlinePlayers())
		{
			plugin.getExperienceHandler().handleXpGain(player, xpGain, "");
		}
	}
}