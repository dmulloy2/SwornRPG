package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

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
		if (args.length == 0)
		{
			if (sender instanceof Player)
			{
				PlayerData data = getPlayerData(player);
				int level = data.getLevel();
				int nextlevel = level+1;
				int totalxp = data.getTotalxp();
				int totalxpneeded = (Math.abs(data.getTotalxp()) + data.getXpneeded());
				int xptonext = (data.getXpneeded() - data.getPlayerxp());
				sendpMessage("&eYou are level &a" + level);
				sendpMessage("&eYou are &a" + xptonext + " &exp away from level &a" + nextlevel);
				sendpMessage("&e(&a" + totalxp + "&e/&a" + totalxpneeded + "&e)");
			}
			else
			{
				sendpMessage(plugin.getMessage("mustbeplayer"));
			}
		}
		else if (args.length == 1)
		{
			Player target = Util.matchPlayer(args[0]);
			if (target == null)
			{
				sendpMessage(plugin.getMessage("noplayer"));
			}
			else
			{
				PlayerData data = getPlayerData(target);
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
				sendpMessage("&eLevel info for: " + title);
				sendpMessage("&e" + name + " level &a" + level);
				sendpMessage("&e" + name + " &a" + xptonext + " &exp away from level &a" + nextlevel);
				sendpMessage("&e(&a" + totalxp + "&e/&a" + totalxpneeded + "&e)");
			}
		}
	}
}