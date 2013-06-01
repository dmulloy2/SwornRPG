package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class CmdRide extends SwornRPGCommand
{
	public CmdRide (SwornRPG plugin)
	{
		super(plugin);
		this.name = "ride";
		this.description = "Get on a player's head!";
		this.requiredArgs.add("player");
		this.permission = PermissionType.CMD_RIDE.permission;
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{		
		/**Make sure the player exists**/
		final Player target = Util.matchPlayer(args[0]);
		if (target == null)
		{
			sendpMessage(plugin.getMessage("noplayer"));
			return;
		}
		
		/**Check for another player riding**/
		if (target.getPassenger() != null)
		{
			if (target.getPassenger() instanceof Player)
			{
				PlayerData passengerData = getPlayerData((Player)target.getPassenger());
				passengerData.setRiding(false);
				target.eject();
			}
		}
			
		/**Lets ride sum peeps!**/
		final PlayerData data = getPlayerData(player);
		final PlayerData targetData = getPlayerData(target);
				
		Location targetLocation = target.getLocation();
		player.teleport(targetLocation);	
		class PassengerTask extends BukkitRunnable
		{
			@Override
			public void run()
			{	
				data.setRiding(true);
				targetData.setVehicle(true);
					
				target.setPassenger(player);
					
				sendpMessage(plugin.getMessage("now_riding"), target.getName());
			}	
		}
		new PassengerTask().runTaskLater(plugin, 20);
	}
}