package net.dmulloy2.swornrpg;

import java.util.HashMap;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class AbilitiesManager
{
	public SwornRPG plugin;
	public void initialize(SwornRPG plugin)
	{
		this.plugin = plugin;
		new FrenzyRemoveThread().runTaskTimer(plugin, 0, 20);
		new SpickRemoveThread().runTaskTimer(plugin, 0, 20);
	}
	
	public HashMap<String, Long> frenzyWaiting = new HashMap<String, Long>();
	public HashMap<String, Long> spickWaiting = new HashMap<String, Long>();
	
	/**Frenzy Mode!**/
	public void activateFrenzy(final Player player, boolean command, Action... actions)
	{
		/**Enable Check**/
		if (!plugin.frenzyenabled)
		{
			if (command) sendpMessage(player, plugin.getMessage("command_disabled"));
			return;
		}
		
		/**GameMode check**/
		if (player.getGameMode() == GameMode.CREATIVE)
		{
			if (command) sendpMessage(player, plugin.getMessage("creative_ability"));
			return;
		}
		
		/**Check for Frenzy In Progress**/
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isFrenzy())
		{
			if (command) sendpMessage(player, plugin.getMessage("frenzy_in_progress"));
			return;
		}
		
		/**Action Check**/
		boolean activate = false;
		if (actions.length > 0)
		{
			for (Action action : actions)
			{
				if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK || action == Action.PHYSICAL)
					activate = true;
			}
		}
		else
		{
			activate = true;
		}
		
		/**The player did not left click**/
		if (activate == false)
		{
			if (!frenzyWaiting.containsKey(player.getName()))
			{
				sendpMessage(player, plugin.getMessage("ability_ready"), "Sword");
				frenzyWaiting.put(player.getName(), System.currentTimeMillis());
			}
			return; // Nothing more here.
		}
		
		/**Check if the player is on the waiting list**/
		if (frenzyWaiting.containsKey(player.getName()))
		{
			/**Cooldown Check**/
			if (data.isFcooldown())
			{
				sendpMessage(player, plugin.getMessage("frenzy_cooldown"), (data.getFrenzycd()));
				frenzyWaiting.remove(player.getName());
				return;
			}
			frenzyWaiting.remove(player.getName());
		}
		else
		{
			return;
		}
		
		/**Activate Frenzy!**/
		sendpMessage(player, plugin.getMessage("frenzy_enter"));
		data.setFrenzy(true);
		int strength = 0;
		int level = data.getLevel();
		if (level == 0)
			level = 1;
		/**Duration = frenzy base duraton + (frenzy multiplier x level)**/
		final int duration = (20*(plugin.frenzyd + (level*plugin.frenzym)));
		player.addPotionEffect(PotionEffectType.SPEED.createEffect((int) duration, strength));
		player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect((int) duration, strength));
		player.addPotionEffect(PotionEffectType.REGENERATION.createEffect((int) duration, strength));
		player.addPotionEffect(PotionEffectType.JUMP.createEffect((int) duration, strength));
		player.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect((int) duration, strength));
		player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect((int) duration, strength));
		if (plugin.debug) plugin.outConsole(plugin.getMessage("log_frenzy_activate"), player.getName(), duration);
		class FrenzyThread extends BukkitRunnable
		{
			@Override
			public void run()
			{
				sendpMessage(player, plugin.getMessage("frenzy_wearoff"));
				int cooldown = (duration*plugin.frenzycd);
				data.setFrenzycd(cooldown);
				data.setFcooldown(true);
				data.setFrenzy(false);
				if (plugin.debug) plugin.outConsole(plugin.getMessage("log_frenzy_cooldown"), player.getName(), cooldown);
			}
		}
		new FrenzyThread().runTaskLater(plugin, duration);
	}
	
	/**Super Pickaxe!**/
	public void activateSpick(final Player player, boolean command, Action... actions)
	{
		/**Enable Check**/
		if (plugin.spenabled == false)
		{
			if (command) sendpMessage(player, plugin.getMessage("command_disabled"));
			return;
		}
		
		/**GameMode check**/
		if (player.getGameMode() == GameMode.CREATIVE)
		{
			if (command) sendpMessage(player, plugin.getMessage("creative_ability"));
			return;
		}
		
		/**Check to make sure it is an iron or diamond pickaxe**/
		String inhand = player.getItemInHand().getType().toString().toLowerCase().replaceAll("_", " ");
		String[] array = inhand.split(" ");
			
		if (array.length < 2)
		{
			if (command) sendpMessage(player, plugin.getMessage("superpick_invalid_item"));
			return;
		}
			
		if (!array[0].equals("diamond") && !array[0].equals("iron"))
		{
			if (command) sendpMessage(player, plugin.getMessage("superpick_invalid_item"));
			return;
		}
			
		if (!array[1].equals("spade") && !array[1].equals("pickaxe"))
		{
			if (command) sendpMessage(player, plugin.getMessage("superpick_invalid_item"));
			return;
		}
		
		/**If the player is using SuperPick, return**/
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isSpick())
		{
			if (command) sendpMessage(player, plugin.getMessage("superpick_inprogress"));
			return;
		}
		
		/**Check the Action**/
		boolean activate = false;
		if (actions.length > 0)
		{
			for (Action action : actions)
			{
				if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK || action == Action.PHYSICAL)
					activate = true;
			}
		}
		else
		{
			activate = true;
		}
		
		/**The player did not left click**/
		if (activate == false)
		{
			if (!spickWaiting.containsKey(player.getName()))
			{
				sendpMessage(player, plugin.getMessage("ability_ready"), inhand);
				spickWaiting.put(player.getName(), System.currentTimeMillis());
			}
			return;
		}
		
		/**Check if the player is on the waiting list**/
		if (spickWaiting.containsKey(player.getName()))
		{
			/**Cooldown Check**/
			if (data.isScooldown())
			{
				sendpMessage(player, plugin.getMessage("superpick_cooldown"), (data.getSuperpickcd()));
				spickWaiting.remove(player.getName());
				return;
			}
			spickWaiting.remove(player.getName());
		}
		else
		{
			return;
		}

		/**Activate Super Pickaxe!**/
		sendpMessage(player, plugin.getMessage("superpick_activated"));
		int level = data.getLevel();
		if (level == 0)
			level = 1;
		final int duration = (20*(plugin.spbaseduration + (level*plugin.superpickm)));
		int strength = 1;
		data.setSpick(true);
		player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect((int) duration, strength));
		if (plugin.debug) plugin.outConsole(plugin.getMessage("log_superpick_activate"), player.getName(), duration);
		class SuperPickThread extends BukkitRunnable
		{
			@Override
			public void run()
			{
				sendpMessage(player, plugin.getMessage("superpick_wearoff"));
				data.setSpick(false);
				data.setScooldown(true);
				int cooldown = (duration*plugin.superpickcd);
				data.setSuperpickcd(cooldown);
				if (plugin.debug) plugin.outConsole(plugin.getMessage("log_superpick_cooldown"), player.getName(), cooldown);
			}
		}
		new SuperPickThread().runTaskLater(plugin, duration);
	}
	
	/**Unlimited Ammo**/
	public void activateAmmo(final Player player)
	{
		/**PVPGunPlus Enable Check**/
		PluginManager pm = plugin.getServer().getPluginManager();
		if (!pm.isPluginEnabled("PVPGunPlus"))
		{
			sendpMessage(player, plugin.getMessage("plugin_not_found"), "PVPGunPlus");
			return;
		}
		
		/**Check to see if they are already using Unlimited Ammo**/
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isUnlimtdammo())
		{
			sendpMessage(player, plugin.getMessage("already_using_ammo"));
			return;
		}
		
		/**Cooldown Check**/
		if (data.isAmmocooling())
		{
			sendpMessage(player, plugin.getMessage("ammo_cooldown"), data.getAmmocd());
			return;
		}
		
		/**Activate Unlimited Ammo!**/
		int level = data.getLevel();
		if (level == 0)
			level = 1;
		final int duration = (20*(plugin.ammobaseduration + (level*plugin.ammomultiplier)));
		data.setUnlimtdammo(true);
		sendpMessage(player, plugin.getMessage("ammo_now_unlimited"));
		if (plugin.debug) plugin.outConsole(plugin.getMessage("log_ammo_activate"), player.getName(), duration);
		class UnlimitedAmmoThread extends BukkitRunnable
		{
			@Override
			public void run()
			{
				data.setUnlimtdammo(false);
				sendpMessage(player, plugin.getMessage("ammo_nolonger_unlimited"));
				data.setAmmocooling(true);
				int cooldown = (duration*plugin.ammocooldown);
				data.setAmmocd(cooldown);
				if (plugin.debug) plugin.outConsole(plugin.getMessage("log_ammo_cooldown"), player.getName(), cooldown);
			}
		}
		new UnlimitedAmmoThread().runTaskLater(plugin, duration);
	}
	
	/**Send non prefixed message**/
	protected final void sendMessage(Player player, String msg, Object... args) 
	{
		player.sendMessage(FormatUtil.format(msg, args));
	}
	
	/**Send prefixed message**/
	protected final void sendpMessage(Player player, String msg, Object... args) 
	{
		player.sendMessage(plugin.prefix + FormatUtil.format(msg, args));
	}
	
	/**If the player has been on the frenzy waiting list for more than 3 seconds, remove them from the list**/
	public class FrenzyRemoveThread extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (long time : frenzyWaiting.values())
			{
				if ((System.currentTimeMillis() - time) >= 60)
				{
					frenzyWaiting.remove(time);
				}
			}
		}
	}
	
	/**If the player has been on the spick waiting list for more than 3 seconds, remove them from the list**/
	public class SpickRemoveThread extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (long time : spickWaiting.values())
			{
				if ((System.currentTimeMillis() - time) >= 60)
				{
					spickWaiting.remove(time);
				}
			}
		}
	}
}