package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Ability;

/**
 * @author dmulloy2
 */

public abstract class AbstractAbilityCommand extends SwornRPGCommand
{
	protected Ability ability;
	public AbstractAbilityCommand(SwornRPG plugin, Ability ability)
	{
		super(plugin);
		this.ability = ability;
		this.mustBePlayer = true;
	}

	@Override
	public final void perform()
	{
		plugin.getAbilityHandler().commandActivation(player, ability);
	}
}