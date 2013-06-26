package net.dmulloy2.swornrpg.commands;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;

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
		PlayerData data = getPlayerData(player);
		Block block = player.getTargetBlock(null, 10);
		if (block == null)
		{
			sendpMessage(plugin.getMessage("no_block"));
			return;
		}

		String seat = FormatUtil.getFriendlyName(block.getType());
		if (seat.contains("Step")||seat.contains("Stair"))
		{
			Arrow it = player.getWorld().spawnArrow(block.getLocation().add(0.5, 0, 0.5), new Vector(0, 0, 0), 0f, 0f);
			it.setPassenger(player);
			data.setSitting(true);	
			
			sendpMessage(plugin.getMessage("now_sitting"), seat);
			sendpMessage(plugin.getMessage("standup_command"));
		}
		else
		{
			sendpMessage(plugin.getMessage("no_chair"));
		}
	}
}