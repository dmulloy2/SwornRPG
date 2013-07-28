package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

public class CmdSitdown extends SwornRPGCommand
{
	public CmdSitdown (SwornRPG plugin)
	{
		super(plugin);
		this.name = "sitdown";
		this.aliases.add("sit");
		this.description = "Sit in a chair";
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		Block block = player.getTargetBlock(null, 10);
		if (block == null)
		{
			err(getMessage("no_block"));
			return;
		}

		String seat = FormatUtil.getFriendlyName(block.getType());
		if (! seat.contains("Step") && ! seat.contains("Stair"))
		{
			err(getMessage("no_chair"));
			return;
		}
		
		Arrow it = player.getWorld().spawnArrow(block.getLocation().add(0.5, 0, 0.5), new Vector(0, 0, 0), 0f, 0f);
			
		if (! it.setPassenger(player))
		{
			it.remove();
			
			err(getMessage("sit_error"));
			return;
		}

		sendpMessage(getMessage("now_sitting"), seat);
		sendpMessage(getMessage("standup_command"));
	}
}