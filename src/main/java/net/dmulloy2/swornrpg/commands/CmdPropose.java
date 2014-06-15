package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdPropose extends SwornRPGCommand
{
	public CmdPropose(SwornRPG plugin)
	{
		super(plugin);
		this.name = "propose";
		this.requiredArgs.add("player");
		this.description = "Propose marriage to a player";
		this.permission = Permission.PROPOSE;

		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (! plugin.getConfig().getBoolean("marriage"))
		{
			err(getMessage("command_disabled"));
			return;
		}
		
		PlayerData data = getPlayerData(player);
		Player target = Util.matchPlayer(args[0]);
		if (target == null)
		{
			err(getMessage("player_not_found"), args[0]);
			return;
		}
		
		String targetp = target.getName();
		PlayerData data1 = getPlayerData(target);
		if (data.getSpouse() != null)
		{
			err(getMessage("polygamy"));
		}
		else if (targetp.equals(sender.getName()))
		{
			err(getMessage("cannot_marry_self"));
		}
		else if (data1.getSpouse() != null)
		{
			err(getMessage("target_is_married"), targetp);
		}
		else if (data1.getProposals().contains(player.getName()))
		{
			err(getMessage("already_proposed"));
		}
		else
		{
			data1.getProposals().add(player.getName());
			sendpMessage(getMessage("you_have_proposed"), targetp);
			sendpMessage(target, getMessage("send_marriage_request"), sender.getName());
		}
	}
}