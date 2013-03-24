package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

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
		if (args.length == 0)
		{
			if (sender instanceof Player)
			{
				PlayerData data = getPlayerData(player);
				data.setPlayerxp(0);
				data.setFcooldown(false);
				data.setFrenzycd(0);
				data.setScooldown(false);
				data.setSuperpickcd(0);
				data.setLevel(0);
				data.setTotalxp(0);
				data.setXpneeded(100 + (data.getPlayerxp()/4));
				sendpMessage("&eYou have reset your level");
			}
			else
			{
				sendpMessage(plugin.getMessage("mustbeplayer"));
			}
		}
		else if (args.length == 1)
		{
			Player target = Util.matchPlayer(args[0]);
			if (target != null)
			{
				String targetp = target.getName();
				PlayerData data = getPlayerData(target);
				data.setPlayerxp(0);
				data.setFcooldown(false);
				data.setFrenzycd(0);
				data.setScooldown(false);
				data.setSuperpickcd(0);
				data.setLevel(0);
				data.setTotalxp(0);
				data.setXpneeded(100 + (data.getPlayerxp()/4));
				sendpMessage("&eYou have reset " + targetp + "'s level");
			}
			else
			{
				sendpMessage(plugin.getMessage("noplayer"));
			}
		}
	}
}