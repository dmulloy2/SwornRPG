package net.dmulloy2.swornrpg.commands;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdEject extends SwornRPGCommand
{
	public CmdEject (SwornRPG plugin)
	{
		super(plugin);
		this.name = "eject";
		this.description = "Remove a player from your head";
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		Entity passenger = player.getPassenger();
		if (passenger == null)
		{
			err(getMessage("no_passenger"));
			return;
		}
		
		String name = "";
		if (passenger instanceof Player)
		{
			name = ((Player)passenger).getName();
		}
		else
		{
			String type = FormatUtil.getFriendlyName(passenger.getType());
			String article = FormatUtil.getArticle(type);
			
			name = article + " " + type;
		}
		
		sendMessage(getMessage("eject_successful"), name);
		
		player.eject();
	}
}