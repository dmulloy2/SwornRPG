package net.dmulloy2.swornrpg.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author dmulloy2
 */

public class PlayerLevelupEvent extends PlayerEvent implements Cancellable
{	
	private static final HandlerList handlers = new HandlerList();
	public int oldLevel;
	public int newLevel;
	
	public boolean cancelled;
	
	public PlayerLevelupEvent(final Player player, final int oldLevel, final int newLevel)
	{
		super(player);
		this.oldLevel = oldLevel;
		this.newLevel = newLevel;
	}
	
	public int getOldLevel()
	{
		return oldLevel;
	}
	
	public int getNewLevel()
	{
		return newLevel;
	}

	public HandlerList getHandlers() 
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList() 
	{
		return handlers;
	}
	
	@Override
	public boolean isCancelled() 
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) 
	{
		this.cancelled = cancelled;
	}
}
