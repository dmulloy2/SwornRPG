package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
		this.optionalArgs.add("player");
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
		int level = data.getLevel();
		int nextlevel = level+1;
		int totalxp = data.getTotalxp();
		int totalxpneeded = (Math.abs(data.getTotalxp()) + data.getXpneeded());
		int xptonext = (data.getXpneeded() - data.getPlayerxp());
		String name;
		String title;
		String senderp = sender.getName();
		String targetp = target.getName();
		if (targetp == senderp)
		{
			name = "You are";
			title = senderp;
		}
		else
		{
			name = targetp + " is";
			title = targetp;
		}
		sendpMessage(plugin.getMessage("level_header"), title);
		sendpMessage(plugin.getMessage("level_level"), name, level);
		sendpMessage(plugin.getMessage("level_xptonext"), name, xptonext, nextlevel);
		sendpMessage(plugin.getMessage("level_amount"), totalxp, totalxpneeded);
	}
}