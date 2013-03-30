package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdLevelr extends SwornRPGCommand
{
	public CmdLevelr (SwornRPG plugin)
	{
		super(plugin);
		this.name = "levelr";
		this.description = "Reset a player's level";
		this.optionalArgs.add("player");
		this.permission = PermissionType.CMD_LEVELR.permission;
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		OfflinePlayer target = null;
		if (args.length == 1)
		{
			target = Util.matchPlayer(args[0]);
			if (target == null)
			{
				target = Util.matchOfflinePlayer(args[0]);
				if (target == null)
				{
					sendpMessage(plugin.getMessage("noplayer"));
					return;
				}
			}
		}
		else
		{
			if (sender instanceof Player)
			{
				target = (Player)sender;
			}
			else
			{
				sendpMessage(plugin.getMessage("console_level"));
				return;
			}
		}
		PlayerData data = getPlayerData(target);
		if (data == null)
		{
			sendpMessage(plugin.getMessage("noplayer"));
			return;
		}
		String targetp = target.getName();
		data.setPlayerxp(0);
		data.setFcooldown(false);
		data.setFrenzycd(0);
		data.setScooldown(false);
		data.setSuperpickcd(0);
		data.setLevel(0);
		data.setTotalxp(0);
		data.setXpneeded(100 + (data.getPlayerxp()/4));
		sendpMessage(plugin.getMessage("level_reset_resetter"), targetp);
		if (target.isOnline())
		{
			Player targetplayer = Util.matchPlayer(targetp);
			sendMessageTarget(plugin.getMessage("level_reset_reset"), targetplayer);
		}
	}
}