package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdResetLevel extends SwornRPGCommand
{
	public CmdResetLevel(SwornRPG plugin)
	{
		super(plugin);
		this.name = "resetlevel";
		this.aliases.add("levelr");
		this.optionalArgs.add("player");
		this.description = "Reset a player's level";
		this.permission = Permission.LEVEL_RESET;

		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		OfflinePlayer target = getTarget(0, true);
		if (target == null)
			return;

		PlayerData data = getPlayerData(target);
		data.setPlayerxp(0);
		data.setLevel(0);
		data.setTotalxp(0);
		data.setXpneeded(100 + (data.getPlayerxp() / 4));

		sendpMessage(getMessage("level_reset_resetter"), target.getName());
		if (target.isOnline())
		{
			sendMessageTarget(getMessage("level_reset_reset"), (Player) target);
		}
	}
}