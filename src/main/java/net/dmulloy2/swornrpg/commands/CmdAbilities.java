/**
 * SwornRPG - a Bukkit plugin
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.handlers.AbilityHandler;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.TimeUtil;

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
		this.addOptionalArg("player");
		this.description = "Check SwornRPG ability levels";
		this.permission = Permission.ABILITIES;
	}

	@Override
	public void perform()
	{
		OfflinePlayer target = getTarget(0, hasPermission(Permission.ABILITIES_OTHERS));
		if (target == null)
			return;

		PlayerData data = getPlayerData(target);
		if (data == null)
		{
			err(plugin.getMessage("missing_data"), target.getName());
			return;
		}

		if (sender.getName().equals(target.getName()))
		{
			sendMessage(plugin.getMessage("ability_header_self"));
		}
		else
		{
			sendMessage(plugin.getMessage("ability_header_others"), target.getName());
		}

		AbilityHandler abilityHandler = plugin.getAbilityHandler();
		if (abilityHandler.isFrenzyEnabled())
		{
			StringBuilder line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("ability_frenzy"),
					TimeUtil.toSeconds(abilityHandler.getFrenzyDuration(data.getLevel()))));

			if (data.getCooldowns().containsKey("frenzy"))
			{
				line.append(FormatUtil.format(" &c(Cooldown: {0}s)", TimeUtil.toSeconds(data.getCooldowns().get("frenzy"))));
			}

			sendMessage(line.toString());
		}

		if (abilityHandler.isSuperPickaxeEnabled())
		{
			StringBuilder line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("ability_spick"),
					TimeUtil.toSeconds(abilityHandler.getSuperPickaxeDuration(data.getLevel()))));

			if (data.getCooldowns().containsKey("superpick"))
			{
				line.append(FormatUtil.format(" &c(Cooldown: {0}s)", TimeUtil.toSeconds(data.getCooldowns().get("superpick"))));
			}

			sendMessage(line.toString());
		}

		if (abilityHandler.isUnlimitedAmmoEnabled() && plugin.getPluginManager().isPluginEnabled("SwornGuns"))
		{
			StringBuilder line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("ability_ammo"),
					TimeUtil.toSeconds(abilityHandler.getUnlimitedAmmoDuration(data.getLevel()))));

			if (data.getCooldowns().containsKey("ammo"))
			{
				line.append(FormatUtil.format(" &c(Cooldown: {0}s)", TimeUtil.toSeconds(data.getCooldowns().get("ammo"))));
			}

			sendMessage(line.toString());
		}

		sendMessage(plugin.getMessage("ability_level"));
	}
}
