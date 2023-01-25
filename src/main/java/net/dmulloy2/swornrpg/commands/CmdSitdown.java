/**
 * SwornRPG - a Bukkit plugin
 * Copyright (C) 2013 - 2015 dmulloy2
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
package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornapi.util.FormatUtil;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;

/**
 * @author dmulloy2
 */

public class CmdSitdown extends SwornRPGCommand
{
	public CmdSitdown(SwornRPG plugin)
	{
		super(plugin);
		this.name = "sitdown";
		this.aliases.add("sit");
		this.description = "Sit in a chair";
		this.permission = Permission.SITDOWN;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		Block block = player.getTargetBlock(null, 10);
		if (block == null)
		{
			err(getMessage("chair_no_block"));
			return;
		}

		String seat = FormatUtil.getFriendlyName(block.getType());
		if (! seat.contains("Step") && ! seat.contains("Stair"))
		{
			err(getMessage("chair_not_chair"));
			return;
		}

		PlayerData data = getPlayerData(player);
		data.setPreviousLocation(player.getLocation().clone());
		data.setSatRecently(true);

		Location location = block.getLocation().clone();
		location.add(0.5D, 0.0D, 0.5D);
		location.setPitch(0F);
		location.setYaw(0F);

		Arrow it = (Arrow) player.getWorld().spawnEntity(location, EntityType.ARROW);
		if (! it.addPassenger(player))
		{
			it.remove();
			data.setPreviousLocation(null);
			err(getMessage("chair_error"));
			return;
		}

		sendpMessage(getMessage("chair_now_sitting"), seat);
		sendpMessage(getMessage("chair_standup"), new CmdStandup(plugin).getUsageTemplate(false));
	}
}
