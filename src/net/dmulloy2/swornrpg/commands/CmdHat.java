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
import net.dmulloy2.swornrpg.util.InventoryWorkaround;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Essentials
 * @editor dmulloy2
 */

public class CmdHat implements CommandExecutor
{
		
	public SwornRPG plugin;
	public CmdHat(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		Player player = null;
		if (sender instanceof Player) 
		{
			player = (Player) sender;   
			if (args.length > 0 && (args[0].contains("rem") || args[0].contains("off") || args[0].equalsIgnoreCase("0")))
			{
				final PlayerInventory inv = player.getInventory();
				final ItemStack head = inv.getHelmet();
				if (head == null || head.getType() == Material.AIR)
				{
					player.sendMessage(plugin.prefix + ChatColor.RED + "You are not wearing a hat.");
				}
				else
				{
					final ItemStack air = new ItemStack(Material.AIR);
					inv.setHelmet(air);
					InventoryWorkaround.addItems(player.getInventory(), head);
					player.sendMessage(plugin.prefix + ChatColor.YELLOW + "Your hat has been removed");
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
							player.sendMessage(plugin.prefix + ChatColor.YELLOW + "Enjoy your new hat!");
						}
						else
						{
							hand.setAmount(1);
							inv.remove(hand);
							inv.setHelmet(hand);
							inv.setItemInHand(head);
							player.sendMessage(plugin.prefix + ChatColor.YELLOW + "Enjoy your new hat!");
						}
					}
					else
					{
						player.sendMessage(plugin.prefix + ChatColor.RED + "Error, you cannot use this item as a hat!");
					}
				}
				else
				{
					player.sendMessage(plugin.prefix + ChatColor.RED + "You must have something to wear in your hand");
				}
			}
		}
		else
		{
			sender.sendMessage(plugin.mustbeplayer);
		}
		
		return true;
	}
}