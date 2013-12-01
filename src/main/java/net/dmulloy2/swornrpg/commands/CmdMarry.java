package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdMarry extends SwornRPGCommand
{
	public CmdMarry(SwornRPG plugin)
	{
		super(plugin);
		this.name = "marry";
		this.requiredArgs.add("player");
		this.description = "Marry another player";
		this.permission = Permission.MARRY;

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

		PlayerData data = getPlayerData(player);
		data.setSpouse(target.getName());

		PlayerData data1 = getPlayerData(target);
		data1.setSpouse(player.getName());

		sendMessageAll(plugin.getMessage("marry"), player.getName(), target.getName());
		plugin.getProposal().remove(player.getName());
		plugin.getProposal().remove(target.getName());
	}
}