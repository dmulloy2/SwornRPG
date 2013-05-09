package net.dmulloy2.swornrpg.commands;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

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
		Block block = player.getTargetBlock(null, 100);
		if (block == null)
		{
			sendpMessage(plugin.getMessage("no_block"));
		}
		else if (player.getLocation().distance(block.getLocation()) > 5)
		{
			sendpMessage(plugin.getMessage("too_far_away"));
		}
		else
		{
			String seat = block.getType().toString().toLowerCase().replaceAll("_", " ");
			if (seat.contains("step")||seat.contains("stair"))
			{
				data.setSitting(true);	
				Arrow it = player.getWorld().spawnArrow(block.getLocation().add(0.5, 0, 0.5), new Vector(0, 0, 0), 0f, 0f);
				it.setPassenger(player);
				sendpMessage(plugin.getMessage("now_sitting"), seat);
				sendpMessage(plugin.getMessage("standup_command"));
			}
			else
			{
				sendpMessage(plugin.getMessage("no_chair"));
			}
		}
	}
}