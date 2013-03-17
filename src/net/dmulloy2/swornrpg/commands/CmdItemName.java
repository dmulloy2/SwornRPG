package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class CmdItemName implements CommandExecutor
{
	public SwornRPG plugin;
	public CmdItemName(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		Player player = null;
		if (sender instanceof Player) 
		{
			player = (Player)sender;
			if (args.length >= 1)
			{
			    ItemStack hand = player.getItemInHand();
			    ItemMeta meta = hand.getItemMeta();
			    String name = new String();
			    for (int i = 0; i < args.length; i++) 
			    { 
			    	name = name.concat(args[i].replaceAll("&", "§") + " ");
			    }
			    meta.setDisplayName(name);
			    hand.setItemMeta(meta);
			}
			else
			{
				sender.sendMessage(plugin.invalidargs + "(/iname <name>)");
			}
		}
		
		return true;
	}
}