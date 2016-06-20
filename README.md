![](http://shadowvolt.com/media/SwornRPGBanner.jpg)
## SwornRPG
SwornRPG is a lightweight and fully-featured alternative to mcMMO. It is based upon a leveling system in which daily tasks such as farming, fighting players and mobs, enchanting, taming, and being on the server yield experience. As with any good leveling system, SwornRPG awards players with items and money (both of which are configurable) each time they gain enough experience to level up! Players also receive increased ability times and greater chances for many of SwornRPG's extra features! Players can check their xp and level using ````/level```` and check the leaderboard with ````/srpg top````.

**Thanks for over 12,000 downloads!!!** 

----

### Official Servers
_Try out SwornRPG here_

![http://shadowvolt.com/](http://shadowvolt.com/images/OfficialGIF.gif)
![http://minesworn.com/](http://minesworn.com/images/Minestatussworn.gif)

----

### Commands
| Command | Description | Permission |
|---------|-------------|------------|
|/srpg|SwornRPG root command||
|/a|Admin only chat|srpg.adminchat|
|/hc|High Council chat|srpg.council|
|/ride|Ride another player|srpg.ride|
|/unride|Get off a player|srpg.ride|
|/eject|Kick a player off your head|srpg.ride|
|/asay|Send a colored message to the server|srpg.asay|
|/iname|Set the name of the item in your hand|srpg.iname|
|/hat|Puts the item in your hand on your head|srpg.hat|
|/iname|Set the name of your in-hand item|srpg.iname|
|/levelr|Reset a player's level|srpg.levelr|
|/abilities|Check ability levels||
|/frenzy|Level based strength ability||
|/mine|Super pickaxe based on level||
|/ammo|Unlimited ammo based on level (Requires SwornGuns)||
|/level|Check your level||
|/match|Match a string with a player||
|/staff|List online staff with permission srpg.staff||
|Marriage|See [here](http://dev.bukkit.org/bukkit-plugins/swornrpg/pages/marriage/)|
----

### Salvaging
This plugin allows for the salvaging of used armor. Salvaging the used armor gives the player SOME of the ingots back, but it is designed to be inefficient. For example: If you salvage a full set of unused diamond armor, you will only get 20 diamonds back (using the config defaults). If the armor is used, the plugin calculates how many ingots the player should receive based on what you defined in the configuration.

----

### Other Features
* Protects Iron doors from being directly broken
* Certain blocks have a random chance of dropping random materials (Completely configurable)
* Arrows have a random chance of setting the player on fire
* Axes have a random chance of blowing the player back
* Players can sit on chairs and slabs
* Players are sent a mail message with death coordinates and time on death
* Mobs and players display their health above their head
* Admin and council chat
* Players can ride other players (with srpg.ride)
* Players can recycle blocks (random chance)
* Players will get a little speed boost randomly
* Players have a random chance of not taking fall damage

----

### Permissions
| Permission | Description | Inheritance |
|------------|-------------|-------------|
|srpg.*|Allows access to all SwornRPG features!|All|
|srpg.moderator|Allows access to features meant for moderators|Guard|
|srpg.guard|Allows access to features meant for guards/lower staff|Member|
_Individual nodes can be found [here](https://github.com/dmulloy2/SwornRPG/blob/master/src/main/resources/plugin.yml)_

----

### Configuration
* A detailed configuration guide can be found [here](http://dev.bukkit.org/bukkit-plugins/swornrpg/pages/sworn-rpg-configuration/)
* The default configuration can be found [here](https://github.com/dmulloy2/SwornRPG/blob/master/src/main/resources/config.yml)

----

### Soft Dependencies
_These plugins are not required but add some functionality to SwornRPG._

| Name | Functionality | Link |
|------|---------------|------|
| SwornGuns | Unlimited ammo ability | [Link](http://ci.dmulloy2.net/job/SwornGuns/) |
| Essentials | Mail messages with death coordinates | [Link](http://dev.bukkit.org/bukkit-plugins/essentials/) |
| Vault | Cash rewards on level-up | [Link](http://dev.bukkit.org/bukkit-plugins/vault/) |
| SwornNations* | SafeZone and WarZone checks | [Link](http://ci.dmulloy2.net/job/SwornNations/) 

*_Also works with Factions 1.6.x_

----

### Development Builds
_Development Builds can be found at our Continuous Integration server. These builds are not approved by the BukkitDev staff and are to be used at your own risk_

[http://ci.dmulloy2/job/SwornRPG/](http://ci.dmulloy2/job/SwornRPG/)

----

### Reporting Issues
When reporting bugs (in a ticket), please follow this template:
* Provide any relevant stack traces (Use pastie or pastebin)
* Provide the version of both CraftBukkit/Spigot and SwornRPG (found with /version and /srpg v, respectively)
* Provide a detailed description of the problem and how it came about, as well as all (if any) steps taken to remedy the problem

#### Notes
* Support will not be given for either of the following: Offline Servers and Servers running ancient versions of either Bukkit/Spigot or Java.
* SwornRPG has been fully tested (as well as built with) with the latest builds of both Java and Bukkit.

----