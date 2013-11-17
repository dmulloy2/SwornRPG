package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class CmdRide extends SwornRPGCommand
{
	public CmdRide(SwornRPG plugin)
	{
		super(plugin);
		this.name = "ride";
		this.requiredArgs.add("player");
		this.description = "Get on a player's head";
		this.permission = Permission.RIDE;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		final Player target = Util.matchPlayer(args[0]);
		if (target == null)
		{
			err(getMessage("noplayer"));
			return;
		}

		// TODO: Move these to the messages.properties
		if (target.getPassenger() != null)
		{
			err("Someone is already riding that person!");
			return;
		}

		if (target.getVehicle() != null)
		{
			err("That person is riding someone else!");
			return;
		}

		player.teleport(target);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				target.setPassenger(player);

				sendpMessage(plugin.getMessage("now_riding"), target.getName());
			}
		}.runTaskLater(plugin, 40L);
	}
}