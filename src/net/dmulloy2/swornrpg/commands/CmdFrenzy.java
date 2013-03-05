package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

public class CmdFrenzy implements CommandExecutor
{
	public SwornRPG plugin;
	public CmdFrenzy(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		if (plugin.frenzyenabled == true)
		{
			Player player = null;
			if (sender instanceof Player) 
			{
				player = (Player) sender;
				if (args.length == 0)
				{
					final PlayerData data = plugin.getPlayerDataCache().getData(sender.getName());
					if (data.isFrenzyused() != true)
					{
						int level = data.getPlayerxp()/125;
						if (level == 0)
							level = 1;
						int strength = 1;
						int duration = plugin.frenzyduration*level*20;
						player.addPotionEffect(PotionEffectType.SPEED.createEffect((int) duration, strength));
						player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect((int) duration, strength));
						player.addPotionEffect(PotionEffectType.REGENERATION.createEffect((int) duration, strength));
						player.addPotionEffect(PotionEffectType.JUMP.createEffect((int) duration, strength));
						player.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect((int) duration, strength));
						player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect((int) duration, strength));
						data.setFrenzyused(true);
						data.setFrenzyusedlevel(data.getPlayerxp()/125);
					}
					else
					{
						player.sendMessage(plugin.prefix + ChatColor.RED + "Error, you have already used frenzy for this level");
					}
				}
				else
				{
					player.sendMessage(plugin.invalidargs + "(/frenzy)");
				}
			}
			else
			{
				sender.sendMessage(plugin.mustbeplayer);
			}
		}
		else
		{
			sender.sendMessage(plugin.prefix + ChatColor.RED + "This feature has been disabled by the owner");
		}
		
		return true;
	}
}