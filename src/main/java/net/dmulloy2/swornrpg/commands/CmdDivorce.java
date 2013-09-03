package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;

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
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		PlayerData data = getPlayerData(player);
		String targetp = data.getSpouse();
		if (targetp == null)
		{
			err(plugin.getMessage("not_married"));
			return;
		}

		PlayerData data1 = plugin.getPlayerDataCache().getData(targetp);
		data.setSpouse(null);
		data1.setSpouse(null);
		sendpMessage(plugin.getMessage("divorce_plaintiff"), targetp);
		sendMessageAll(plugin.getMessage("divorce_broadcast"), sender.getName(), targetp);
		Player target = Util.matchPlayer(targetp);
		if (target != null)
		{
			sendMessageTarget(plugin.getMessage("divorce_defendant"), target);
		}
	}
}