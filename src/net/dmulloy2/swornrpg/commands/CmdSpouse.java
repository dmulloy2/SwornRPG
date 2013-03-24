package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdSpouse extends SwornRPGCommand
{
	public CmdSpouse (SwornRPG plugin)
	{
		super(plugin);
		this.name = "spouse";
		this.aliases.add("spouseinfo");
		this.description = "Check information on a player's spouse";
		this.optionalArgs.add("player");
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (args.length == 1)
		{
			Player target = Util.matchPlayer(args[0]);
			if (target != null)
			{
				String targetp = target.getName();
				PlayerData data = getPlayerData(target);
				String spouse = data.getSpouse();
				if (spouse != null)
				{
					sendpMessage("&e" + targetp + " is married to " + spouse);
				}
				else
				{
					sendpMessage("&e" + targetp + " is not married");
				}
			}
			else
			{
				sendpMessage(plugin.getMessage("noplayer"));
			}
		}
		else if (args.length == 0)
		{
			if (sender instanceof Player)
			{
				PlayerData data = getPlayerData(player);
				String spouse = data.getSpouse();
				if (spouse != null)
				{
					sendpMessage("&eYou are married to " + spouse);				
				}
				else
				{
					sendpMessage("&cYou are not married");
				}
			}
			else
			{
				sendpMessage(plugin.getMessage("mustbeplayer"));
			}
		}
	}
}