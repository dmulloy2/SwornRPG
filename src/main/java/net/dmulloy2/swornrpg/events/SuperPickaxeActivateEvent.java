package net.dmulloy2.swornrpg.events;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class SuperPickaxeActivateEvent extends AbilityActivateEvent
{
	public boolean command;
	public int duration;
	
	public SuperPickaxeActivateEvent(Player player, int duration, boolean command)
	{
		super(player);
		this.command = command;
		this.duration = duration;
	}
	
	public boolean isCommand()
	{
		return command;
	}
	
	public int getDuration()
	{
		return duration;
	}
}