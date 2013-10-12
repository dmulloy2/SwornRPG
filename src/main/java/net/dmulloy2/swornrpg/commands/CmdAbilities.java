package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.handlers.AbilityHandler;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */

public class CmdAbilities extends SwornRPGCommand
{
	public CmdAbilities(SwornRPG plugin)
	{
		super(plugin);
		this.name = "abilities";
		this.aliases.add("skills");
		this.optionalArgs.add("player");
		this.description = "Check SwornRPG ability levels";
		this.permission = Permission.ABILITIES;
	}

	@Override
	public void perform()
	{
		OfflinePlayer target = getTarget(0, hasPermission(Permission.ABILITIES_OTHERS));
		if (target == null)
			return;

		if (sender.getName().equals(target.getName()))
		{
			sendMessage(plugin.getMessage("ability_header_self"));
		}
		else
		{
			sendMessage(plugin.getMessage("ability_header_others"), target.getName());
		}

		AbilityHandler abilityHandler = plugin.getAbilityHandler();

		PlayerData data = getPlayerData(target);
		if (abilityHandler.isFrenzyEnabled())
		{
			StringBuilder line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("ability_frenzy"), abilityHandler.getFrenzyDuration(data)));

			if (data.isFrenzyCooldownEnabled())
			{
				line.append(FormatUtil.format(" &c(Cooldown: {0})", data.getFrenzyCooldownTime()));
			}

			sendMessage(line.toString());
		}

		if (abilityHandler.isSuperPickaxeEnabled())
		{
			StringBuilder line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("ability_spick"), abilityHandler.getSuperPickaxeDuration(data)));

			if (data.isSuperPickaxeCooldownEnabled())
			{
				line.append(FormatUtil.format(" &c(Cooldown: {0})", data.getSuperPickaxeCooldownTime()));
			}

			sendMessage(line.toString());
		}

		if (abilityHandler.isUnlimitedAmmoEnabled() && plugin.getPluginManager().isPluginEnabled("SwornGuns"))
		{
			StringBuilder line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("ability_ammo"), abilityHandler.getUnlimitedAmmoCooldown(data)));

			if (data.isUnlimitedAmmoCooldownEnabled())
			{
				line.append(FormatUtil.format(" &c(Cooldown: {0})", data.getUnlimitedAmmoCooldownTime()));
			}

			sendMessage(line.toString());
		}

		sendMessage(plugin.getMessage("ability_level"));
	}
}