package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdSuperPickaxe extends SwornRPGCommand
{
	public CmdSuperPickaxe(SwornRPG plugin)
	{
		super(plugin);
		this.name = "superpick";
		this.aliases.add("mine");
		this.aliases.add("superpickaxe");
		this.description = "Activate super pickaxe";
		this.permission = Permission.SUPERPICKAXE;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		plugin.getAbilityHandler().activateSuperPickaxe(player, true);
	}
}