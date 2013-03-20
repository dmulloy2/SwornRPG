package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdPropose extends SwornRPGCommand
{
	public CmdPropose (SwornRPG plugin)
	{
		super(plugin);
		this.name = "propose";
		this.aliases.add("engage");
		this.description = "Propose marriage to a player";
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		final PlayerData data = plugin.getPlayerDataCache().getData(sender.getName());
		Player target = Bukkit.getPlayer(args[0]);
		if (target != null)
		{
			String targetp = target.getName();
			final PlayerData data1 = plugin.getPlayerDataCache().getData(targetp);
			if (args.length == 1)
			{
				if (data.getSpouse() != null)
				{
					sendpMessage("&cPolygamy is not allowed!");
				}
				else if (targetp.equals(sender.getName()))
				{
					sendpMessage("&cYou cannot marry yourself");
				}
				else if (data1.getSpouse() != null)
				{
					sendpMessage("&c" + targetp + " is already married");
				}
				else
				{
					plugin.proposal.put(targetp, sender.getName());
					sendpMessage("&aYou have proposed to " + targetp);
					target.sendMessage(plugin.prefix + ChatColor.GREEN + sender.getName() + " Wishes to marry you. Type " + ChatColor.RED + "/marry " + ChatColor.GOLD + sender.getName() + ChatColor.GREEN + " to confirm");
				}
			}
		}
		else
		{
			sendMessage(plugin.noplayer);
		}
	}
}