package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.Location;
import org.bukkit.block.Block;
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
		this.optionalArgs.add("player");
		this.permission = PermissionType.CMD_RIDE.permission;
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (plugin.rideenabled == false)
		{
			sendpMessage(plugin.getMessage("command_disabled"));
			return;
		}
		
		if (plugin.ridepointing)
		{
			Block block = player.getTargetBlock(null, 10);
			Location loc = block.getLocation();
			for (Player pl : plugin.getServer().getOnlinePlayers())
			{
				if (pl.getLocation() == loc)
				{
					PlayerData data = getPlayerData(player);
					data.setRiding(true);
						
					PlayerData targetData = getPlayerData(pl);
					targetData.setVehicle(true);
						
					pl.setPassenger(player);
					
					sendpMessage(plugin.getMessage("now_riding"), pl.getName());
					return;
				}
			}
			
			sendpMessage(plugin.getMessage("noplayer"));
		}
		else
		{
			if (args.length < 1)
			{
				sendpMessage(plugin.getMessage("invalidargs") + " &c(/ride &4<player>&c)");
				return;
			}
			
			final Player target = Util.matchPlayer(args[0]);
			if (target == null)
			{
				sendpMessage(plugin.getMessage("noplayer"));
				return;
			}
			
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
}