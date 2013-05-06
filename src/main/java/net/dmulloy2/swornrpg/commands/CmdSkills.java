package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

/**
 * @author dmulloy2
 */

public class CmdSkills extends SwornRPGCommand
{
	public CmdSkills (SwornRPG plugin)
	{
		super(plugin);
		this.name = "skills";
		this.description = "Check your SwornRPG skill levels";
		this.aliases.add("skill");
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		PlayerData data = getPlayerData(player);
		int level = data.getLevel();
		sendMessage("&4==== &6SwornRPG Skills &4====");
		sendMessage("&6Level: &e{0}", level);
		
		if (plugin.frenzyenabled)
		{
			if (data.isFcooldown())
				sendMessage("&6Frenzy Duration: &e{0} seconds &c(Cooldown: {1})", (plugin.frenzyd + (level*plugin.frenzym)), data.getFrenzycd());
			else
				sendMessage("&6Frenzy Duration: &e{0} seconds", (plugin.frenzyd + (level*plugin.frenzym)));
		}
		
		if (plugin.spenabled)
		{
			if (data.isScooldown())
				sendMessage("&6Super Pick Duration: &e{0} seconds &c(Cooldown: {1})", (plugin.spbaseduration + (level*plugin.superpickm)), data.getSuperpickcd());
			else
				sendMessage("&6Super Pick Duration: &e{0} seconds", (plugin.spbaseduration + (level*plugin.superpickm)));
		}
		
		if (plugin.ammoenabled && plugin.getServer().getPluginManager().isPluginEnabled("PVPGunPlus"))
		{
			if (data.isAmmocooling())
				sendMessage("&6Unlimited Ammo Duration: &e{0} seconds &c(Cooldown: {1})", (plugin.ammobaseduration + (level*plugin.ammomultiplier)), data.getAmmocd());
			else
				sendMessage("&6Unlimited Ammo Duration: &e{0} seconds", (plugin.ammobaseduration + (level*plugin.ammomultiplier)));
		}
	}
}