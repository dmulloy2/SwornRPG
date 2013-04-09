package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;

public class CmdLeaderboard extends SwornRPGCommand
{
	public CmdLeaderboard (SwornRPG plugin)
	{
		super(plugin);
		this.name = "top";
		this.description = "Leaderboard";
		this.aliases.add("lb");
		this.mustBePlayer = true;
		this.usesPrefix = true;
	}
	
	@Override
	public void perform()
	{
//		Map<String, PlayerData> data = plugin.getPlayerDataCache().getAllPlayerData();	
	}
}