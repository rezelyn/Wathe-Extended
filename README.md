<div align="center">

# Wathe: Extended

<img alt="Icon" height="384" width="384" style="border-radius: 24px; margin-bottom: 8px" src="https://raw.githubusercontent.com/rezelyn/Wathe-Extended/refs/heads/main/src/main/resources/assets/watheextended/icon.png"/>

---

![GitHub](https://img.shields.io/github/stars/rezelyn/Wathe-Extended?style=for-the-badge&logo=github&labelColor=11111b&color=FFFFFF&label=GitHub) ![Issues](https://img.shields.io/github/issues/rezelyn/Wathe-Extended?style=for-the-badge&labelColor=11111b&color=F08A3A&label=Issues) ![License](https://img.shields.io/github/license/rezelyn/Wathe-Extended?style=for-the-badge&labelColor=11111b&color=3094FF&label=License)
<br> ![Version](https://img.shields.io/github/release/rezelyn/Wathe-Extended?style=flat-square&logo=git&labelColor=11111b&color=f03c2e&label=Version) ![Modrinth](https://img.shields.io/modrinth/dt/7LadFLDS?style=flat-square&logo=modrinth&labelColor=11111b&color=00AF5C&label=Downloads)

---

⚠️ **Core mod of [The Harpy Express: Extended](https://modrinth.com/modpack/the-harpy-express-extended) modpack outside which, isn't intended to be played with** ⚠️
<br> Using this mod outside the modpack is not recommended; you are free to do so, but at your own risk.
<br> Support requests or issues reported by users who choose to use the mod outside the base modpack will be ignored.

## Overview

<details><summary>Roles</summary>

Soon™

</details>

<details><summary>Modifiers</summary>

|         Name         |           Side            |                                                                                                                                                                          Description                                                                                                                                                                          |
|:--------------------:|:-------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|   **Introverted**    |     Innocent/Neutral      |                                                                                               When more than X players are nearby (configurable crowd count + range), mood drains faster. When alone or with only one other player nearby, mood drains slower.                                                                                                |
|      **Taxed**       |          Killer           |                                                                                                             If a killer exceeds a configurable kill count within a rolling time window, their kill income is reduced by a configurable fraction.                                                                                                              |
|     **Adaptive**     |          Killer           |                                                                                                      Repeating the same kill method consecutively applies an income penalty. Switching to a different kill method consecutively grants an income bonus.                                                                                                       |
| **Forbidden Lovers** | Innocent + Neutral/Killer | An alternative way on how the Lovers modifier from [Stupid Express](https://modrinth.com/mod/stupid-express) will work in-game, the lovers will always consist of a civilian/neutral and a killer, the Forbidden Lovers wins by surviving together until the end of the game. Turning this option on will also decrease the chances of lovers being assigned. |

</details>

</div>

### Gameplay Balancing & Customizability
Wathe: Extended aims to improve the overall gameplay experience and accessibility of [Wathe: Murder Mystery](https://modrinth.com/mod/wathe) *(and some others add-ons)* by listening to players feedback, it has a lot of new features to make the game more balanced, enjoyable and easily customizable for any types of players:
- `Player Collisions`
  > Default Wathe forces players to collide with each other, this game rule totally disables this, letting the vanilla collisions, could be useful for custom maps.
- `Item Bounds Check`
  > Items that falls out of the play area boundaries during an active game are automatically teleported back to the nearest alive player or dead body.
- `World Protection`
  > Prevents player interaction with containers, buttons, levers, trapdoors, doors, and other interactive blocks within the map variables areas, not recommended for custom maps that might use modded blocks/containers!
- `Ability VFX/SFX Suppression`
  > A global toggle that suppresses all ability visual and sound effects when being triggered to avoid players abusing this to clear themselves.
- `Random Teleportation`
  > At game start, players in the ready area are each assigned a unique teleportation slot and teleported on it while the screen fades to black at the start of a game, making the start of the game somewhat balanced with players being spread.
- `Cleaner Ability Player Limit`
  > The ability of the **Cleaner** (from Kin's Wathe) will automatically be disabled when the number of alive survival players falls below a configurable threshold, preventing it from being overpowered in the late game when there are very few players left, ultimately soft-locking the remaining players to have a chance to win.
- `Thief Stealable Items Compatibility`
  > Allow the **Thief** (from Kin's Wathe) to steal more items: *Pan*, *Poison Injector*, *Blowgun*, *Pill*, *Delusion Vial*, *Defense Vial* and *Tape*.

### Configuration Screen
A YACL configuration screen accessible from the pause menu that can be used to directly tweak the game that splits into six categories:
  - `CLIENT`
    > Client-side only settings such as keybindings, visual effects, personal pronouns...
  - `GAME` (OP Only)
    > Extra game rules and gameplay balancing settings, such as player collisions, ability VFX/SFX suppression, roles & modifiers dividends, economy settings...
  - `MAP` (OP Only)
    > Map-specific settings such as map variables, random teleportation state & slots, item bounds check, world protection...
  - `ITEMS` (OP Only)
    > Item-specific settings like prices, cooldowns, and item-specific effects.
  - `ROLES` (OP Only)
    > Role-specific settings, control which roles are enabled/disabled, and tweak role-specific rules, abilities and more...
  - `MODIFIERS` (OP Only)
    > Modifier-specific settings, control which modifiers are enabled/disabled, and tweak modifier-specific rules, abilities and more...

### Players Pronouns System
Displayed under the players username when a nearby player looks at them at close range.
- **Players** can **set** their pronouns via the configuration screen or with the `/watheextended:pronouns set <pronouns>` command.
- **OPs** can **clear** other player's pronouns if needed, using the `/watheextended:pronouns clear <target>` command.
  > Pronouns are saved persistently in `config/watheextended/cache/pronouns.json` on the server and synced to all players via a custom network packet on join.

### Items & Blocks
- **The Guidebook**
  <br> An in-game book item that replaces Wathe's Letter item, opens a custom GUI that contains three tabs:
  - `R0LES`
    > Roles are categorized as **Civilian**, **Killer**, and **Neutral** on the left page, right page displays the selected role's description, abilities, and items which can be navigated with the next/previous buttons.
  - `MODIFIERS`
    > Modifiers are listed on the left page, right page displays the selected modifier's description and special effects.
  - `GAMES GUIDE`
    > A comprehensive guide journey, aimed for the new players that might need a little help learning the game's mechanics and features.
- **Panel block extra variants**
- **Moquette color extra variants**
- **Steel Ornament color extra variants**
- **Ish Plush**
- **Creative items:**

  |            Item            |                                                   Description                                                   | Requires OP |
  |:--------------------------:|:---------------------------------------------------------------------------------------------------------------:|:-----------:|
  | **Teleport to Ready Area** |                            Teleports players to the ready area spawn point upon use                             |     ❌      |
  |  **Teleport to Scenery**   |                              Teleports players to the scenery spawn point upon use                              |     ✅      |
  |    **Create RTP Slot**     |       Creates a random teleportation slot based on the player's current position and view pitch upon use        |     ✅      |
  |      **Trigger RTP**       | Manualy triggers the random teleportation function and randomly teleports players across all the slots upon use |     ✅      |
  |    **Add Fake Players**    |             Spawns fake players for testing, requires [Carpet](https://modrinth.com/mod/carpet) mod             |     ✅      |
  |  **Remove Fake Players**   |           Kicks all fake players for testing, requires [Carpet](https://modrinth.com/mod/carpet) mod            |     ✅      |

# Extras & Bug Fixes
On top of what the mod adds, the mod also includes a number of bug fixes for issues that exist in Wathe and some of the add-ons, improving overall stability and gameplay experience.

<details>
<summary>Bug fixes list</summary>

- `Defense Shield Hit Condition`
  > The `AllowPlayerDeath` event is now properly fired before executing a kill, fixing cases where Defense Shields were bypassed (e.g. *Pill*, *Defense Vial*, *Dream Imprint*...), this also resolves problems with role conversion, which were happening when a player got git while having an active Defense Shield, triggering the role conversion even though the player haven't been killed (e.g. **Executioner**, **Initiates**...).
- `Disabled Roles Conversion`
  > If a role conversion condition triggers (e.g. **Executioner** succeeding, **Vulture** eating enough bodies, etc...), the affected player will now never convert to a role that is disabled in the current configuration the round is running on.
- `Duplicate Revolver Pickup`
  > Players could previously pick up extra *Revolvers* by standing on one on the ground while holding a second one with their cursor in their inventory.
- `Mood Component NBT Persistence`
  > `nextTaskTimer` and `timesGotten` task data are now properly serialized/deserialized in NBT, preventing task progress/mood from being lost and reset when reconnecting during an active game.
- `Clear Every Effects on Game Stop`
  > Every single active effects from the past round (e.g. Poison, Infection, Invisibility...) are cleared from players when the game is in the stopping phase so it doesn't persist when back at the lobby.
- `Gamemode Persistence When Reconnecting`
  > If a player gets killed during an ongoing game, then rejoins the server after disconnecting, they will be kept in Spectator instead of being revived.
- `Inventory Row Layout`
  > Player-picker icons (e.g. **Morphling**, **Swapper**, **Voodoo**, ***Guesser***, **Bodymaker**...) are now arranged in wrapping rows based on screen width, preventing them from going off-screen when playing on servers with many players.
- `Mods Conflicts and Fixes`
  > - Fixed a crash caused by a conflict between [Wathe](https://modrinth.com/mod/wathe) and [Iris Shaders](https://modrinth.com/mod/iris) when certain specific shaders where used.
  > - Fixed a crash caused by Stupid Express that would throw a `NullPointerException` from the Lovers HUD renderer during spectator mode, ultimately causing the client to crash if a lover died while a spectator was looking at them.
  > - Fixed an issue in Noelle's Roles **Graverobber** modifier, it now displays correctly it's "Coroner" UI death information (time, reason, role) when inspecting a body.
  > - Fixed a compatibility issue with the **Necromancer** role and it's player revival ability, players who got revived by the **Necromancer** now correctly receive all role-specific items for their newly assigned role.

</details>

---

#### Credits
- **[Wathe: Murder Mystery](https://modrinth.com/mod/wathe)** by [@RAT](https://modrinth.com/user/RAT)

#### Contributors
- [@yoy333](https://github.com/yoy333), [@Basinity](https://github.com/Basinity), [@ItsSyfe](https://github.com/ItsSyfe)
- [@Celemimphar](https://x.com/Celemimphar) (Ish Plush model/texture)
- [@math730]() (French localization)
- [@haiman322]() (Chinese localization)

#### Special thanks
- The **D.R. Harpy Transportation Ltd.** Discord server for being psychopaths
- [@doctor4t](https://www.youtube.com/@doctor4t) and all the contributors who worked on making the original [The Last Voyage of the Harpy Express](https://modrinth.com/modpack/harpy-express) modpack and [Wathe: Murder Mystery](https://modrinth.com/mod/wathe).

#### Compatibility

|    | Mod                                                                                          | Author(s)                                                   |
|:--:|:---------------------------------------------------------------------------------------------|:------------------------------------------------------------|
| ✅ | [Noelle's Roles](https://modrinth.com/mod/noelles-roles-tmm)                                 | [@agmas](https://modrinth.com/user/agmas)                   |
| ✅ | [Stupid Express](https://modrinth.com/mod/stupid-express)                                    | [@flowingforever](https://modrinth.com/user/flowingforever) |
| ✅ | [Starry Express](https://modrinth.com/mod/starexpress)                                       | [@AussieBox](https://modrinth.com/user/AussieBox)           |
| ✅ | [Kin's Wathe](https://modrinth.com/mod/kinswathe)                                            | [@Bsxin](https://modrinth.com/user/Bsxin)                   |
| ❌ | [More Shooter Punishements](https://modrinth.com/mod/harpy-express-more-shooter-punishments) | [@TheDeafCreeper](https://modrinth.com/user/TheDeafCreeper) |
