package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

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
		this.addRequiredArg("player");
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

		PlayerData data = getPlayerData(player);
		if (! data.getProposals().contains(args[0]))
		{
			err("&c{0} &4hasn''t proposed!", args[0]);
			return;
		}

		Player target = Util.matchPlayer(args[0]);
		if (target == null)
		{
			err(getMessage("player_not_found"), args[0]);
			return;
		}

		data.setSpouse(target.getName());

		PlayerData data1 = getPlayerData(target);
		data1.setSpouse(player.getName());

		plugin.getServer().broadcastMessage(FormatUtil.format(getMessage("marry"), player.getName(), target.getName()));
		data.getProposals().remove(target.getName());
	}
}