package net.dmulloy2.swornrpg.events;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class SuperPickaxeActivateEvent extends AbilityActivateEvent
{
	private final boolean command;
	private int duration;
	
	public SuperPickaxeActivateEvent(Player player, int duration, boolean command)
	{
		super(player);
		this.command = command;
		this.duration = duration;
	}
	
	public final boolean isCommand()
	{
		return command;
	}
	
	public final int getDuration()
	{
		return duration;
	}
	
	public final void setDuration(int duration)
	{
		this.duration = duration;
	}
}