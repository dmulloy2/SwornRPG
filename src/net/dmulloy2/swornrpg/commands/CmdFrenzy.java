package net.dmulloy2.swornrpg.commands;

import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

/**
 * @author dmulloy2
 */

public class CmdFrenzy extends SwornRPGCommand
{
	public CmdFrenzy (SwornRPG plugin)
	{
		super(plugin);
		this.name = "frenzy";
		this.description = "Enter frenzy mode";
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if(!plugin.frenzyenabled)
		{
			sendpMessage(plugin.getMessage("command_disabled"));
			return;
		}
		final PlayerData data = getPlayerData(player);
		if (!(data.isFcooldown()))
		{
			sendpMessage(plugin.getMessage("frenzy_enter"));
			int strength = 0;
			int level = data.getLevel();
			/**Duration = frenzy base duraton + (frenzy multiplier x level)**/
			int duration = (20*(plugin.frenzyd + (level*plugin.frenzym)));
			player.addPotionEffect(PotionEffectType.SPEED.createEffect((int) duration, strength));
			player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect((int) duration, strength));
			player.addPotionEffect(PotionEffectType.REGENERATION.createEffect((int) duration, strength));
			player.addPotionEffect(PotionEffectType.JUMP.createEffect((int) duration, strength));
			player.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect((int) duration, strength));
			player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect((int) duration, strength));
			if (plugin.debug) plugin.outConsole("{0} has entered frenzy mode. Duration: {1}", player.getName(), duration);
			new FrenzyThread().runTaskLater(plugin, duration);
		}
		else
		{
			sendpMessage(plugin.getMessage("frenzy_cd_header"));
			sendpMessage(plugin.getMessage("frenzy_cd_time"), (data.getFrenzycd()*30));
		}
	}
	
	public class FrenzyThread extends BukkitRunnable
	{
		@Override
		public void run()
		{
			PlayerData data = getPlayerData(player);
			int level = data.getLevel();
			sendpMessage(plugin.getMessage("frenzy_wearoff"));
			int duration = (20*(plugin.frenzyd + (level*plugin.frenzym)));
			int cooldown = (duration*plugin.frenzycd);
			data.setFrenzycd(cooldown);
			data.setFcooldown(true);
			if (plugin.debug) plugin.outConsole("{0} has a cooldown of {1} for frenzy", player.getName(), cooldown);
		}
	}
}