package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdDeny extends SwornRPGCommand
{
	public CmdDeny(SwornRPG plugin)
	{
		super(plugin);
		this.name = "deny";
		this.aliases.add("reject");
		this.description = "Deny a player's proposal";
		this.permission = Permission.DENY;

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
		
		if (! plugin.getProposal().containsKey(player.getName()))
		{
			err(plugin.getMessage("no_proposal"));
			return;
		}

		Player target = Util.matchPlayer(plugin.getProposal().get(sender.getName()));
		if (target == null)
		{
			err(plugin.getMessage("player_not_found"), plugin.getProposal().get(sender.getName()));
			return;
		}

		plugin.getProposal().remove(sender.getName());
		plugin.getProposal().remove(target.getName());

		sendpMessage(plugin.getMessage("deny_sender"), target.getName());
		sendMessageTarget(plugin.getMessage("deny_rejcted"), target, sender.getName());
	}
}