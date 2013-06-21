package net.dmulloy2.swornrpg.events;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class UnlimitedAmmoActivateEvent extends AbilityActivateEvent
{
	public int duration;
	
	public UnlimitedAmmoActivateEvent(Player player, int duration)
	{
		super(player);
		this.duration = duration;
	}
	
	public int getDuration()
	{
		return duration;
	}
}