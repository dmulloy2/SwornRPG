/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.swornrpg.integration;

import java.util.logging.Level;

import net.dmulloy2.integration.DependencyProvider;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

/**
 * @author dmulloy2
 */

public class EssentialsHandler extends DependencyProvider<Essentials>
{
	public EssentialsHandler(SwornRPG plugin)
	{
		super(plugin, "Essentials");
	}

	public final boolean sendMail(Player player, String mail)
	{
		if (! isEnabled())
			return false;

		try
		{
			User user = getDependency().getUser(player);
			user.addMail(mail);
			return true;
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING,
					Util.getUsefulStack(ex, String.format("sendMail(%s, %s)", player.getName(), mail)));
		}

		return false;
	}
}