package net.dmulloy2.swornrpg.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author dmulloy2
 */

public class PlayerXpGainEvent extends PlayerEvent implements Cancellable
{	
	private static final HandlerList handlers = new HandlerList();
	
	private boolean cancelled;
	private final int xpgained;
	private final String message;
	
	public PlayerXpGainEvent(final Player player, int xpgained, String message)
	{
		super(player);
		this.xpgained = xpgained;
		this.message = message;
	}

	@Override
	public HandlerList getHandlers() 
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList() 
	{
		return handlers;
	}
	
	public final int getXpGained()
	{
		return xpgained;
	}
	
	public final String getMessage()
	{
		return message;
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