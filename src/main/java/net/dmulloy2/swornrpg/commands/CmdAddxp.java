package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
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
		
		int giveXP = argAsInt(1, true);
		plugin.getExperienceManager().onXPGain(target, giveXP, "");

		if (target.getName().equals(player.getName()))
		{
			sendpMessage(plugin.getMessage("addxp_self"), giveXP);
		}
		else
		{
			sendpMessage(plugin.getMessage("addxp_give"), giveXP, target.getName());
			sendMessageTarget(plugin.getMessage("addxp_given"), target, giveXP);
		}
	}
}