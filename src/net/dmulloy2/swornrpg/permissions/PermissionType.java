package net.dmulloy2.swornrpg.permissions;

import net.dmulloy2.swornrpg.permissions.Permission;

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
	CMD_TAGR("tagr"),
	CMD_TAG("tag"),
	CMD_TAG_OTHERS("tag.others"),
	CMD_LEVELR("levelr"),
	CMD_INAME("iname"),
	CMD_MATCH("match"),
	CMD_ADDXP("addxp");
	
	public final Permission permission;
	
	PermissionType(final String node) 
	{
		permission = new Permission(node);
	}
}
