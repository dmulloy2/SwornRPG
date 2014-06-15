package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.Util;

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
		this.optionalArgs.add("player");
		this.description = "Deny a player's proposal";
		this.permission = Permission.DENY;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		if (! plugin.getConfig().getBoolean("marriage"))
		{
			err(getMessage("command_disabled"));
			return;
		}

		PlayerData data = getPlayerData(player);
		if (data.getProposals().isEmpty())
		{
			err(getMessage("no_proposal"));
			return;
		}

		if (args.length == 0)
		{
			for (String reject : data.getProposals())
			{
				data.getProposals().remove(reject);

				Player target = Util.matchPlayer(reject);
				if (target != null)
				{
					sendpMessage(target, getMessage("deny_rejcted"), player.getName());
				}
			}

			sendpMessage(getMessage("deny_sender"), "all");
		}
		else
		{
			String reject = args[0];
			if (! data.getProposals().contains(reject))
			{
				err(getMessage("no_proposal"));
				return;
			}

			data.getProposals().remove(reject);

			Player target = Util.matchPlayer(reject);
			if (target != null)
			{
				sendpMessage(target, getMessage("deny_rejcted"), player.getName());
			}

			sendpMessage(getMessage("deny_sender"), target.getName() + "''s proposal");
		}
	}
}