package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdAddxp extends SwornRPGCommand
{
	public CmdAddxp(SwornRPG plugin)
	{
		super(plugin);
		this.name = "addxp";
		this.aliases.add("givexp");
		this.addRequiredArg("player");
		this.addRequiredArg("xp");
		this.description = "Manually give xp to a player";
		this.permission = Permission.ADDXP;
	}

	@Override
	public void perform()
	{
		Player target = Util.matchPlayer(args[0]);
		if (target == null)
		{
			err(plugin.getMessage("player_not_found"), args[0]);
			return;
		}

		if (args[1].equalsIgnoreCase("recalculate"))
		{
			plugin.getExperienceHandler().recalculate(target);
			sendpMessage("&eYou have recalculated &b{0}&e''s level.", target.getName());
			return;
		}

		int giveXP = argAsInt(1, true);
		if (giveXP == -1)
			return;

		plugin.getExperienceHandler().handleXpGain(target, giveXP, "");
		plugin.getExperienceHandler().recalculate(target);

		if (target.getName().equals(player.getName()))
		{
			sendpMessage(plugin.getMessage("addxp_self"), giveXP);
		}
		else
		{
			sendpMessage(plugin.getMessage("addxp_give"), giveXP, target.getName());
			sendpMessage(target, plugin.getMessage("addxp_given"), giveXP);
		}
	}
}