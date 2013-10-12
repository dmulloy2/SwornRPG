package net.dmulloy2.swornrpg.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.events.FrenzyActivateEvent;
import net.dmulloy2.swornrpg.events.SuperPickaxeActivateEvent;
import net.dmulloy2.swornrpg.events.UnlimitedAmmoActivateEvent;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles the activation of abilities
 * 
 * @author dmulloy2
 */

@Getter
public class AbilityHandler
{
	private boolean frenzyEnabled;
	private int frenzyDuration;
	private int frenzyMultiplier;
	private int frenzyCooldown;

	private boolean superPickaxeEnabled;
	private int superPickaxeDuration;
	private int superPickaxeMultiplier;
	private int superPickaxeCooldown;

	private boolean unlimitedAmmoEnabled;
	private int unlimitedAmmoDuration;
	private int unlimitedAmmoMultiplier;
	private int unlimitedAmmoCooldown;

	private HashMap<String, Long> frenzyWaiting;
	private HashMap<String, Long> spickWaiting;

	private final SwornRPG plugin;

	public AbilityHandler(SwornRPG plugin)
	{
		this.plugin = plugin;

		this.frenzyEnabled = plugin.getConfig().getBoolean("frenzy.enabled");
		this.frenzyDuration = plugin.getConfig().getInt("frenzy.baseDuration");
		this.frenzyMultiplier = plugin.getConfig().getInt("frenzy.levelMultiplier");
		this.frenzyCooldown = plugin.getConfig().getInt("frenzy.baseCooldown");

		this.superPickaxeEnabled = plugin.getConfig().getBoolean("superPickaxe.enabled");
		this.superPickaxeDuration = plugin.getConfig().getInt("superPickaxe.baseDuration");
		this.superPickaxeMultiplier = plugin.getConfig().getInt("superPickaxe.levelMultiplier");
		this.superPickaxeCooldown = plugin.getConfig().getInt("superPickaxe.baseCooldown");

		this.unlimitedAmmoEnabled = plugin.getConfig().getBoolean("unlimitedAmmo.enabled");
		this.unlimitedAmmoDuration = plugin.getConfig().getInt("unlimitedAmmo.baseDuration");
		this.unlimitedAmmoMultiplier = plugin.getConfig().getInt("unlimitedAmmo.levelMultiplier");
		this.unlimitedAmmoCooldown = plugin.getConfig().getInt("unlimitedAmmo.baseCooldown");

		this.frenzyWaiting = new HashMap<String, Long>();
		this.spickWaiting = new HashMap<String, Long>();
	}

	/**
	 * Activates Frenzy for a player
	 * 
	 * @param player
	 *            - {@link Player} to activate Frenzy for
	 * @param command
	 *            - Whether or not it was activated via command
	 * @param actions
	 *            - {@link Action} taken
	 */
	public void activateFrenzy(final Player player, boolean command, Action... actions)
	{
		/** Enable Check **/
		if (! frenzyEnabled)
		{
			if (command)
				sendpMessage(player, plugin.getMessage("command_disabled"));
			return;
		}

		/** Disabled World Check **/
		if (plugin.isDisabledWorld(player))
		{
			if (command)
				sendpMessage(player, plugin.getMessage("disabled_world"));
			return;
		}

		/** GameMode check **/
		if (player.getGameMode() == GameMode.CREATIVE)
		{
			if (command)
				sendpMessage(player, plugin.getMessage("creative_ability"));
			return;
		}

		/** Check for Frenzy In Progress **/
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isFrenzyEnabled())
		{
			if (command)
				sendpMessage(player, plugin.getMessage("ability_in_progress"), "Frenzy Mode");
			return;
		}

		/** Action Check **/
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

		/** The player did not left click **/
		if (! activate)
		{
			if (! frenzyWaiting.containsKey(player.getName()))
			{
				String inHand = FormatUtil.getFriendlyName(player.getItemInHand().getType());
				sendpMessage(player, plugin.getMessage("ability_ready"), inHand);
				frenzyWaiting.put(player.getName(), System.currentTimeMillis());
				new FrenzyRemoveTask(player).runTaskLater(plugin, 60);
			}

			return;
		}

		/** The player is activating using an item **/
		if (! command)
		{
			/** Check if the player is on the waiting list **/
			if (frenzyWaiting.containsKey(player.getName()))
			{
				/** Cooldown Check **/
				if (data.isFrenzyCooldownEnabled())
				{
					sendpMessage(player, plugin.getMessage("frenzy_cooldown"), (data.getFrenzyCooldownTime()));
					frenzyWaiting.remove(player.getName());
					return;
				}

				frenzyWaiting.remove(player.getName());
			}
			else
			{
				return;
			}
		}
		else
		{
			if (data.isFrenzyCooldownEnabled())
			{
				sendpMessage(player, plugin.getMessage("frenzy_cooldown"), (data.getFrenzyCooldownTime()));
				return;
			}
		}

		int level = data.getLevel();
		if (level == 0)
			level = 1;

		final int duration = Integer.valueOf(Math.round(20 * (frenzyDuration + (level * frenzyMultiplier))));

		FrenzyActivateEvent event = new FrenzyActivateEvent(player, duration, command);
		plugin.getPluginManager().callEvent(event);

		if (event.isCancelled())
			return;

		sendpMessage(player, plugin.getMessage("frenzy_enter"));
		data.setFrenzyEnabled(true);

		List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();

		String[] types = new String[] { "SPEED", "INCREASE_DAMAGE", "REGENERATION", "JUMP", "FIRE_RESISTANCE", "DAMAGE_RESISTANCE" };
		for (String type : types)
		{
			potionEffects.add(new PotionEffect(PotionEffectType.getByName(type), duration, 1));
		}

		player.addPotionEffects(potionEffects);

		plugin.debug(plugin.getMessage("log_frenzy_activate"), player.getName(), duration);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				sendpMessage(player, plugin.getMessage("frenzy_wearoff"));
				data.setFrenzyEnabled(false);

				int cooldown = duration * frenzyCooldown;
				data.setFrenzyCooldownTime(cooldown);
				data.setFrenzyCooldownEnabled(true);

				plugin.debug(plugin.getMessage("log_frenzy_cooldown"), player.getName(), cooldown);
			}
		}.runTaskLater(plugin, duration);
	}

	/**
	 * Activates Super Pickaxe for a player
	 * 
	 * @param player
	 *            - {@link Player} to activate Super Pickaxe for
	 * @param command
	 *            - Whether or not it was activated via command
	 * @param actions
	 *            - {@link Action} taken
	 */
	public void activateSuperPickaxe(final Player player, boolean command, Action... actions)
	{
		/** Enable Check **/
		if (! superPickaxeEnabled)
		{
			if (command)
				sendpMessage(player, plugin.getMessage("command_disabled"));
			return;
		}

		/** Disabled World Check **/
		if (plugin.isDisabledWorld(player))
		{
			if (command)
				sendpMessage(player, plugin.getMessage("disabled_world"));
			return;
		}

		/** GameMode check **/
		if (player.getGameMode() == GameMode.CREATIVE)
		{
			if (command)
				sendpMessage(player, plugin.getMessage("creative_ability"));
			return;
		}

		/** If the player is using SuperPick, return **/
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isSuperPickaxeEnabled())
		{
			if (command)
				sendpMessage(player, plugin.getMessage("ability_in_progress"), "Super Pickaxe");
			return;
		}

		/** Check the Action **/
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

		/** The player did not left click **/
		if (! activate)
		{
			if (! spickWaiting.containsKey(player.getName()))
			{
				String inhand = FormatUtil.getFriendlyName(player.getItemInHand().getType());
				sendpMessage(player, plugin.getMessage("ability_ready"), inhand);
				spickWaiting.put(player.getName(), System.currentTimeMillis());
				new SuperPickaxeRemoveTask(player).runTaskLater(plugin, 60);
			}

			return;
		}

		/** The player is activating using an item **/
		if (! command)
		{
			/** Check if the player is on the waiting list **/
			if (spickWaiting.containsKey(player.getName()))
			{
				/** Cooldown Check **/
				if (data.isSuperPickaxeCooldownEnabled())
				{
					sendpMessage(player, plugin.getMessage("superpick_cooldown"), (data.getSuperPickaxeCooldownTime()));
					spickWaiting.remove(player.getName());
					return;
				}

				spickWaiting.remove(player.getName());
			}
			else
			{
				return;
			}
		}
		else
		{
			if (data.isSuperPickaxeCooldownEnabled())
			{
				sendpMessage(player, plugin.getMessage("superpick_cooldown"), (data.getSuperPickaxeCooldownTime()));
				return;
			}
		}

		int level = data.getLevel();
		if (level == 0)
			level = 1;

		final int duration = Integer.valueOf(Math.round(20 * (superPickaxeDuration + (level * superPickaxeMultiplier))));

		SuperPickaxeActivateEvent event = new SuperPickaxeActivateEvent(player, duration, command);
		plugin.getPluginManager().callEvent(event);

		if (event.isCancelled())
			return;

		sendpMessage(player, plugin.getMessage("superpick_activated"));
		data.setSuperPickaxeEnabled(true);

		player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, 1), true);

		plugin.debug(plugin.getMessage("log_superpick_activate"), player.getName(), duration);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				sendpMessage(player, plugin.getMessage("superpick_wearoff"));
				data.setSuperPickaxeEnabled(false);

				player.removePotionEffect(PotionEffectType.FAST_DIGGING);

				int cooldown = duration * superPickaxeCooldown;
				data.setSuperPickaxeCooldownEnabled(true);
				data.setSuperPickaxeCooldownTime(cooldown);

				plugin.debug(plugin.getMessage("log_superpick_cooldown"), player.getName(), cooldown);
			}
		}.runTaskLater(plugin, duration);
	}

	/**
	 * Activates Unlimited Ammo ability
	 * 
	 * @param player
	 *            - {@link Player} to activate it for
	 */
	public void activateAmmo(final Player player)
	{
		/** SwornGuns Enable Check **/
		PluginManager pm = plugin.getServer().getPluginManager();
		if (! pm.isPluginEnabled("SwornGuns"))
		{
			sendpMessage(player, plugin.getMessage("plugin_not_found"), "SwornGuns");
			return;
		}

		/** Enable Check **/
		if (! unlimitedAmmoEnabled)
		{
			sendpMessage(player, plugin.getMessage("command_disabled"));
			return;
		}

		/** Disabled World Check **/
		if (plugin.isDisabledWorld(player))
		{
			sendpMessage(player, plugin.getMessage("disabled_world"));
			return;
		}

		/** Check to see if they are already using Unlimited Ammo **/
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isUnlimitedAmmoEnabled())
		{
			sendpMessage(player, plugin.getMessage("ability_in_progress"), "Unlimited Ammo");
			return;
		}

		/** Cooldown Check **/
		if (data.isUnlimitedAmmoCooldownEnabled())
		{
			sendpMessage(player, plugin.getMessage("ammo_cooldown"), data.getSuperPickaxeCooldownTime());
			return;
		}

		int level = data.getLevel();
		if (level == 0)
			level = 1;

		final int duration = Integer.valueOf(Math.round(20 * (unlimitedAmmoDuration + (level * unlimitedAmmoMultiplier))));

		UnlimitedAmmoActivateEvent event = new UnlimitedAmmoActivateEvent(player, duration);
		plugin.getPluginManager().callEvent(event);

		if (event.isCancelled())
			return;

		sendpMessage(player, plugin.getMessage("ammo_now_unlimited"));
		data.setUnlimitedAmmoEnabled(true);

		plugin.debug(plugin.getMessage("log_ammo_activate"), player.getName(), duration);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				sendpMessage(player, plugin.getMessage("ammo_nolonger_unlimited"));
				data.setUnlimitedAmmoCooldownEnabled(false);

				int cooldown = duration * unlimitedAmmoCooldown;
				data.setUnlimitedAmmoCooldownEnabled(true);
				data.setUnlimitedAmmoCooldownTime(cooldown);

				plugin.debug(plugin.getMessage("log_ammo_cooldown"), player.getName(), cooldown);
			}
		}.runTaskLater(plugin, duration);
	}

	/** Send prefixed message **/
	protected final void sendpMessage(Player player, String msg, Object... args)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(msg, args));
	}

	/**
	 * If the player has been on the frenzy waiting list for more than 3
	 * seconds, remove them from the list
	 */
	public class FrenzyRemoveTask extends BukkitRunnable
	{
		private final Player player;
		private final ItemStack hand;

		public FrenzyRemoveTask(Player player)
		{
			this.player = player;
			this.hand = player.getItemInHand();
		}

		@Override
		public void run()
		{
			if (frenzyWaiting.containsKey(player.getName()))
			{
				long value = frenzyWaiting.get(player.getName()).longValue();
				long current = System.currentTimeMillis();
				if (current - value >= 60)
				{
					frenzyWaiting.remove(player.getName());
					if (player.isOnline())
					{
						String inHand = FormatUtil.getFriendlyName(hand.getType());
						player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("lower_item"), inHand));
					}
				}
			}
		}
	}

	/**
	 * If the player has been on the spick waiting list for more than 3 seconds,
	 * remove them
	 */
	public class SuperPickaxeRemoveTask extends BukkitRunnable
	{
		private final Player player;
		private final ItemStack hand;

		public SuperPickaxeRemoveTask(Player player)
		{
			this.player = player;
			this.hand = player.getItemInHand();
		}

		@Override
		public void run()
		{
			if (spickWaiting.containsKey(player.getName()))
			{
				long value = spickWaiting.get(player.getName());
				long current = System.currentTimeMillis();
				if (current - value >= 60)
				{
					spickWaiting.remove(player.getName());
					if (player.isOnline())
					{
						String inHand = FormatUtil.getFriendlyName(hand.getType());
						player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("lower_item"), inHand));
					}
				}
			}
		}
	}

	public int getFrenzyDuration(PlayerData data)
	{
		return frenzyDuration + (data.getLevel() * frenzyMultiplier);
	}

	public int getSuperPickaxeDuration(PlayerData data)
	{
		return superPickaxeDuration + (data.getLevel() + superPickaxeMultiplier);
	}

	public int getUnlimitedAmmoCooldown(PlayerData data)
	{
		return unlimitedAmmoDuration + (data.getLevel() + unlimitedAmmoMultiplier);
	}
}