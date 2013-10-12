package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */

public class CmdSpouse extends SwornRPGCommand
{
	public CmdSpouse(SwornRPG plugin)
	{
		super(plugin);
		this.name = "spouse";
		this.optionalArgs.add("player");
		this.description = "Check a player's spouse";
		this.permission = Permission.SPOUSE;
	}

	@Override
	public void perform()
	{
		if (! plugin.getConfig().getBoolean("marriage"))
		{
			err(plugin.getMessage("command_disabled"));
			return;
		}

		OfflinePlayer target = getTarget(0, hasPermission(Permission.SPOUSE_OTHERS));

		PlayerData data = getPlayerData(target);
		String spouse = data.getSpouse();

		String name;
		if (target.getName().equals(sender.getName()))
		{
			name = "You are";
		}
		else
		{
			name = target.getName() + " is";
		}

		if (spouse != null)
		{
			sendpMessage(plugin.getMessage("spouse_info"), name, spouse);
		}
		else
		{
			sendpMessage(plugin.getMessage("no_spouse"), name);
		}
	}
}