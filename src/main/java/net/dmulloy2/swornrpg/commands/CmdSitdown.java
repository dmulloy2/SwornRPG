package net.dmulloy2.swornrpg.commands;

import java.util.HashSet;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
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
		Block block = getTargetBlock(player, 10);
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

		PlayerData data = getPlayerData(player);
		data.setPreviousLocation(player.getLocation().clone());

		Arrow it = player.getWorld().spawnArrow(block.getLocation().add(0.5, 0, 0.5), new Vector(0, 0, 0), 0F, 0F);
		if (! it.setPassenger(player))
		{
			it.remove();
			data.setPreviousLocation(null);
			err(getMessage("chair_error"));
			return;
		}

		sendpMessage(getMessage("chair_now_sitting"), seat);
		sendpMessage(getMessage("chair_standup"), new CmdStandup(plugin).getUsageTemplate(false));
	}

	@SuppressWarnings("deprecation")
	private static Block getTargetBlock(Player player, int maxDistance)
	{
		// TODO: LivingEntity#getTargetBlock(Set<Material>, int)
		// was added, but it isn't showing up in Eclipse
		return player.getTargetBlock((HashSet<Byte>) null, maxDistance);
	}
}