package net.dmulloy2.swornrpg.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

/**
 * @author dmulloy2
 */

public class CmdAbilities extends SwornRPGCommand
{
	public CmdAbilities (SwornRPG plugin)
	{
		super(plugin);
		this.name = "abilities";
		this.description = "Check SwornRPG ability levels";
		this.aliases.add("skills");
		this.optionalArgs.add("player");
		this.mustBePlayer = true;
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
					err(plugin.getMessage("noplayer"));
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
				err(plugin.getMessage("console_level"));
				return;
			}
		}
		PlayerData data = getPlayerData(target);
		if (data == null)
		{
			err(plugin.getMessage("noplayer"));
			return;
		}

		int level = data.getLevel();
		
		if (sender == target)
			sendMessage(plugin.getMessage("ability_header_self"));
		else
			sendMessage(plugin.getMessage("ability_header_others"), target.getName());
		
		if (plugin.frenzyenabled)
		{
			StringBuilder line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("ability_frenzy"), 
					(plugin.frenzyd + (level*plugin.frenzym))));
			
			if (data.isFrenzyCooldownEnabled())
			{
				line.append(FormatUtil.format(" &c(Cooldown: {0})", data.getFrenzyCooldownTime()));
			}
			
			sendMessage(line.toString());
		}
		
		if (plugin.spenabled)
		{
			StringBuilder line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("ability_spick"), 
					(plugin.spbaseduration + (level*plugin.superpickm))));
			
			if (data.isSuperPickaxeCooldownEnabled())
			{
				line.append(FormatUtil.format(" &c(Cooldown: {0})", data.getSuperPickaxeCooldownTime()));
			}
			
			sendMessage(line.toString());
		}
		
		if (plugin.ammoenabled && plugin.getPluginManager().isPluginEnabled("SwornGuns"))
		{
			StringBuilder line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("ability_ammo"), 
					(plugin.ammobaseduration + (level*plugin.ammomultiplier))));
			
			if (data.isUnlimitedAmmoCooldownEnabled())
			{
				line.append(FormatUtil.format(" &c(Cooldown: {0})", data.getUnlimitedAmmoCooldownTime()));
			}
			
			sendMessage(line.toString());
		}
		
		sendMessage(plugin.getMessage("ability_level"));
	}
}