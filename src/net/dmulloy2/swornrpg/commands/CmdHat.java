/**
* Essentials - a bukkit plugin
* Copyright (C) 2011 Essentials Team
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.InventoryWorkaround;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Essentials
 * @editor dmulloy2
 */

public class CmdHat extends SwornRPGCommand
{
	public CmdHat (SwornRPG plugin)
	{
		super(plugin);
		this.name = "hat";
		this.aliases.add("headgear");
		this.description = "Put the block in your hand on your head!";
		this.optionalArgs.add("remove");
		this.permission = PermissionType.CMD_HAT.permission;
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (args.length > 0 && (args[0].contains("rem") || args[0].contains("off") || args[0].equalsIgnoreCase("0")))
		{
			final PlayerInventory inv = player.getInventory();
			final ItemStack head = inv.getHelmet();
			if (head == null || head.getType() == Material.AIR)
			{
				sendpMessage(plugin.getMessage("no_hat"));
			}
			else
			{
				final ItemStack air = new ItemStack(Material.AIR);
				inv.setHelmet(air);
				InventoryWorkaround.addItems(player.getInventory(), head);
				sendpMessage(plugin.getMessage("hat_removed"));
			}
		}
		else
		{
			if (player.getItemInHand().getType() != Material.AIR)
			{
				final ItemStack hand = player.getItemInHand();
				if (hand.getType().getMaxDurability() == 0)
				{
					final PlayerInventory inv = player.getInventory();
					final ItemStack head = inv.getHelmet();
					ItemStack itm = player.getItemInHand();
					ItemStack toHead = itm.clone();
					toHead.setAmount(1);
					if (hand.getAmount() > 1)
					{
						hand.setAmount(hand.getAmount() - 1);
						inv.setHelmet(toHead);
						InventoryWorkaround.addItems(player.getInventory(), head);
						sendpMessage(plugin.getMessage("hat_success"));
					}
					else
					{
						hand.setAmount(1);
						inv.remove(hand);
						inv.setHelmet(hand);
						inv.setItemInHand(head);
						sendpMessage(plugin.getMessage("hat_success"));
					}
				}
				else
				{
					sendpMessage(plugin.getMessage("hat_failure"));
				}
			}
			else
			{
				sendpMessage(plugin.getMessage("hand_empty"));
			}
		}
	}
}