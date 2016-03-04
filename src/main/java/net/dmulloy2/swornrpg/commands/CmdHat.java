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
import net.dmulloy2.util.InventoryUtil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author dmulloy2
 */

public class CmdHat extends SwornRPGCommand
{
	public CmdHat(SwornRPG plugin)
	{
		super(plugin);
		this.name = "hat";
		this.addOptionalArg("remove");
		this.description = "Put the block in your hand on your head!";
		this.permission = Permission.HAT;

		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (args.length > 0 && args[0].equalsIgnoreCase("remove"))
		{
			PlayerInventory inv = player.getInventory();
			ItemStack head = inv.getHelmet();
			if (head == null || head.getType() == Material.AIR)
			{
				sendpMessage(plugin.getMessage("no_hat"));
			}
			else
			{
				ItemStack air = new ItemStack(Material.AIR);
				inv.setHelmet(air);
				InventoryUtil.giveItem(player, head);
				sendpMessage(plugin.getMessage("hat_removed"));
			}
		}
		else
		{
			ItemStack hand = player.getInventory().getItemInMainHand();
			if (hand.getType() != Material.AIR)
			{
				if (hand.getType().getMaxDurability() == 0)
				{
					PlayerInventory inv = player.getInventory();
					ItemStack head = inv.getHelmet();
					ItemStack toHead = hand.clone();
					toHead.setAmount(1);
					if (hand.getAmount() > 1)
					{
						hand.setAmount(hand.getAmount() - 1);
						inv.setHelmet(toHead);
						if (head != null)
							InventoryUtil.giveItem(player, head);
						sendpMessage(plugin.getMessage("hat_success"));
					}
					else
					{
						hand.setAmount(1);
						inv.remove(hand);
						inv.setHelmet(hand);
						inv.setItemInMainHand(head);
						sendpMessage(plugin.getMessage("hat_success"));
					}
				}
				else
				{
					err(plugin.getMessage("hat_failure"));
				}
			}
			else
			{
				err(plugin.getMessage("hand_empty"));
			}
		}
	}
}
