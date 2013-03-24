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
				sendpMessage("&cYou are currently using super pickaxe");
				return;
			}
			if (data.isScooldown())
			{
				sendpMessage("&cYou are still recovering from super pick!");
				sendpMessage("&cYou have " + (data.getSuperpickcd()/20) + " seconds left");
				return;
			}
			if (player.getItemInHand() != null)
			{
				String inhand = player.getItemInHand().toString().toLowerCase().replaceAll("_", " ");
				if (inhand.contains("pickaxe")&&!inhand.contains("wood")&&!inhand.contains("gold"))
				{
					sendpMessage("&aReady to mine?");
					sendpMessage("&aYour pickaxe has become a super pickaxe!");
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
							sendpMessage("&eSuper pickaxe ability has worn off");
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
					sendpMessage("&cYour inhand item is not valid for this command!");
					sendpMessage("&cYou must have an iron or diamond pickaxe!");
				}
			}
			else
			{
				sendpMessage("&cYou must have a pickaxe to use this command!");
			}
		}
		else
		{
			sendpMessage("&cThis command has been disabled by your server owner!");
		}
	}
}