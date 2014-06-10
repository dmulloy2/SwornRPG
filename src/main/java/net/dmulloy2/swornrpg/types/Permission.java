package net.dmulloy2.swornrpg.types;

import lombok.Getter;

/**
 * @author dmulloy2
 */

@Getter
public enum Permission
{
	ABILITIES,
	ABILITIES_OTHERS,
	ADDXP,
	ADMINCHAT,
	ADMINSAY,
	COORDSTOGGLE,
	DENY,
	DIVORCE,
	EJECT,
	FRENZY,
	HAT,
	HIGHCOUNCIL,
	LEADERBOARD,
	LEVEL,
	LEVEL_OTHERS,
	LEVEL_RESET,
	LORE,
	MARRY,
	NAME,
	PROPOSE,
	RELOAD,
	RIDE,
	SITDOWN,
	SPOUSE,
	SPOUSE_OTHERS,
	STAFFLIST,
	STANDUP,
	SUPERPICKAXE,
	TAG,
	TAG_OTHERS,
	TAG_RESET,
	TAG_RESET_OTHERS,
	UNLIMITEDAMMO,
	UNRIDE,
	VERSION,
	
	STAFF(false),
	UPDATE_NOTIFY(false),
	;

	private String node;
	private Permission(boolean command)
	{
		this.node = toString().toLowerCase().replaceAll("_", ".");
		
		if (command) 
			node = "cmd." + node;
	}

	private Permission()
	{
		this(true);
	}
}