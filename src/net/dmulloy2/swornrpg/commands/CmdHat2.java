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

public class CmdHat2 implements CommandExecutor{
	
	public SwornRPG plugin;
	  public CmdHat2(SwornRPG plugin)  {
	    this.plugin = plugin;
	  }
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
		    Player player = null;
		    if (sender instanceof Player) {
		      player = (Player) sender;
		    }
			if (args.length > 0 && (args[0].contains("rem") || args[0].contains("off") || args[0].equalsIgnoreCase("0")))
				{
				final PlayerInventory inv = player.getInventory();
				final ItemStack head = inv.getHelmet();
				if (head == null || head.getType() == Material.AIR)
				{
					player.sendMessage(ChatColor.RED + "You are not wearing a hat.");
				}
				else
				{
					final ItemStack air = new ItemStack(Material.AIR);
					inv.setHelmet(air);
					InventoryWorkaround.addItems(player.getInventory(), head);
					player.sendMessage(ChatColor.GOLD + "Your hat has been removed");
				}
				}
				else
				{
					if (player.getItemInHand().getType() != Material.AIR)
					{
						final ItemStack hand = player.getItemInHand().clone();
						if (hand.getType().getMaxDurability() == 0)
						{
							final PlayerInventory inv = player.getInventory();
							final ItemStack head = inv.getHelmet();
							hand.setAmount(1);
							inv.remove(hand);
							inv.setHelmet(hand);
							inv.setItemInHand(head);
							player.sendMessage(ChatColor.GOLD + "Enjoy your new hat!");
						}
						else
						{
							player.sendMessage(ChatColor.RED + "Error, you cannot use this item as a hat!");
						}
					}
					else
					{
						player.sendMessage(ChatColor.RED + "You must have something to wear in your hand");
					}
				}
			return false;
	  }	
}

//hatArmor=\u00a74Error, you cannot use this item as a hat!
//hatEmpty=\u00a74You are not wearing a hat.
//hatFail=\u00a74You must have something to wear in your hand.
//hatPlaced=\u00a76Enjoy your new hat!
//hatRemoved=\u00a76Your hat has been removed.