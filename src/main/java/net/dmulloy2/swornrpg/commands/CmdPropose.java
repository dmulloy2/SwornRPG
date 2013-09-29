package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdPropose extends SwornRPGCommand
{
	public CmdPropose(SwornRPG plugin)
	{
		super(plugin);
		this.name = "propose";
		this.aliases.add("engage");
		this.requiredArgs.add("player");
		this.description = "Propose marriage to a player";
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (! plugin.getConfig().getBoolean("marriage"))
		{
			err(plugin.getMessage("command_disabled"));
			return;
		}
		
		PlayerData data = getPlayerData(player);
		Player target = Util.matchPlayer(args[0]);
		if (target == null)
		{
			err(plugin.getMessage("noplayer"));
			return;
		}
		
		String targetp = target.getName();
		PlayerData data1 = getPlayerData(target);
		if (data.getSpouse() != null)
		{
			err(plugin.getMessage("polygamy"));
		}
		else if (targetp.equals(sender.getName()))
		{
			err(plugin.getMessage("cannot_marry_self"));
		}
		else if (data1.getSpouse() != null)
		{
			err(plugin.getMessage("target_is_married"), targetp);
		}
		else
		{
			plugin.getProposal().put(targetp, sender.getName());
			sendpMessage(plugin.getMessage("you_have_proposed"), targetp);
			sendMessageTarget(plugin.getMessage("send_marriage_request"), target, sender.getName());
		}
	}
}