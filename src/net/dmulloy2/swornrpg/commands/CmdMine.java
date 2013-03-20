package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;

/**
 * @author dmulloy2
 * Unimplemented, functionality coming soon
 */

public class CmdMine extends SwornRPGCommand
{
	public CmdMine (SwornRPG plugin)
	{
		super(plugin);
		this.name = "mine";
		this.description = "Activate super pickaxe!";
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (player.getItemInHand() != null)
		{
			/**
			String inhand = player.getItemInHand().toString().toLowerCase().replaceAll("_", " ");
			if (inhand.contains("pickaxe")&&!inhand.contains("wood")&&!inhand.contains("gold"));
			{
				sendpMessage("&aReady to mine?");
				sendpMessage("&aYour " + inhand + " has become a super pickaxe!");
			}
			*/
			sendpMessage("&eThis command has not been implemented yet");
		}
		else
		{
			sendpMessage("&cYou must have a pickaxe to use this command!");
		}
	}
}