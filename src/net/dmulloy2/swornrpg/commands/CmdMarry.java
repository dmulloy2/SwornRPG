package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdMarry extends SwornRPGCommand
{
	public CmdMarry (SwornRPG plugin)
	{
		super(plugin);
		this.name = "marry";
		this.aliases.add("matrimony");
		this.description = "Marry a player";
		this.requiredArgs.add("player");
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (plugin.proposal.containsKey(sender.getName()))
		{
			Player target = Bukkit.getServer().getPlayer((String)plugin.proposal.get(sender.getName()));
			if (target != null)
			{
				String targetp = target.getName();
				String senderp = sender.getName();
				final PlayerData data = plugin.getPlayerDataCache().getData(senderp);
				final PlayerData data1 = plugin.getPlayerDataCache().getData(targetp);
				data.setSpouse(targetp);
				data1.setSpouse(senderp);
				Bukkit.getServer().broadcastMessage(plugin.prefix + ChatColor.GREEN + targetp + " has married " + senderp);
				plugin.proposal.remove(senderp);
				plugin.proposal.remove(targetp);
			}
			else
			{
				sendMessage(plugin.noplayer);
			}
		}
		else
		{
			sendpMessage("&cYou do not have a proposal");
		}
	}
}