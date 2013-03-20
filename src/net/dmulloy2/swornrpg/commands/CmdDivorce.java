package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdDivorce extends SwornRPGCommand
{
	public CmdDivorce (SwornRPG plugin)
	{
		super(plugin);
		this.name = "divorce";
		this.description = "Divorce your spouse";
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		PlayerData data = getPlayerData(player);
		String targetp = data.getSpouse();
		if (targetp != null)
		{
			PlayerData data1 = plugin.getPlayerDataCache().getData(targetp);
			data.setSpouse(null);
			data1.setSpouse(null);
			sendpMessage("&cYou have divorced " + targetp);
			Bukkit.getServer().broadcastMessage(plugin.prefix + ChatColor.RED + sender.getName() + " has divorced " + targetp);
			Player target = Util.matchPlayer(targetp);
			if (target != null)
			{
				target.sendMessage(plugin.prefix + ChatColor.RED + "You are now single");
			}
		}
		else
		{
			sendpMessage("&cError, you are not married");
		}
	}
}