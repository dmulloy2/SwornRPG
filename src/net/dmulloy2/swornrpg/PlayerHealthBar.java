package net.dmulloy2.swornrpg;

import java.util.Set;
import java.util.logging.Level;

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

	public void updateHealth(Player player) throws NoSuchMethodException, IllegalStateException
	{
		ScoreboardManager manager = plugin.getServer().getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		
		if (plugin.playerhealth == false)
		{
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
		
		if (player.getHealth() > 0)
			try 
		{
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
			
			board.registerNewTeam("health0").setDisplayName("");
		    board.registerNewTeam("health1").setDisplayName("§c❤");
		    board.registerNewTeam("health2").setDisplayName("§c❤❤");
		    board.registerNewTeam("health3").setDisplayName("§c❤❤❤");
		    board.registerNewTeam("health4").setDisplayName("§e❤❤❤❤");
		    board.registerNewTeam("health5").setDisplayName("§e❤❤❤❤❤");
		    board.registerNewTeam("health6").setDisplayName("§e❤❤❤❤❤❤");
		    board.registerNewTeam("health7").setDisplayName("§e❤❤❤❤❤❤❤");
		    board.registerNewTeam("health8").setDisplayName("§a❤❤❤❤❤❤❤❤");
		    board.registerNewTeam("health9").setDisplayName("§a❤❤❤❤❤❤❤❤❤");
		    board.registerNewTeam("health10").setDisplayName("§a❤❤❤❤❤❤❤❤❤❤");

			final int health = Math.round(player.getHealth() / 2);
			Team oldteam = board.getPlayerTeam(player);
			if (oldteam != null)
				oldteam.removePlayer(player);
			board.getTeam("health"+Integer.toString(health)).addPlayer(player);
			Score score = objective.getScore(player);
			score.setScore(player.getHealth());
			objective.setDisplayName(board.getPlayerTeam(player).getDisplayName());
			
			for (Player players : plugin.getServer().getOnlinePlayers())
			{
				players.setScoreboard(board);
			}
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, plugin.getMessage("log_health_error"), e.getMessage());
		}
	}
}