package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

/**
 * @author dmulloy2
 */

public class CmdTag extends SwornRPGCommand
{
	PluginManager pm = Bukkit.getPluginManager();
	public CmdTag (SwornRPG plugin)
	{
		super(plugin);
		this.name = "tag";
		this.aliases.add("settag");
		this.description = "Change the name above your head";
		this.requiredArgs.add("tag");
		this.optionalArgs.add("player");
		this.permission = PermissionType.CMD_TAG.permission;
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (pm.isPluginEnabled("TagAPI"))
		{
			if (args.length == 2) 
			{
				if (sender.hasPermission("srpg.othertag"))
				{
					if (args[1].length() > 2)
					{
						sendMessage(plugin.invalidargs + "&c(/tag [player] + <colorcode>)");
					}
					else 
					{
						  Player target = Util.matchPlayer(args[0]);
						  this.plugin.addTagChange(target.getName(), args[1] + target.getName());
						  sendpMessage("&aChanged tag for " + target.getName() + "!");
						  target.sendMessage(plugin.prefix + ChatColor.GREEN + "Your tag is now '" + args[1] + target.getName() + "'");
					}
				}
				else
				{
					sendMessage(plugin.noperm);
				}
			}
			else if (args.length == 1)
			{
				if (args[0].length() > 2)
				{
					sendMessage(plugin.invalidargs + "&c(/tag <colorcode>)");
				}
				else
				{
					this.plugin.addTagChange(sender.getName(), args[0] + sender.getName());
					sendpMessage("&aYou have successfully changed your tag to '" + args[0] + sender.getName() + "'");
				}
			}
		}
		else
		{
			sendpMessage("&cYou must have TagAPI installed to perform this command");
			plugin.outConsole("You must have TagAPI installed to use Tag related commands. http://dev.bukkit.org/server-mods/tag/");
		}
	}
}