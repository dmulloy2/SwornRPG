package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdTag extends SwornRPGCommand
{
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
		if (plugin.getPluginManager().isPluginEnabled("TagAPI"))
		{
			if (args.length == 2) 
			{
				if (sender.hasPermission("srpg.othertag"))
				{
					if (args[1].length() > 2)
					{
						sendpMessage(plugin.getMessage("invalidargs") + "&c(/tag [player] + <colorcode>)");
					}
					else 
					{
						  Player target = Util.matchPlayer(args[0]);
						  plugin.addTagChange(target.getName(), args[1] + target.getName());
						  sendpMessage(plugin.getMessage("tag_changed_changer"), args[1] + target.getName());
						  sendMessageTarget(plugin.getMessage("tag_changed_changed"), target, args[1] + target.getName());
					}
				}
				else
				{
					sendMessage(plugin.getMessage("noperm"));
				}
			}
			else if (args.length == 1)
			{
				if (args[0].length() > 2)
				{
					sendpMessage(plugin.getMessage("invalidargs") + "&c(/tag <colorcode>)");
				}
				else
				{
					this.plugin.addTagChange(sender.getName(), args[0] + sender.getName());
					sendpMessage(plugin.getMessage("tag_changed_self"), (args[0] + sender.getName()));
				}
			}
		}
		else
		{
			sendpMessage(plugin.getMessage("plugin_not_found"), "TagAPI"); 
			if (plugin.debug) plugin.outConsole(plugin.getMessage("log_tagapi_null"));
		}
	}
}