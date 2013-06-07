package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

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
		this.description = "Marry another player";
		this.requiredArgs.add("player");
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (plugin.marriage == false)
		{
			sendpMessage(plugin.getMessage("command_disabled"));
			return;
		}
		if (plugin.proposal.containsKey(sender.getName()))
		{
			Player target = Util.matchPlayer(plugin.proposal.get(sender.getName()));
			if (target != null)
			{
				String targetp = target.getName();
				String senderp = sender.getName();
				final PlayerData data = plugin.getPlayerDataCache().getData(senderp);
				final PlayerData data1 = plugin.getPlayerDataCache().getData(targetp);
				data.setSpouse(targetp);
				data1.setSpouse(senderp);
				sendMessageAll(plugin.getMessage("marry"), senderp, targetp);
				plugin.proposal.remove(senderp);
				plugin.proposal.remove(targetp);
			}
			else
			{
				sendpMessage(plugin.getMessage("noplayer"));
			}
		}
		else
		{
			sendpMessage(plugin.getMessage("no_proposal"));
		}
	}
}