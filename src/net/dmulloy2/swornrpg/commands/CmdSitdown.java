package net.dmulloy2.swornrpg.commands;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

public class CmdSitdown extends SwornRPGCommand
{
	public CmdSitdown (SwornRPG plugin)
	{
		super(plugin);
		this.name = "sitdown";
		this.aliases.add("sit");
		this.description = "Sit down on a block";
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		PlayerData data = getPlayerData(player);
		PluginManager pm = Bukkit.getPluginManager();
		if (pm.isPluginEnabled("Factions")||pm.isPluginEnabled("SwornNations"))
		{
			Faction otherFaction = Board.getFactionAt(new FLocation(player.getLocation()));
			if (otherFaction.isWarZone())
			{
				sendpMessage("&cYou cannot use this command in a warzone");
				return;
			}
		}
		Block block = player.getTargetBlock(null, 100);
		if (block == null)
		{
			sendpMessage("&cError, you are not targeting a block!");
		}
		else
		{
			String seat = block.getType().toString().toLowerCase().replaceAll("_", " ");
			if (plugin.debug)
			{
				plugin.outConsole("Seat: " + seat);
				plugin.outConsole("Block: " + block);
			}
			if (seat.contains("step")||seat.contains("stair"))
			{
				data.setSitting(true);	
				Arrow it = player.getWorld().spawnArrow(block.getLocation().add(0.5, 0, 0.5), new Vector(0, 0, 0), 0f, 0f);
				it.setPassenger(player);
				sendpMessage("&eYou are now sitting on a &a" + seat);
				sendpMessage("&eStand up with &c/standup");
			}
			else
			{
				sendpMessage("&cError, you are not targeting a chair!");
			}
		}
	}
}