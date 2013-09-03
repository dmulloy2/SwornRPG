package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdTag extends SwornRPGCommand
{
	public CmdTag(SwornRPG plugin)
	{
		super(plugin);
		this.name = "tag";
		this.aliases.add("settag");
		this.description = "Change the name above your head";
		this.requiredArgs.add("tag");
		this.optionalArgs.add("player");
		this.permission = Permission.CMD_TAG;
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (! plugin.getPluginManager().isPluginEnabled("TagAPI"))
		{
			err(plugin.getMessage("plugin_not_found"), "TagAPI"); 
			plugin.debug(plugin.getMessage("log_tagapi_null"));
			return;
		}
			
		if (args.length == 2) 
		{
			if (! plugin.getPermissionHandler().hasPermission(sender, Permission.CMD_TAG_OTHERS))
			{
				sendMessage(plugin.getMessage("noperm"));
				return;
			}
			
			if (! isValidTag(args[1]))
			{
				invalidArgs();
				return;
			}
			
			Player target = Util.matchPlayer(args[0]);
			if (target == null)
			{
				err(getMessage("noplayer"));
				return;
			}
			
			String newTag = args[1] + target.getName();
			
			plugin.getTagHandler().addTagChange(target, newTag);
			
			sendpMessage(plugin.getMessage("tag_changed_changer"), newTag);
			sendMessageTarget(plugin.getMessage("tag_changed_changed"), target, newTag);
		}
		else if (args.length == 1)
		{
			if (! isValidTag(args[0]))
			{
				invalidArgs();
				return;
			}
			
			plugin.getTagHandler().addTagChange(player, args[0] + sender.getName());
			sendpMessage(plugin.getMessage("tag_changed_self"), (args[0] + player.getName()));
		}
	}
	
	public boolean isValidTag(String tag)
	{
		if (tag.length() < 2)
		{
			return tag.contains("&");
		}
		
		return false;
			
	}
}