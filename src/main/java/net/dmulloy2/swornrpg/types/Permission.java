package net.dmulloy2.swornrpg.types;

/**
 * @author dmulloy2
 */

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
	ITEMNAME,
	LEADERBOARD,
	LEVEL,
	LEVEL_OTHERS,
	LEVEL_RESET,
	MARRY,
	MATCH,
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

	Permission(boolean command)
	{
		this.node = toString().toLowerCase().replaceAll("_", ".");
		
		if (command) node = "cmd." + node;
	}

	Permission()
	{
		this(true);
	}
	
	public String getNode()
	{
		return node;
	}
}