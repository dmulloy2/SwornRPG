package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdFrenzy extends SwornRPGCommand
{
	public CmdFrenzy(SwornRPG plugin)
	{
		super(plugin);
		this.name = "frenzy";
		this.description = "Enter frenzy mode";
		this.permission = Permission.FRENZY;

		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		plugin.getAbilityHandler().activateFrenzy(player, true);
	}
}