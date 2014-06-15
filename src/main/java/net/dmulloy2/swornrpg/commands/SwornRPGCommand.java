package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.commands.Command;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public abstract class SwornRPGCommand extends Command
{
	protected final SwornRPG plugin;
	public SwornRPGCommand(SwornRPG plugin)
	{
		super(plugin);
		this.plugin = plugin;
	}

	protected final String getMessage(String msg)
	{
		return plugin.getMessage(msg);
	}

	// ---- Player Management

	protected PlayerData getPlayerData(OfflinePlayer target)
	{
		return plugin.getPlayerDataCache().getData(target);
	}

	protected final OfflinePlayer getTarget(int arg, boolean others)
	{
		OfflinePlayer target = null;
		if (args.length == 1 && others)
		{
			target = Util.matchPlayer(args[arg]);
			if (target == null)
			{
				target = Util.matchOfflinePlayer(args[arg]);
				if (target == null)
				{
					err(getMessage("player_not_found"), args[arg]);
					return null;
				}
			}
		}
		else
		{
			if (sender instanceof Player)
			{
				target = player;
			}
			else
			{
				err(getMessage("console_level"));
				return null;
			}
		}

		return target;
	}
}