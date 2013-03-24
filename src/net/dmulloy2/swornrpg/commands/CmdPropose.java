package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

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
		PlayerData data = getPlayerData(player);
		Player target = Util.matchPlayer(args[0]);
		if (target != null)
		{
			String targetp = target.getName();
			PlayerData data1 = getPlayerData(target);
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
					sendMessageTarget("&a" + sender.getName() + " Wishes to marry you. Type &c/marry &6 " + sender.getName() + " &ato confirm", target);
				}
			}
		}
		else
		{
			sendpMessage(plugin.getMessage("noplayer"));
		}
	}
}