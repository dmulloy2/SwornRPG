package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.InventoryUtil;

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
		this.optionalArgs.add("remove");
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
			if (player.getItemInHand().getType() != Material.AIR)
			{
				ItemStack hand = player.getItemInHand();
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
						inv.setItemInHand(head);
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