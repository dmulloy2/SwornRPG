package net.dmulloy2.swornrpg.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerXpGainEvent extends PlayerEvent implements Cancellable
{	
	private static final HandlerList handlers = new HandlerList();
	
	public boolean cancelled;
	
	public PlayerXpGainEvent(final Player player)
	{
		super(player);
	}

	/** Generic methods required **/
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
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