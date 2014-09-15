package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */

public class CmdLevel extends SwornRPGCommand
{
	public CmdLevel (SwornRPG plugin)
	{
		super(plugin);
		this.name = "level";
		this.description = "Check a player's level";
		this.addOptionalArg("player");
		this.permission = Permission.LEVEL;
	}
	
	@Override
	public void perform()
	{
		OfflinePlayer target = getTarget(0, hasPermission(Permission.LEVEL_OTHERS));
		if (target == null)
			return;
		
		PlayerData data = getPlayerData(target);
		if (data == null)
		{
			err(plugin.getMessage("player_not_found"), args[0]);
			return;
		}

		int level = data.getLevel();
		int nextlevel = level+1;
		int totalxp = data.getTotalxp();
		int xptonext = data.getXpNeeded() - data.getPlayerxp();

		String name, title;
		String senderp = sender.getName();
		String targetp = target.getName();

		if (targetp.equals(senderp))
		{
			name = "You are";
			title = senderp;
		}
		else
		{
			name = targetp + " is";
			title = targetp;
		}

		sendMessage(plugin.getMessage("level_header"), title);
		sendMessage(plugin.getMessage("level_info"), name, level, totalxp);
		sendMessage(plugin.getMessage("level_xptonext"), name, xptonext, nextlevel);

		StringBuilder bar = new StringBuilder();
		bar.append("&e[");

		int scale = 20;
		int bars = Math.round(scale - ((xptonext * scale) / data.getXpNeeded()));
		for (int i = 0; i < bars; i++)
		{
			bar.append("&b=");
		}
		
		int left = scale - bars;
		for (int ii = 0; ii < left; ii++)
		{
			bar.append("&e=");
		}

		bar.append("&e]");

		sendMessage(bar.toString());
	}
}