package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 * Unimplemented, functionality coming soon
 */

public class CmdMine implements CommandExecutor
{	
	public SwornRPG plugin;
	public CmdMine(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = null;
		if (sender instanceof Player)
		{
			player = (Player)sender;
			if (player.getItemInHand() != null)
			{
				/**
				String inhand = player.getItemInHand().toString().toLowerCase().replaceAll("_", " ");
				if (inhand.contains("pickaxe")&&!inhand.contains("wood")&&!inhand.contains("gold"));
				{
					sender.sendMessage(plugin.prefix + ChatColor.GREEN + "Ready to mine?");
					sender.sendMessage(plugin.prefix + ChatColor.GREEN + "Your " + inhand + " has become a super pickaxe!");
				}
				*/
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "This command has not been implemented yet");
			}
			else
			{
				sender.sendMessage(plugin.prefix + ChatColor.RED + "You must have a pickaxe to use this command!");
			}
		}
		else
		{
			sender.sendMessage(plugin.mustbeplayer);
		}
		
		return true;
	}
}