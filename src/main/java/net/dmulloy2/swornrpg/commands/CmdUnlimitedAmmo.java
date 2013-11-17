package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Ability;
import net.dmulloy2.swornrpg.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdUnlimitedAmmo extends AbstractAbilityCommand
{
	public CmdUnlimitedAmmo(SwornRPG plugin) 
	{
		super(plugin, Ability.UNLIMITED_AMMO);
		this.name = "unlimitedammo";
		this.aliases.add("ammo");
		this.description = "Activate unlimited ammo ability";
		this.permission = Permission.UNLIMITEDAMMO;
	}
}