package net.dmulloy2.swornrpg.commands;

import org.bukkit.potion.PotionEffectType;

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
				sendpMessage(plugin.getMessage("superpick_cooldown_time"), (data.getSuperpickcd()/20));
				return;
			}
			if (player.getItemInHand() != null)
			{
				String inhand = player.getItemInHand().toString().toLowerCase().replaceAll("_", " ");
				if (inhand.contains("pickaxe")&&!inhand.contains("wood")&&!inhand.contains("gold"))
				{
					sendpMessage(plugin.getMessage("superpick_question"));
					sendpMessage(plugin.getMessage("superpick_activate"));
					int level = data.getLevel();
					final int duration = (20*(plugin.spbaseduration + (level*plugin.superpickm)));
					int strength = 1;
					data.setSpick(true);
					player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect((int) duration, strength));
					if (plugin.debug) plugin.outConsole(player.getName() + "has activated super pickaxe. Duration: " + duration);
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
					{
						@Override
						public void run()
						{
							sendpMessage(plugin.getMessage("superpick_wearoff"));
							data.setSpick(false);
							data.setScooldown(true);
							int cooldown = (20*(duration*plugin.superpickcd));
							data.setSuperpickcd(cooldown);
							if (plugin.debug) plugin.outConsole(player.getName() + "has a cooldown of " + cooldown + " for super pickaxe");
						}				
					},(duration));
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
}