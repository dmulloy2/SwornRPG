package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdLevelr extends SwornRPGCommand
{
	public CmdLevelr(SwornRPG plugin)
	{
		super(plugin);
		this.name = "levelr";
		this.description = "Reset a player's level";
		this.aliases.add("levelreset");
		this.optionalArgs.add("player");
		this.permission = Permission.CMD_LEVEL_RESET;
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
					err(plugin.getMessage("noplayer"));
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
				err(plugin.getMessage("console_level"));
				return;
			}
		}
		
		PlayerData data = getPlayerData(target);
		if (data == null)
		{
			err(plugin.getMessage("noplayer"));
			return;
		}
		
		String targetp = target.getName();
		data.setPlayerxp(0);
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