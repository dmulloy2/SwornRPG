package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 * Unimplimented. Pondering removal.
 */

public class CmdHat implements CommandExecutor{
	
	public SwornRPG plugin;
	  public CmdHat(SwornRPG plugin)  {
	    this.plugin = plugin;
	  }
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
		    Player player = null;
		    if (sender instanceof Player) {
		      player = (Player) sender;
		    }
		        if((player.getItemInHand() != null) && (player.getItemInHand().getType() != (Material.AIR))){
		        	if((player.getInventory().getHelmet().getType() != null) && (player.getInventory().getHelmet().getType() != (Material.AIR))){
		        		try {
		        			ItemStack itm = player.getItemInHand();
		        			ItemStack toHead = itm.clone();
		        			toHead.setAmount(1);
		        			int type = itm.getTypeId();
		        			if (type < 256){
		        				if (itm.getAmount() > 1) {
		        					itm.setAmount(itm.getAmount() - 1);
		        					player.setItemInHand(itm);
		        				} else {
		        					player.setItemInHand(null);
		        				}
		        				player.getInventory().setHelmet(toHead);
		        			} else {
		        				player.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.RED + "Error: This is not a block!");
		        			}
		        		}
		            
		        		catch (Exception localException)
		        		{
		        		}
		        	}else{
		        		player.sendMessage(ChatColor.GOLD + "[SwornRPG]" + ChatColor.RED + " Error: " + player.getInventory().getHelmet() + ChatColor.RED + "Is already on your head.");
		        		}
		        }else{
		        	player.sendMessage(ChatColor.GOLD + "[SwornRPG]" + ChatColor.RED + " Error: You are not holding anything!");
		        }
		        	return false;
		        }
}