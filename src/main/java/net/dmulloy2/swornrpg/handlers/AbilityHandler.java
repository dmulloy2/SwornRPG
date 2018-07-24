/**
 * SwornRPG - a Bukkit plugin
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.swornrpg.handlers;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Ability;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.CompatUtil;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.MaterialUtil;
import net.dmulloy2.util.TimeUtil;
import net.dmulloy2.util.Util;

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
	private List<PotionEffectType> frenzyEffects;

	private boolean superPickaxeEnabled;
	private int superPickaxeDuration;
	private int superPickaxeLevelMultiplier;
	private int superPickaxeCooldownMultiplier;

	private boolean unlimitedAmmoEnabled;
	private int unlimitedAmmoDuration;
	private int unlimitedAmmoLevelMultiplier;
	private int unlimitedAmmoCooldownMultiplier;

	private boolean reduceCooldownSpam;

	private List<String> waiting;

	private final SwornRPG plugin;
	public AbilityHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.waiting = new ArrayList<>();
		this.reload();

		// Run the clean up task every second
		new CleanupTask().runTaskTimer(plugin, TimeUtil.toTicks(3), TimeUtil.toTicks(1));
	}

	// ---- Public Use Methods

	public final void checkActivation(Player player, Action action)
	{
		Material mat = CompatUtil.getItemInMainHand(player).getType();
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
			default:
				throw new IllegalArgumentException("Unsupported ability: " + ability);
		}
	}

	// ---- Internal Methods

	private void activateFrenzy(Player player)
	{
		if (! frenzyEnabled)
		{
			sendpMessage(player, "&c" + plugin.getMessage("command_disabled"));
			return;
		}

		if (plugin.isDisabledWorld(player))
		{
			sendpMessage(player, "&c" + plugin.getMessage("disabled_world"));
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE)
		{
			sendpMessage(player, "&c" + plugin.getMessage("creative_ability"));
			return;
		}

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isFrenzyEnabled())
		{
			sendpMessage(player, plugin.getMessage("ability_in_progress"), "Frenzy");
			return;
		}

		if (data.getCooldowns().containsKey("frenzy"))
		{
			sendpMessage(player, plugin.getMessage("frenzy_cooldown"), TimeUtil.toSeconds(data.getCooldowns().get("frenzy")));
			return;
		}

		frenzy(player);
	}

	private void activateFrenzy(Player player, Action action)
	{
		if (! frenzyEnabled || plugin.isDisabledWorld(player))
			return;

		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isFrenzyWaiting())
		{
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
			{
				if (data.isFrenzyEnabled())
					return;

				frenzy(player);
			}
		}
		else
		{
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
			{
				if (data.isFrenzyEnabled())
					return;

				if (data.getCooldowns().containsKey("frenzy"))
				{
					// Reduce cooldown spam
					if (reduceCooldownSpam && data.getNextCooldownMessage() >= System.currentTimeMillis())
						return;

					sendpMessage(player, plugin.getMessage("frenzy_cooldown"), TimeUtil.toSeconds(data.getCooldowns().get("frenzy")));
					return;
				}

				String inHand = MaterialUtil.getName(CompatUtil.getItemInMainHand(player));
				sendpMessage(player, plugin.getMessage("ability_ready"), inHand);

				data.setItemName(inHand);
				data.setFrenzyWaiting(true);
				data.setFrenzyReadyTime(3); // 3 seconds

				waiting.add(player.getName());
			}
		}
	}

	private void frenzy(final Player player)
	{
		final PlayerData data = plugin.getPlayerDataCache().getData(player);

		final int level = data.getLevel();
		final long duration = getFrenzyDuration(level);

		sendpMessage(player, plugin.getMessage("frenzy_enter"));

		data.setFrenzyEnabled(true);
		data.setFrenzyWaiting(false);
		waiting.remove(player.getName());

		for (PotionEffectType type : frenzyEffects)
		{
			player.addPotionEffect(new PotionEffect(type, (int) duration, 1));
		}

		plugin.debug(plugin.getMessage("log_frenzy_activate"), player.getName(), duration);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				long cooldown = getFrenzyCooldown(level);

				sendpMessage(player, plugin.getMessage("frenzy_wearoff"), TimeUtil.toSeconds(cooldown));

				data.setFrenzyEnabled(false);
				data.getCooldowns().put("frenzy", cooldown);
				data.setNextCooldownMessage(System.currentTimeMillis() + (cooldown / 4));

				plugin.debug(plugin.getMessage("log_frenzy_cooldown"), player.getName(), TimeUtil.toSeconds(cooldown));
			}
		}.runTaskLater(plugin, duration);
	}

	private void activateSuperPickaxe(Player player)
	{
		if (! superPickaxeEnabled)
		{
			sendpMessage(player, "&c" + plugin.getMessage("command_disabled"));
			return;
		}

		if (plugin.isDisabledWorld(player))
		{
			sendpMessage(player, "&c" + plugin.getMessage("disabled_world"));
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE)
		{
			sendpMessage(player, "&c" + plugin.getMessage("creative_ability"));
			return;
		}

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isSuperPickaxeEnabled())
		{
			sendpMessage(player, plugin.getMessage("ability_in_progress"), "Super Pickaxe");
			return;
		}

		if (data.getCooldowns().containsKey("superpick"))
		{
			sendpMessage(player, plugin.getMessage("superpick_cooldown"), TimeUtil.toSeconds(data.getCooldowns().get("superpick")));
			return;
		}

		superPickaxe(player);
	}

	private void activateSuperPickaxe(Player player, Action action)
	{
		if (! superPickaxeEnabled || plugin.isDisabledWorld(player))
			return;

		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isSuperPickaxeWaiting())
		{
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
			{
				if (data.isSuperPickaxeEnabled())
					return;

				superPickaxe(player);
			}
		}
		else
		{
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
			{
				if (data.isSuperPickaxeEnabled())
					return;

				if (data.getCooldowns().containsKey("superpick"))
				{
					// Reduce cooldown spam
					if (reduceCooldownSpam && data.getNextCooldownMessage() >= System.currentTimeMillis())
						return;

					sendpMessage(player, plugin.getMessage("superpick_cooldown"), TimeUtil.toSeconds(data.getCooldowns().get("superpick")));
					return;
				}

				String inHand = MaterialUtil.getName(CompatUtil.getItemInMainHand(player));
				sendpMessage(player, plugin.getMessage("ability_ready"), inHand);

				data.setItemName(inHand);
				data.setSuperPickaxeWaiting(true);
				data.setSuperPickaxeReadyTime(3); // 3 seconds

				waiting.add(player.getName());
			}
		}
	}

	private void superPickaxe(final Player player)
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
				long cooldown = getSuperPickaxeCooldown(level);

				sendpMessage(player, plugin.getMessage("superpick_wearoff"), TimeUtil.toSeconds(cooldown));
				player.removePotionEffect(PotionEffectType.FAST_DIGGING);

				data.setSuperPickaxeEnabled(false);
				data.getCooldowns().put("superpick", cooldown);
				data.setNextCooldownMessage(System.currentTimeMillis() + (cooldown / 4));

				plugin.debug(plugin.getMessage("log_superpick_cooldown"), player.getName(), TimeUtil.toSeconds(cooldown));
			}
		}.runTaskLater(plugin, duration);
	}

	private void activateUnlimitedAmmo(final Player player)
	{
		PluginManager pm = plugin.getServer().getPluginManager();
		if (! pm.isPluginEnabled("SwornGuns"))
		{
			sendpMessage(player, plugin.getMessage("plugin_not_found"), "SwornGuns");
			return;
		}

		if (! unlimitedAmmoEnabled)
		{
			sendpMessage(player, plugin.getMessage("command_disabled"));
			return;
		}

		if (plugin.isDisabledWorld(player))
		{
			sendpMessage(player, plugin.getMessage("disabled_world"));
			return;
		}

		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isUnlimitedAmmoEnabled())
		{
			sendpMessage(player, plugin.getMessage("ability_in_progress"), "Unlimited Ammo");
			return;
		}

		if (data.getCooldowns().containsKey("ammo"))
		{
			sendpMessage(player, plugin.getMessage("ammo_cooldown"), TimeUtil.toSeconds(data.getCooldowns().get("ammo")));
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
				long cooldown = getUnlimitedAmmoCooldown(level);

				sendpMessage(player, plugin.getMessage("ammo_nolonger_unlimited"), TimeUtil.toSeconds(cooldown));

				data.setUnlimitedAmmoEnabled(false);
				data.getCooldowns().put("ammo", cooldown);

				plugin.debug(plugin.getMessage("log_ammo_cooldown"), player.getName(), TimeUtil.toSeconds(cooldown));
			}
		}.runTaskLater(plugin, duration);
	}

	private void sendpMessage(Player player, String msg, Object... args)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(msg, args));
	}

	private class CleanupTask extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (String wait : waiting.toArray(new String[0]))
			{
				Player player = Util.matchPlayer(wait);
				if (player == null || ! player.isOnline())
				{
					waiting.remove(wait);
					continue;
				}

				PlayerData data = plugin.getPlayerDataCache().getData(player);
				if (data.isFrenzyWaiting())
				{
					data.setFrenzyReadyTime(data.getFrenzyReadyTime() - 1);
					if (data.getFrenzyReadyTime() <= 0)
					{
						sendpMessage(player, plugin.getMessage("ability_lower"), data.getItemName());

						data.setFrenzyWaiting(false);
						data.setItemName(null);

						waiting.remove(wait);
					}
				}

				if (data.isSuperPickaxeWaiting())
				{
					data.setSuperPickaxeReadyTime(data.getSuperPickaxeReadyTime() - 1);
					if (data.getSuperPickaxeReadyTime() <= 0)
					{
						sendpMessage(player, plugin.getMessage("ability_lower"), data.getItemName());

						data.setSuperPickaxeWaiting(false);
						data.setItemName(null);

						waiting.remove(wait);
					}
				}
			}
		}
	}

	// ---- Calculations

	public final long getFrenzyDuration(int level)
	{
		return TimeUtil.toTicks(frenzyDuration + (level * frenzyLevelMultiplier));
	}

	private long getFrenzyCooldown(int level)
	{
		return TimeUtil.toTicks(getFrenzyDuration(level) * frenzyCooldownMultiplier);
	}

	public final long getSuperPickaxeDuration(int level)
	{
		return TimeUtil.toTicks(superPickaxeDuration + (level * superPickaxeLevelMultiplier));
	}

	private long getSuperPickaxeCooldown(int level)
	{
		return TimeUtil.toTicks(getSuperPickaxeDuration(level) * superPickaxeCooldownMultiplier);
	}

	public final long getUnlimitedAmmoDuration(int level)
	{
		return TimeUtil.toTicks(unlimitedAmmoDuration + (level * unlimitedAmmoLevelMultiplier));
	}

	private long getUnlimitedAmmoCooldown(int level)
	{
		return TimeUtil.toTicks(getUnlimitedAmmoDuration(level) * unlimitedAmmoCooldownMultiplier);
	}

	private List<PotionEffectType> parseFrenzyEffects()
	{
		List<PotionEffectType> types = new ArrayList<>();

		for (String name : plugin.getConfig().getStringList("frenzy.effects"))
		{
			name = name.replaceAll(" ", "_").toUpperCase();
			PotionEffectType type = PotionEffectType.getByName(name);
			if (type != null)
				types.add(type);
		}

		if (types.isEmpty())
		{
			// Default types
			types.add(PotionEffectType.SPEED);
			types.add(PotionEffectType.INCREASE_DAMAGE);
			types.add(PotionEffectType.REGENERATION);
			types.add(PotionEffectType.JUMP);
			types.add(PotionEffectType.FIRE_RESISTANCE);
			types.add(PotionEffectType.DAMAGE_RESISTANCE);
		}

		return types;
	}

	@Override
	public void reload()
	{
		this.frenzyEnabled = plugin.getConfig().getBoolean("frenzy.enabled");
		this.frenzyDuration = plugin.getConfig().getInt("frenzy.baseDuration");
		this.frenzyLevelMultiplier = plugin.getConfig().getInt("frenzy.levelMultiplier");
		this.frenzyCooldownMultiplier = plugin.getConfig().getInt("frenzy.cooldownMultiplier");
		this.frenzyEffects = parseFrenzyEffects();

		this.superPickaxeEnabled = plugin.getConfig().getBoolean("superPickaxe.enabled");
		this.superPickaxeDuration = plugin.getConfig().getInt("superPickaxe.baseDuration");
		this.superPickaxeLevelMultiplier = plugin.getConfig().getInt("superPickaxe.levelMultiplier");
		this.superPickaxeCooldownMultiplier = plugin.getConfig().getInt("superPickaxe.cooldownMultiplier");

		this.unlimitedAmmoEnabled = plugin.getConfig().getBoolean("unlimitedAmmo.enabled");
		this.unlimitedAmmoDuration = plugin.getConfig().getInt("unlimitedAmmo.baseDuration");
		this.unlimitedAmmoLevelMultiplier = plugin.getConfig().getInt("unlimitedAmmo.levelMultiplier");
		this.unlimitedAmmoCooldownMultiplier = plugin.getConfig().getInt("unlimitedAmmo.cooldownMultiplier");

		this.reduceCooldownSpam = plugin.getConfig().getBoolean("reduceCooldownSpam", true);
	}
}
