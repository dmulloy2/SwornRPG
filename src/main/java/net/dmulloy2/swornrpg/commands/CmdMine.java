package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;

/**
 * @author dmulloy2
 */

public class CmdMine extends SwornRPGCommand
{
	public CmdMine(SwornRPG plugin)
	{
		super(plugin);
		this.name = "mine";
		this.aliases.add("superpick");
		this.description = "Activate super pickaxe!";
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		plugin.getAbilityHandler().activateSpick(player, true);
	}
}