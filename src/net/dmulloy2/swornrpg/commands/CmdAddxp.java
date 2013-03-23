package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdAddxp extends SwornRPGCommand
{
	public CmdAddxp (SwornRPG plugin)
	{
		super(plugin);
		this.name = "addxp";
		this.aliases.add("givexp");
		this.requiredArgs.add("player");
		this.requiredArgs.add("xp");
		this.description = "Manually give xp to a player";
		this.permission = PermissionType.CMD_ADDXP.permission;
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		Player target = Util.matchPlayer(args[0]);
		if (target == null)
			return;
		PlayerData data = getPlayerData(target);
		int xptoadd = Integer.parseInt(args[1]);
		data.setPlayerxp(data.getPlayerxp() + xptoadd);
		sendpMessage("&eYou have given &a" + xptoadd + " &exp to &a" + target.getName());
		sendMessageTarget("&eYou have been given &a" + xptoadd + " &exp", target);
	}
}