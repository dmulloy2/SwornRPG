package net.dmulloy2.swornrpg.permissions;


/**
 * @author dmulloy2
 */

public enum PermissionType 
{
	CMD_ACHAT("adminchat"),
	CMD_HAT("hat"),
	CMD_RIDE("ride"),
	CMD_ASAY("asay"),
	CMD_COUNCIL("council"),
	CMD_TAG_RESET("tag.reset"),
	CMD_TAG("tag"),
	CMD_TAG_OTHERS("tag.others"),
	CMD_LEVEL_RESET("level.reset"),
	CMD_INAME("iname"),
	CMD_MATCH("match"),
	CMD_ADDXP("addxp"), 
	CMD_RELOAD("reload"),
	
	UPDATE_NOTIFY("update");
	
	public final Permission permission;
	
	PermissionType(final String node) 
	{
		permission = new Permission(node);
	}
}
