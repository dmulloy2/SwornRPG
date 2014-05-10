package net.dmulloy2.swornrpg.handlers;

import java.util.Arrays;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Reloadable;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Handles player and mob health bars
 * 
 * @author dmulloy2
 */

// TODO Make this actually function
public class HealthBarHandler implements Reloadable
{
	private final Scoreboard board;
	private final SwornRPG plugin;
	public HealthBarHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.board = plugin.getServer().getScoreboardManager().getNewScoreboard();
		this.reload();
	}

	private final void setupScoreboard()
	{
		unregister();
		if (isEnabled())
		{
			generateTeams();
			standardizeInvisibilities();
			setupObjective();
		}
	}

	public final void handleJoin(final Player player)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (! isEnabled())
				{
					refreshBoard(player);
					return;
				}

				updatePlayerHealth(player);
			}
		}.runTaskLater(plugin, 120L);
	}

	public final void refreshBoard(Player player)
	{
		player.setScoreboard(board);
	}

	public final void refreshBoard()
	{
		for (Player player : plugin.getServer().getOnlinePlayers())
		{
			refreshBoard(player);
		}
	}

	// Currently bugged; do not use
	public final boolean isEnabled()
	{
//		return plugin.getConfig().getBoolean("playerHealthBars.enabled", false);
		return false;
	}

	public void generateTeams()
	{
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

		String h = "\u2764"; // heart character
		ChatColor red = ChatColor.RED;
		ChatColor yellow = ChatColor.YELLOW;
		ChatColor green = ChatColor.GREEN;

		board.registerNewTeam("health0").setDisplayName("");
		board.registerNewTeam("health1").setDisplayName(red + h);
		board.registerNewTeam("health2").setDisplayName(red + h + h);
		board.registerNewTeam("health3").setDisplayName(red + h + h + h);
		board.registerNewTeam("health4").setDisplayName(yellow + h + h + h + h);
		board.registerNewTeam("health5").setDisplayName(yellow + h + h + h + h + h);
		board.registerNewTeam("health6").setDisplayName(yellow + h + h + h + h + h + h);
		board.registerNewTeam("health7").setDisplayName(yellow + h + h + h + h + h + h + h);
		board.registerNewTeam("health8").setDisplayName(green + h + h + h + h + h + h + h + h);
		board.registerNewTeam("health9").setDisplayName(green + h + h + h + h + h + h + h + h + h);
		board.registerNewTeam("health10").setDisplayName(green + h + h + h + h + h + h + h + h + h + h);
	}

	public final void unregister()
	{
		unregisterTeams();
		unregisterObjectives();
	}

	private final void unregisterTeams()
	{
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

		for (Team team : board.getTeams())
		{
			if (team.getName().contains("health"))
				team.unregister();
		}
	}

	private final void unregisterObjectives()
	{
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

		if (board.getObjective(DisplaySlot.BELOW_NAME) != null)
		{
			board.getObjective(DisplaySlot.BELOW_NAME).unregister();
		}

		if (board.getObjective("healthBar") != null)
		{
			board.getObjective("healthBar").unregister();
		}
	}

	private final void standardizeInvisibilities()
	{
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

		for (Team team : board.getTeams())
		{
			if (team.getName().contains("health"))
				team.setCanSeeFriendlyInvisibles(false);
		}
	}

	private final void setupObjective()
	{
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

		if (board.getObjective(DisplaySlot.BELOW_NAME) != null)
		{
			board.getObjective(DisplaySlot.BELOW_NAME).unregister();
		}

		if (board.getObjective("healthBar") != null)
		{
			board.getObjective("healthBar").unregister();
		}

		Objective objective = board.registerNewObjective("healthBar", "dummy");
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
	}

	public final void updateHealth(LivingEntity entity)
	{
		if (entity instanceof Player)
		{
			updatePlayerHealth((Player) entity);
		}
		else
		{
			updateEntityHealth(entity);
		}
	}

	public final void updatePlayerHealth()
	{
		for (Player player : plugin.getServer().getOnlinePlayers())
		{
			updatePlayerHealth(player);
		}
	}

	@SuppressWarnings("deprecation")
	private final void updatePlayerHealth(Player player)
	{
		if (isEnabled())
		{
			if (board.getPlayerTeam(player) != null)
			{
				board.getPlayerTeam(player).removePlayer(player);
			}

			int teamNumber = 0;
			if (player.getHealth() > 0.0D)
				teamNumber = (int) player.getHealth() / 2;

			String team = "health" + teamNumber;

			board.getTeam(team).addPlayer(player);

			board.getObjective(DisplaySlot.BELOW_NAME).getScore(player).setScore(teamNumber);

			refreshBoard(player);
		}
	}

	private void updateEntityHealth(LivingEntity entity)
	{
		try
		{
			if (plugin.getConfig().getBoolean("mobHealthBars.enabled", true))
			{
				List<EntityType> blockedTypes = Arrays.asList(new EntityType[]
				{
						EntityType.VILLAGER, EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.HORSE
				});

				if (blockedTypes.contains(entity.getType()))
				{
					if (entity.isCustomNameVisible() && ! entity.getCustomName().isEmpty())
					{
						if (entity.getCustomName().contains("\u2764"))
						{
							entity.setCustomNameVisible(false);
							entity.setCustomName("");
						}
						else
						{
							entity.setCustomNameVisible(true);
						}
					}

					return;
				}

				int health = (int) Math.round(entity.getHealth() / 2);
				int maxhealth = (int) Math.round(entity.getMaxHealth() / 2);
				int hearts = Math.round((health * 10) / maxhealth);

				if (health == maxhealth)
				{
					entity.setCustomNameVisible(false);
					return;
				}

				StringBuilder tag = new StringBuilder();
				for (int i = 0; i < hearts; i++)
				{
					tag.append("\u2764");
				}

				String displayName = tag.toString();

				// Determine Colour
				ChatColor color = null;
				if (hearts >= 8) // health 8, 9, or full
					color = ChatColor.GREEN;
				else if (health >= 4 && hearts <= 7) // health 4, 5, 6, or 7
					color = ChatColor.YELLOW;
				else if (hearts <= 3) // health 1, 2, or 3
					color = ChatColor.RED;
				else
					// health null? (default to yellow, white hearts are ugly)
					color = ChatColor.YELLOW;

				entity.setCustomNameVisible(true);
				entity.setCustomName(color + displayName);
			}
		}
		catch (Exception e)
		{
			plugin.debug(Util.getUsefulStack(e, "updating entity health bar"));
		}
	}

	@Override
	public void reload()
	{
		setupScoreboard();
		updatePlayerHealth();
		refreshBoard();
	}
}