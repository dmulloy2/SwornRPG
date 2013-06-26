package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import net.dmulloy2.swornrpg.SwornRPG;

public class CmdHelp extends SwornRPGCommand
{
	public CmdHelp(SwornRPG plugin)
	{
		super(plugin);
		this.name = "help";
		this.aliases.add("h");
		this.description = "Display SwornRPG help";
		this.optionalArgs.add("page");
		this.usesPrefix = true;
	}	
	
	@Override
	public void perform()
	{
		if (helpPages == null) updateHelp();
		
		int page = 0;
		if (args.length > 0)
		{
			page = Integer.parseInt(args[0]);
		}
		
		if (page == 0)
			page = 1;
		
		sendMessage(plugin.getMessage("help_header"), page, helpPages.size());
		
		page -= 1;
		
		if (page < 0 || page >= helpPages.size())
		{
			err(plugin.getMessage("invalid_help_page"), args[0]);
			return;
		}
		for (String string : helpPages.get(page))
			sendMessage(string);
	}
	
	/**Build the help pages**/
	public ArrayList<ArrayList<String>> helpPages;
	
	public void updateHelp()
	{
		helpPages = new ArrayList<ArrayList<String>>();
		ArrayList<String> pageLines;

		pageLines = new ArrayList<String>();
		pageLines.add(new CmdHelp(plugin).getUsageTemplate(true));
		pageLines.add(new CmdReload(plugin).getUsageTemplate(true));
		pageLines.add(new CmdVersion(plugin).getUsageTemplate(true));
		pageLines.add(new CmdLeaderboard(plugin).getUsageTemplate(true));
		pageLines.add(new CmdAChat(plugin).getUsageTemplate(true));
		pageLines.add(new CmdAddxp(plugin).getUsageTemplate(true));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add(new CmdASay(plugin).getUsageTemplate(true));
		pageLines.add(new CmdCoordsToggle(plugin).getUsageTemplate(true));
		pageLines.add(new CmdDeny(plugin).getUsageTemplate(true));
		pageLines.add(new CmdDivorce(plugin).getUsageTemplate(true));
		pageLines.add(new CmdEject(plugin).getUsageTemplate(true));
		pageLines.add(new CmdFrenzy(plugin).getUsageTemplate(true));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add(new CmdHighCouncil(plugin).getUsageTemplate(true));
		pageLines.add(new CmdItemName(plugin).getUsageTemplate(true));
		pageLines.add(new CmdLevel(plugin).getUsageTemplate(true));
		pageLines.add(new CmdLevelr(plugin).getUsageTemplate(true));
		pageLines.add(new CmdMarry(plugin).getUsageTemplate(true));
		pageLines.add(new CmdMatch(plugin).getUsageTemplate(true));
		helpPages.add(pageLines);
	
		pageLines = new ArrayList<String>();
		pageLines.add(new CmdMine(plugin).getUsageTemplate(true));
		pageLines.add(new CmdPropose(plugin).getUsageTemplate(true));
		pageLines.add(new CmdRide(plugin).getUsageTemplate(true));
		pageLines.add(new CmdSitdown(plugin).getUsageTemplate(true));
		pageLines.add(new CmdSpouse(plugin).getUsageTemplate(true));
		pageLines.add(new CmdStaffList(plugin).getUsageTemplate(true));	
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add(new CmdStandup(plugin).getUsageTemplate(true));
		pageLines.add(new CmdTag(plugin).getUsageTemplate(true));
		pageLines.add(new CmdTagr(plugin).getUsageTemplate(true));
		pageLines.add(new CmdUnride(plugin).getUsageTemplate(true));
		pageLines.add(new CmdUnlimitedAmmo(plugin).getUsageTemplate(true));
		pageLines.add(new CmdAbilities(plugin).getUsageTemplate(true));
		helpPages.add(pageLines);
	}
}