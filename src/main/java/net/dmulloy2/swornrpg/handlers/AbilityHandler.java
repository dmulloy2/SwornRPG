package net.dmulloy2.swornrpg.handlers;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Ability;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.types.Reloadable;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.TimeUtil;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

@Getter
public class AbilityHandler implements Reloadable
{
	private boolean frenzyEnabled;
	private int frenzyDuration;
	private int frenzyLevelMultiplier;
	private int frenzyCooldownMultiplier;

	private boolean superPickaxeEnabled;
	private int superPickaxeDuration;
	private int superPickaxeLevelMultiplier;
	private int superPickaxeCooldownMultiplier;

	private boolean unlimitedAmmoEnabled;
	private int unlimitedAmmoDuration;
	private int unlimitedAmmoLevelMultiplier;
	private int unlimitedAmmoCooldownMultiplier;

	private List<String> waiting;

	private final SwornRPG plugin;
	public AbilityHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.waiting = new ArrayList<String>();
		this.reload(); // Load configuration

		new CleanupTask().runTaskTimer(plugin, 2L, 1L);
	}

	// ---- Public Use Methods ---- //

	public final void checkActivation(Player player, Action action)
	{
		Material mat = player.getItemInHand().getType();

		if (Ability.FRENZY.isValidMaterial(mat))
		{
			activateFrenzy(player, action);
		}
		else if (Ability.SUPER_PICKAXE.isValidMaterial(mat))
		{
			activateSuperPickaxe(player, action);
		}
	}

	public final void commandActivation(Player player, Ability ability)
	{
		switch (ability)
		{
			case FRENZY:
				activateFrenzy(player);
				break;
			case SUPER_PICKAXE:
				activateSuperPickaxe(player);
				break;
			case UNLIMITED_AMMO:
				activateUnlimitedAmmo(player);
				break;
		}
	}

	// ---- Internal Methods ---- //

	private final void activateFrenzy(Player player)
	{
		/** Enable Check **/
		if (! frenzyEnabled)
		{
			sendpMessage(player, "&c" + plugin.getMessage("command_disabled"));
			return;
		}

		/** Disabled World Check **/
		if (plugin.isDisabledWorld(player))
		{
			sendpMessage(player, "&c" + plugin.getMessage("disabled_world"));
			return;
		}

		/** GameMode check **/
		if (player.getGameMode() == GameMode.CREATIVE)
		{
			sendpMessage(player, "&c" + plugin.getMessage("creative_ability"));
			return;
		}

		/** Check for Frenzy In Progress **/
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isFrenzyEnabled())
		{
			sendpMessage(player, plugin.getMessage("ability_in_progress"), "Frenzy");
			return;
		}

		frenzy(player);
	}

	private final void activateFrenzy(Player player, Action action)
	{
		/** Enable Check **/
		if (! frenzyEnabled)
		{
			// sendpMessage(player, plugin.getMessage("command_disabled"));
			return;
		}

		/** Disabled World Check **/
		if (plugin.isDisabledWorld(player))
		{
			// sendpMessage(player, plugin.getMessage("disabled_world"));
			return;
		}

		/** GameMode check **/
		if (player.getGameMode() == GameMode.CREATIVE)
		{
			// sendpMessage(player, plugin.getMessage("creative_ability"));
			return;
		}

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isFrenzyWaiting())
		{
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK || action == Action.PHYSICAL)
			{
				if (data.isFrenzyEnabled())
					return;

				frenzy(player);
				return;
			}
		}
		else
		{
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
			{
				if (data.isFrenzyEnabled())
					return;

				if (data.isFrenzyCooldownEnabled())
				{
					sendpMessage(player, plugin.getMessage("frenzy_cooldown"), TimeUtil.toSeconds(data.getFrenzyCooldownTime()));
					return;
				}

				String inHand = FormatUtil.getFriendlyName(player.getItemInHand().getType());
				sendpMessage(player, plugin.getMessage("ability_ready"), inHand);

				data.setItemName(inHand);
				data.setFrenzyWaiting(true);
				data.setFrenzyReadyTime(System.currentTimeMillis());

				waiting.add(player.getName());
			}
		}
	}

	private final void frenzy(final Player player)
	{
		final PlayerData data = plugin.getPlayerDataCache().getData(player);

		final int level = data.getLevel();
		final long duration = getFrenzyDuration(level);

		sendpMessage(player, plugin.getMessage("frenzy_enter"));

		data.setFrenzyEnabled(true);
		data.setFrenzyWaiting(false);
		waiting.remove(player.getName());

		List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();

		String[] types = new String[] { "SPEED", "INCREASE_DAMAGE", "REGENERATION", "JUMP", "FIRE_RESISTANCE", "DAMAGE_RESISTANCE" };
		for (String type : types)
		{
			potionEffects.add(new PotionEffect(PotionEffectType.getByName(type), (int) duration, 1));
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
				data.setFrenzyCooldownEnabled(true);
				data.setFrenzyCooldownTime(getFrenzyCooldown(level));

				plugin.debug(plugin.getMessage("log_frenzy_cooldown"), player.getName(), TimeUtil.toSeconds(data.getFrenzyCooldownTime()));
			}
		}.runTaskLater(plugin, duration);
	}

	private final void activateSuperPickaxe(Player player)
	{
		/** Enable Check **/
		if (! superPickaxeEnabled)
		{
			sendpMessage(player, "&c" + plugin.getMessage("command_disabled"));
			return;
		}

		/** Disabled World Check **/
		if (plugin.isDisabledWorld(player))
		{
			sendpMessage(player, "&c" + plugin.getMessage("disabled_world"));
			return;
		}

		/** GameMode check **/
		if (player.getGameMode() == GameMode.CREATIVE)
		{
			sendpMessage(player, "&c" + plugin.getMessage("creative_ability"));
			return;
		}

		/** Check for Frenzy In Progress **/
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isSuperPickaxeEnabled())
		{
			sendpMessage(player, plugin.getMessage("ability_in_progress"), "Super Pickaxe");
			return;
		}

		superPickaxe(player);
	}

	private final void activateSuperPickaxe(Player player, Action action)
	{
		/** Enable Check **/
		if (! superPickaxeEnabled)
		{
			// sendpMessage(player, plugin.getMessage("command_disabled"));
			return;
		}

		/** Disabled World Check **/
		if (plugin.isDisabledWorld(player))
		{
			// sendpMessage(player, plugin.getMessage("disabled_world"));
			return;
		}

		/** GameMode check **/
		if (player.getGameMode() == GameMode.CREATIVE)
		{
			// sendpMessage(player, plugin.getMessage("creative_ability"));
			return;
		}

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isSuperPickaxeWaiting())
		{
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK)
			{
				if (data.isSuperPickaxeEnabled())
					return;

				superPickaxe(player);
				return;
			}
		}
		else
		{
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
			{
				if (data.isSuperPickaxeEnabled())
					return;

				if (data.isSuperPickaxeCooldownEnabled())
				{
					sendpMessage(player, plugin.getMessage("superpick_cooldown"), TimeUtil.toSeconds(data.getSuperPickaxeCooldownTime()));
					return;
				}

				String inHand = FormatUtil.getFriendlyName(player.getItemInHand().getType());
				sendpMessage(player, plugin.getMessage("ability_ready"), inHand);

				data.setItemName(inHand);
				data.setSuperPickaxeWaiting(true);
				data.setSuperPickaxeReadyTime(System.currentTimeMillis());

				waiting.add(player.getName());
			}
		}
	}

	private final void superPickaxe(final Player player)
	{
		final PlayerData data = plugin.getPlayerDataCache().getData(player);

		final int level = data.getLevel();
		final long duration = getSuperPickaxeDuration(level);

		sendpMessage(player, plugin.getMessage("superpick_activated"));

		data.setSuperPickaxeEnabled(true);
		data.setSuperPickaxeWaiting(false);
		waiting.remove(player.getName());

		player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, (int) duration, 1), true);

		plugin.debug(plugin.getMessage("log_superpick_activate"), player.getName(), duration);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				sendpMessage(player, plugin.getMessage("superpick_wearoff"));
				player.removePotionEffect(PotionEffectType.FAST_DIGGING);

				data.setSuperPickaxeEnabled(false);
				data.setSuperPickaxeCooldownEnabled(true);
				data.setSuperPickaxeCooldownTime(getSuperPickaxeCooldown(level));

				plugin.debug(plugin.getMessage("log_superpick_cooldown"), player.getName(),
						TimeUtil.toSeconds(data.getSuperPickaxeCooldownTime()));
			}
		}.runTaskLater(plugin, duration);
	}

	private final void activateUnlimitedAmmo(final Player player)
	{
		/** SwornGuns Enable Check **/
		PluginManager pm = plugin.getServer().getPluginManager();
		if (!pm.isPluginEnabled("SwornGuns"))
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
			sendpMessage(player, plugin.getMessage("ammo_cooldown"), TimeUtil.toSeconds(data.getUnlimitedAmmoCooldownTime()));
			return;
		}

		final int level = data.getLevel();
		final long duration = getUnlimitedAmmoDuration(level);

		sendpMessage(player, plugin.getMessage("ammo_now_unlimited"));
		data.setUnlimitedAmmoEnabled(true);

		plugin.debug(plugin.getMessage("log_ammo_activate"), player.getName(), duration);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				sendpMessage(player, plugin.getMessage("ammo_nolonger_unlimited"));

				data.setUnlimitedAmmoEnabled(false);
				data.setUnlimitedAmmoCooldownEnabled(true);
				data.setUnlimitedAmmoCooldownTime(getUnlimitedAmmoCooldown(level));

				plugin.debug(plugin.getMessage("log_ammo_cooldown"), player.getName(),
						TimeUtil.toSeconds(data.getUnlimitedAmmoCooldownTime()));
			}
		}.runTaskLater(plugin, duration);
	}

	/** Send prefixed message **/
	protected final void sendpMessage(Player player, String msg, Object... args)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(msg, args));
	}

	/** Cleanup Task **/
	public final class CleanupTask extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (String wait : Util.newList(waiting))
			{
				PlayerData data = plugin.getPlayerDataCache().getData(wait);
				if (data.isFrenzyWaiting())
				{
					plugin.debug("Checking frenzy waiting for {0}", wait);
					if ((System.currentTimeMillis() - data.getFrenzyReadyTime()) > TimeUtil.toTicks(3))
					{
						plugin.debug("(({0} - {1}) > 60L = true", System.currentTimeMillis(), data.getFrenzyReadyTime());

						Player player = Util.matchPlayer(wait);
						if (player != null)
							sendpMessage(player, "&eYou lower your &a{0}&e!", data.getItemName());

						data.setFrenzyWaiting(false);
						data.setItemName(null);

						waiting.remove(wait);
					}
				}

				if (data.isSuperPickaxeWaiting())
				{
					if ((System.currentTimeMillis() - data.getSuperPickaxeReadyTime()) > TimeUtil.toTicks(3))
					{
						Player player = Util.matchPlayer(wait);
						if (player != null)
							sendpMessage(player, "&eYou lower your &a{0}&e!", data.getItemName());

						data.setSuperPickaxeWaiting(false);
						data.setItemName(null);

						waiting.remove(wait);
					}
				}
			}
		}
	}

	// ---- Calculations ---- //

	public final long getFrenzyDuration(int level)
	{
		// return 20 * (frenzyDuration + (level * frenzyLevelMultiplier));
		return TimeUtil.toTicks(frenzyDuration + (level * frenzyLevelMultiplier));
	}

	public final long getFrenzyCooldown(int level)
	{
		// return duration * frenzyCooldownMultiplier;
		return getFrenzyDuration(level) * frenzyCooldownMultiplier;
	}

	public final long getSuperPickaxeDuration(int level)
	{
		// return 20 * (superPickaxeDuration + (level *
		// superPickaxeLevelMultiplier));
		return TimeUtil.toTicks(superPickaxeDuration + (level * superPickaxeLevelMultiplier));
	}

	public final long getSuperPickaxeCooldown(int level)
	{
		// return duration * superPickaxeCooldownMultiplier;
		return getSuperPickaxeDuration(level) * superPickaxeCooldownMultiplier;
	}

	public final long getUnlimitedAmmoDuration(int level)
	{
		// return 20 * (unlimitedAmmoDuration + (level *
		// unlimitedAmmoLevelMultiplier));
		return TimeUtil.toTicks(unlimitedAmmoDuration + (level * unlimitedAmmoLevelMultiplier));
	}

	public final long getUnlimitedAmmoCooldown(int level)
	{
		// return duration * unlimitedAmmoCooldownMultiplier;
		return getUnlimitedAmmoDuration(level) * unlimitedAmmoCooldownMultiplier;
	}

	@Override
	public void reload()
	{
		this.frenzyEnabled = plugin.getConfig().getBoolean("frenzy.enabled");
		this.frenzyDuration = plugin.getConfig().getInt("frenzy.baseDuration");
		this.frenzyLevelMultiplier = plugin.getConfig().getInt("frenzy.levelMultiplier");
		this.frenzyCooldownMultiplier = plugin.getConfig().getInt("frenzy.cooldownMultiplier");

		this.superPickaxeEnabled = plugin.getConfig().getBoolean("superPickaxe.enabled");
		this.superPickaxeDuration = plugin.getConfig().getInt("superPickaxe.baseDuration");
		this.superPickaxeLevelMultiplier = plugin.getConfig().getInt("superPickaxe.levelMultiplier");
		this.superPickaxeCooldownMultiplier = plugin.getConfig().getInt("superPickaxe.cooldownMultiplier");

		this.unlimitedAmmoEnabled = plugin.getConfig().getBoolean("unlimitedAmmo.enabled");
		this.unlimitedAmmoDuration = plugin.getConfig().getInt("unlimitedAmmo.baseDuration");
		this.unlimitedAmmoLevelMultiplier = plugin.getConfig().getInt("unlimitedAmmo.levelMultiplier");
		this.unlimitedAmmoCooldownMultiplier = plugin.getConfig().getInt("unlimitedAmmo.cooldownMultiplier");
	}
}