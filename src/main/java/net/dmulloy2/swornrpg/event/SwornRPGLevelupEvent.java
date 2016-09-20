/**
 * SwornRPG - a Bukkit plugin
 * Copyright (C) 2016 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.swornrpg.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import lombok.AllArgsConstructor;

/**
 * Called when a player levels up in SwornRPG
 * @author dmulloy2
 */
@AllArgsConstructor
public class SwornRPGLevelupEvent extends SwornRPGEvent
{
	private static final HandlerList HANDLERS = new HandlerList();

	private final Player player;
	private final int oldLevel;
	private final int newLevel;

	/**
	 * Gets the player that leveled up
	 * @return The player
	 */
	public Player getPlayer()
	{
		return player;
	}

	/**
	 * Gets the player's previous level
	 * @return The previous level
	 */
	public int getOldLevel()
	{
		return oldLevel;
	}

	/**
	 * Gets the player's new level
	 * @return The new level
	 */
	public int getNewLevel()
	{
		return newLevel;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}
}