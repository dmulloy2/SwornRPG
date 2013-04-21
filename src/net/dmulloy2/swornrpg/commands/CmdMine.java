package net.dmulloy2.swornrpg.commands;

import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

/**
 * @author dmulloy2
 */

public class CmdMine extends SwornRPGCommand
{
	public CmdMine (SwornRPG plugin)
	{
		super(plugin);
		this.name = "mine";
		this.aliases.add("superpick");
		this.description = "Activate super pickaxe!";
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (plugin.spenabled == true)
		{
			final PlayerData data = getPlayerData(player);
			if (data.isSpick())
			{
				sendpMessage(plugin.getMessage("superpick_inprogress"));
				return;
			}
			
			if (data.isScooldown())
			{
				sendpMessage(plugin.getMessage("superpick_cooldown_header"));
				sendpMessage(plugin.getMessage("superpick_cooldown_time"), (data.getSuperpickcd()*30));
				return;
			}
			
			if (player.getItemInHand() != null)
			{
				String inhand = player.getItemInHand().toString().toLowerCase().replaceAll("_", " ");
				if (inhand.contains("pickaxe")&&!inhand.contains("wood")&&!inhand.contains("gold"))
				{
					sendpMessage(plugin.getMessage("superpick_question"));
					sendpMessage(plugin.getMessage("superpick_activated"));
					int level = data.getLevel();
					int duration = (20*(plugin.spbaseduration + (level*plugin.superpickm)));
					int strength = 1;
					data.setSpick(true);
					player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect((int) duration, strength));
					if (plugin.debug) plugin.outConsole("{0} has activated super pickaxe. Duration: {1}", player.getName(), duration);
					new SuperPickThread().runTaskLater(plugin, duration);
				}
				else
				{
					sendpMessage(plugin.getMessage("superpick_invalid_item"));
				}
			}
			else
			{
				sendpMessage(plugin.getMessage("hand_empty"));
			}
		}
		else
		{
			sendpMessage(plugin.getMessage("command_disabled"));
		}
	}
	
	public class SuperPickThread extends BukkitRunnable
	{
		@Override
		public void run()
		{
			PlayerData data = getPlayerData(player);
			int level = data.getLevel();
			int duration = (20*(plugin.spbaseduration + (level*plugin.superpickm)));
			sendpMessage(plugin.getMessage("superpick_wearoff"));
			data.setSpick(false);
			data.setScooldown(true);
			int cooldown = (duration*plugin.superpickcd);
			data.setSuperpickcd(cooldown);
			if (plugin.debug) plugin.outConsole(player.getName() + "{0} has a cooldown of {1} for super pickaxe", player.getName(), cooldown);
		}
	}
}