package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

/**
 * @author dmulloy2
 */

public class CmdSitdown extends SwornRPGCommand
{
	public CmdSitdown(SwornRPG plugin)
	{
		super(plugin);
		this.name = "sitdown";
		this.aliases.add("sit");
		this.description = "Sit in a chair";
		this.permission = Permission.SITDOWN;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		// TODO: There is no valid replacement for this.
		// A method that ignores transparency would be wonderful...
		@SuppressWarnings("deprecation")
		Block block = player.getTargetBlock(null, 10);

		if (block == null)
		{
			err(getMessage("chair_no_block"));
			return;
		}

		String seat = FormatUtil.getFriendlyName(block.getType());
		if (! seat.contains("Step") && ! seat.contains("Stair"))
		{
			err(getMessage("chair_not_chair"));
			return;
		}

		Arrow it = player.getWorld().spawnArrow(block.getLocation().add(0.5, 0, 0.5), new Vector(0, 0, 0), 0f, 0f);

		if (! it.setPassenger(player))
		{
			it.remove();

			err(getMessage("chair_error"));
			return;
		}

		sendpMessage(getMessage("chair_now_sitting"), seat);
		sendpMessage(getMessage("chair_standup"), new CmdStandup(plugin).getUsageTemplate(false));
	}
}