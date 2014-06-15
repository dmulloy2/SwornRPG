package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */

public class CmdDivorce extends SwornRPGCommand
{
	public CmdDivorce(SwornRPG plugin)
	{
		super(plugin);
		this.name = "divorce";
		this.description = "Divorce your spouse";
		this.permission = Permission.DIVORCE;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		PlayerData data = getPlayerData(player);

		String spouse = data.getSpouse();
		if (spouse == null)
		{
			err(plugin.getMessage("not_married"));
			return;
		}

		OfflinePlayer target = Util.matchOfflinePlayer(spouse);
		if (target == null)
		{
			sendpMessage(plugin.getMessage("divorce_plaintiff"), spouse);
			data.setSpouse(null);
			return;
		}

		PlayerData targetData = getPlayerData(target);
		if (targetData == null)
		{
			sendpMessage(plugin.getMessage("divorce_plaintiff"), spouse);
			data.setSpouse(null);
			return;
		}

		plugin.getServer().broadcastMessage(FormatUtil.format(plugin.getMessage("divorce_broadcast"), player.getName(), spouse));
		targetData.setSpouse(null);
		if (target.isOnline())
		{
			sendpMessage(target.getPlayer(), plugin.getMessage("divorce_defendant"));
		}
	}
}