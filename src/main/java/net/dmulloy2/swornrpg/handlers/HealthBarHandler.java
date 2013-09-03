package net.dmulloy2.swornrpg.handlers;

import java.util.Arrays;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Handles player and mob health bars
 * 
 * @author dmulloy2
 */

public class HealthBarHandler
{
	private final SwornRPG plugin;
	public HealthBarHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
		
		this.setupScoreboard();
	}
	
	public void setupScoreboard()
	{
		if (checkEnabled())
		{
			unregisterAll();
			
			generateTeams();
			
			standardizeInvisibilities();
			
			setupObjective();
		}
	}
	
	public boolean checkEnabled()
	{
		boolean isEnabled = plugin.getConfig().getBoolean("playerhealth");
		
		if (! isEnabled)
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
		
		return isEnabled;
	}

	public void generateTeams()
	{
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();
		
		String h = "\u2764"; // heart character
		ChatColor red = ChatColor.RED;
		ChatColor yellow = ChatColor.YELLOW;
		ChatColor green = ChatColor.GREEN;
		
		board.registerNewTeam("health0").setSuffix("");
	    board.registerNewTeam("health1").setSuffix(red + h);
	    board.registerNewTeam("health2").setSuffix(red + h + h);
	    board.registerNewTeam("health3").setSuffix(red + h + h + h);
	    board.registerNewTeam("health4").setSuffix(yellow + h + h + h + h);
	    board.registerNewTeam("health5").setSuffix(yellow + h + h + h + h + h);
	    board.registerNewTeam("health6").setSuffix(yellow + h + h + h + h + h + h);
	    board.registerNewTeam("health7").setSuffix(yellow + h + h + h + h + h + h + h);
	    board.registerNewTeam("health8").setSuffix(green + h + h + h + h + h + h + h + h);
	    board.registerNewTeam("health9").setSuffix(green + h + h + h + h + h + h + h + h + h);
	    board.registerNewTeam("health10").setSuffix(green + h + h + h + h + h + h + h + h + h + h);
	}
	
	
	public void unregisterAll()
	{
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();
		
		for (Team team : board.getTeams())
		{
			if (team.getName().contains("health"))
				team.unregister();
		}
	}
	
	public void standardizeInvisibilities()
	{
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();
		
		for (Team team : board.getTeams())
		{
			if (team.getName().contains("health"))
				team.setCanSeeFriendlyInvisibles(false);
		}
	}
	
	public void setupObjective()
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
	
	public void updateHealth(LivingEntity entity)
	{
		if (entity instanceof Player)
		{
			updatePlayerHealth((Player)entity);
		}
		else
		{
			updateEntityHealth(entity);
		}
	}
	
	private void updatePlayerHealth(Player player)
	{
		Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();
		
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
		
		player.setScoreboard(board);
	}

	private void updateEntityHealth(LivingEntity entity)
	{
		if (plugin.isHealthtags())
		{
			LivingEntity lentity = (LivingEntity)entity;
			
			List<EntityType> blockedTypes = Arrays.asList(new EntityType[] { EntityType.VILLAGER, 
					EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.HORSE });
			
			if (blockedTypes.contains(entity.getType()))
			{
				if (! lentity.getCustomName().isEmpty())
				{
					if (lentity.getCustomName().contains("\u2764"))
					{
						lentity.setCustomNameVisible(false);
						lentity.setCustomName("");
					}
					else
					{
						lentity.setCustomNameVisible(true);
					}
				}
				
				return;
			}
			
			int health = (int) Math.round(lentity.getHealth() / 2);
			int maxhealth = (int) Math.round(lentity.getMaxHealth() / 2);
			int hearts = Math.round((health * 10) / maxhealth);
			
			if (health == maxhealth)
			{
				lentity.setCustomNameVisible(false);
				return;
			}
			
			StringBuilder tag = new StringBuilder();
			for (int i=0; i<hearts; i++)
			{
				tag.append("\u2764");
			}

			String displayName = tag.toString();
			
			// Determine Colour
			ChatColor color = null;
			if (hearts >= 8) //health 8, 9, or full
				color = ChatColor.GREEN;
			else if (hearts <= 7 && health > 3) //health 4, 5, 6, or 7
				color = ChatColor.YELLOW;
			else if (hearts <= 3) //health 1, 2, or 3
				color = ChatColor.RED;
			else //health null? (default to yellow, white hearts are ugly)
				color = ChatColor.YELLOW;
			    
			lentity.setCustomNameVisible(true);
			lentity.setCustomName(color + displayName);
		}
	}
}

/*
public class HealthBarHandler
{
	private final SwornRPG plugin;
	public HealthBarHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
	}
	
	private HashMap<String, Scoreboard> boards = new HashMap<String, Scoreboard>();

	public void register(Player player)
	{
		ScoreboardManager manager = plugin.getServer().getScoreboardManager();

		if (player.getHealth() > 0)
			try 
		{
			Scoreboard board = manager.getNewScoreboard();
			Set<Team> teams = board.getTeams();
			for (Team team : teams)
			{
				if (team.getName().contains("health"))
					team.unregister();
			}
			
			if (board.getObjective(DisplaySlot.BELOW_NAME) != null)
				board.getObjective(DisplaySlot.BELOW_NAME).unregister();
			if (board.getObjective("showhealth") != null)
				board.getObjective("showhealth").unregister();
			
			Objective objective = board.registerNewObjective("showhealth", "health");
			objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
			
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

			final int health = (int) Math.round(player.getHealth() / 2);
			Team oldteam = board.getPlayerTeam(player);
			if (oldteam != null)
				oldteam.removePlayer(player);
			board.getTeam("health"+Integer.toString(health)).addPlayer(player);
			Score score = objective.getScore(player);
			score.setScore((int) player.getHealth());
			objective.setDisplayName(board.getPlayerTeam(player).getDisplayName());
			
			boards.put(player.getName(), board);
			player.setScoreboard(board);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, plugin.getMessage("log_health_error"), e.getMessage());
		}
	}
	
	public void updateHealth(Player player)
	{
		if (! plugin.isPlayerhealth())
		{
			Scoreboard board = player.getScoreboard();
			if (board == null) return;
			if (board.getObjective(DisplaySlot.BELOW_NAME) != null)
				board.getObjective(DisplaySlot.BELOW_NAME).unregister();
			if (board.getObjective("showhealth") != null)
				board.getObjective("showhealth").unregister();
			
			Set<Team> teams = board.getTeams();
			for (Team team : teams)
			{
				if (team.getName().contains("health"))
					team.unregister();
			}
			return;
		}
		
		if (player.getHealth() == 0) return;
		
		if (!boards.containsKey(player.getName()))
			register(player);
		
		Scoreboard board = boards.get(player.getName());
		Objective objective = board.getObjective(DisplaySlot.BELOW_NAME);
		Score score = objective.getScore(player);
		score.setScore((int) player.getHealth());
		
		final int health = (int) Math.round(player.getHealth() / 2);
		
		Team oldteam = board.getPlayerTeam(player);
		if (oldteam != null)
			oldteam.removePlayer(player);
		
		Team newTeam = board.getTeam("health"+health);
		if (newTeam != null)
			newTeam.addPlayer(player);

		objective.setDisplayName(board.getPlayerTeam(player).getDisplayName());
	}
	
	public void clear()
	{
		boards.clear();
	}
	
	public void unregister(Player player)
	{
		if (boards.containsKey(player.getName()))
			boards.remove(player.getName());
	}
}*/