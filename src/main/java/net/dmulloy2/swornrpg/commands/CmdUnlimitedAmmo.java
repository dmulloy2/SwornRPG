package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdUnlimitedAmmo extends SwornRPGCommand
{

	public CmdUnlimitedAmmo(SwornRPG plugin) 
	{
		super(plugin);
		this.name = "unlimitedammo";
		this.aliases.add("ammo");
		this.description = "Activate unlimited ammo ability";
		this.permission = Permission.UNLIMITEDAMMO;

		this.mustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		plugin.getAbilityHandler().activateAmmo(player);
	}
}