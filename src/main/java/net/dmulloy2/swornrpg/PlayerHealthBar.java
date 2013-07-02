package net.dmulloy2.swornrpg;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

/**
 * @author dmulloy2
 */

public class PlayerHealthBar 
{
	public SwornRPG plugin;
	public PlayerHealthBar(SwornRPG plugin)
	{
		this.plugin = plugin;
	}
	
	private HashMap<String, Scoreboard> boards = new HashMap<String, Scoreboard>();

	public void register(Player player) throws Exception
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
	
	public void updateHealth(Player player) throws Exception
	{
		if (plugin.playerhealth == false)
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
}