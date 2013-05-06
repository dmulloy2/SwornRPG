package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;

/**
 * @author dmulloy2
 */

public class CmdUnlimitedAmmo extends SwornRPGCommand
{

	public CmdUnlimitedAmmo(SwornRPG plugin) 
	{
		super(plugin);
		this.name = "ammo";
		this.description = "Unlimited ammo for your gun!";
		this.aliases.add("unlimitedammo");
		this.mustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		plugin.getAbilitiesManager().activateAmmo(player);
	}
}