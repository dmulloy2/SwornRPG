package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;

/**
 * @author dmulloy2
 */

public class CmdStandup extends SwornRPGCommand
{
	public CmdStandup(SwornRPG plugin)
	{
		super(plugin);
		this.name = "standup";
		this.aliases.add("stand");
		this.description = "Get out of your chair";
		this.permission = Permission.STANDUP;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		// Weird bug: if the arrow despawns, players will become "ghosts", this somewhat fixes it
		// The previousLocation variable will only have a value if a player has recently sat
		// It is always cleared when a player stands up or leaves/is kicked

		Entity vehicle = player.getVehicle();

		PlayerData data = getPlayerData(player);
		if (data.getPreviousLocation() != null)
		{
			player.teleport(data.getPreviousLocation());
			data.setPreviousLocation(null);

			if (vehicle != null)
				vehicle.remove();

			return;
		}

		if (vehicle == null || ! vehicle.isValid() || ! (vehicle instanceof Arrow))
		{
			err(getMessage("not_sitting"));
			return;
		}

		player.leaveVehicle();
		vehicle.remove();
	}
}