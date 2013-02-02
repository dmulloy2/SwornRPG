package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 * I'm considering removing this (essentials has a better version)
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
		        if (label.equalsIgnoreCase("hat"))
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
			return false;
	  }
}