package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

public class CmdFrenzy extends SwornRPGCommand
{
	public CmdFrenzy (SwornRPG plugin)
	{
		super(plugin);
		this.name = "frenzy";
		this.description = "Enter Beastmode!";
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if(!plugin.frenzyenabled)
			return;
		PlayerData data = getPlayerData(player);
		if (data.isFrenzyused() == false)
		{
			int level = data.getPlayerxp()/125;
			if (level == 0)
				level = 1;
			int strength = 0;
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
			sendpMessage("&cError, you have already used frenzy for this level");
		}
	}
}