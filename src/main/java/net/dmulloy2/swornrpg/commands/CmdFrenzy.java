package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Ability;
import net.dmulloy2.swornrpg.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdFrenzy extends AbstractAbilityCommand
{
	public CmdFrenzy(SwornRPG plugin)
	{
		super(plugin, Ability.FRENZY);
		this.name = "frenzy";
		this.description = "Enter frenzy mode";
		this.permission = Permission.FRENZY;

		this.mustBePlayer = true;
	}
}