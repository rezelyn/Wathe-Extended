# Wathe: Extended
#### ⚠️ **This is the core mod of "[The Harpy Express: Extended](https://modrinth.com/modpack/the-harpy-express-extended)" modpack and outside of which, isn't intended to be played with.**
Using this mod outside the modpack is not recommended; you are free to do so, but at your own risk.
<br> I will not provide support or address issues reported by users who choose to use the mod outside the base modpack.

## Roles
Soon™

## Modifiers
|      Name       |        Side        |                                                                           Description                                                                            |
|:---------------:|:------------------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| **Introverted** | Civilians/Neutrals | When more than X players are nearby (configurable crowd count + range), mood drains faster. When alone or with only one other player nearby, mood drains slower. |
|    **Taxed**    |      Killers       |               If a killer exceeds a configurable kill count within a rolling time window, their kill income is reduced by a configurable fraction.               |
|  **Adaptive**   |      Killers       |        Repeating the same kill method consecutively applies an income penalty. Switching to a different kill method consecutively grants an income bonus.        |

## Items & Blocks
- **Guidebook**
  - An in-game book item that replaces Wathe's Letter item, opens a custom GUI containing descriptions, abilities, and items for every registered roles and modifiers.
- **LGBTQIA+ Pride-themed Cocktails**
  - Cotton Candy Shake *(Trans)*
  - Galaxy Fizz *(Bi)*
  - Honey Lemonade *(Non-binary)*
  - Mint Ocean *(Gay)*
  - Neon Splash *(Pan)*
  - Pride Punch *(Rainbow)*
  - Sunset Prism *(Lesbian)*
  - The Amethyst *(Intersex)*
  - Velvet Ace *(Asexual)*
- **More Panel block variants**
- **More Moquette color variants**
- **Steel Ornament variants**
  - Anthracite color
  - Khaki color
  - Maroon color
  - Muntz color
  - Navy color
- **Ish Plush**
  - Meow :3

## Features

### ⚙️ Configuration Screen
- A YACL configuration screen accessible from the pause menu, organized into five categories, that can be used to directly tweak the game.
  <br> **Note:** Only OP players have access to all the categories, non-OP players can only see the Client category.

### 🔪 Gameplay & Mechanics Tweaks
- **Forbidden Lovers**: An alternative way on how the Lovers modifier from Stupid Express will work in-game, the lovers will always consist of a civilian/neutral and a killer, the Forbidden Lovers wins by surviving together until the end of the game. Turning this option on will also decrease the chances of lovers being assigned.
- **Ability VFX/SFX Suppression**: A global toggle that, when enabled, suppresses all ability trigger visual and sound effects to avoid players abusing this to clear themselves.
- **Cleaner Ability Player Limit**: The Deep Cleaning ability from Kin's Wathe Cleaner will automatically be disabled when the number of alive survival players falls below a configurable threshold, preventing it from being overpowered in the late game when there are very few players left, ultimately softlocking the remaining players to have a chance to win.
- **Thief Stealable Items Compatibility**: Allow the Thief to steal more items: Pan, Poison Injector, Blowgun, Pill, Delusion Vial, Defense Vial and Tape.
- **Last Stand Chance**: An optional and configurable option that when enabled, will grant players extra-seconds of life if their fate is decided to be death by the Guesser ability, a Lovers heartbreak, or a Voodoo Doll curse, giving them a chance to do one last move before dying.

### 📖 Guidebook GUI
A full custom in-game screen that can be opened by right-clicking the Guidebook item:
- Contains two tabs: **Roles** and **Modifiers**.
- Roles are categorized as **Civilian**, **Killer**, and **Neutral** on the left page.
- Right page displays the selected entry's description, abilities, and items, can be navigated with next/previous buttons.
- Custom open/close and page-flip sounds.

### 🏷️ Pronouns System
- Players can set their own pronouns via the configuration screen or by using the `/watheextended:pronouns set <pronouns>` command.
- Pronouns are saved persistently in `config/watheextended/cache/pronouns.json` on the server.
- They will be displayed under the player's username when looking at them at close range.
- Pronouns are synced to all players via packet when joining the server.

### 🗺️ Map Features
- **Random Teleportation (RTP)**: At game start, players in the ready area are each assigned a unique teleportation slot and teleported there while the screen fades to black at the start of a game. Configurable via `/watheextended:rtp slot` command.
- **Item Bounds Check**: Items that falls out of the playable area boundaries during an active game wil au teleported back to the nearest alive player or dead body.
- **World Protection**: Prevents player interaction with containers, buttons, levers, trapdoors, doors, and other interactive blocks within the map variables areas.

## Bug Fixes
On top of what the mod adds, the mod also includes a number of bug fixes for issues that exist in Wathe and some of the add-ons, improving overall stability and gameplay experience.

<details>
<summary>Bug fixes list</summary>

- **Defense Shield Kill Bypass**: The `AllowPlayerDeath` event is now properly fired before executing a kill, fixing cases where Defense Shields were bypassed and made the Executioner or the Initiates role conversions triggers even though their targets were still alive.
- **Disabled Killer Role Conversion**: If a role conversion condition triggers (Executioner succeeding, Vulture eating enough bodies, etc.), the player will always convert to a random **ENABLED** role and will never convert to a role that has been disabled in the current configuration.
- **Duplicate Gun Pickup bug**: Players could previously pick up extra guns by picking a gun on the ground while already holding one in their cursor.
- **Mood Component NBT persistence**: `nextTaskTimer` and `timesGotten` task data are now properly serialized/deserialized in NBT, preventing mood task progress from being lost when reconnecting during an active game.
- **Status Effects Cleared on Stop**: All active potion effects are cleared from players when the game is in the stopping phase.
- **Spectator on Rejoin**: If a player was killed during an ongoing game and rejoins the server after disconnecting, they will be kept in spectator mode instead of being revived.
- **Ability Row Layout**: Player-picker widgets (e.g. Morphling, Swapper, Voodoo, Judge, and Bodymaker UIs) are now arranged in wrapping rows based on screen width, preventing them from going off-screen when playing on servers with many players.
- **Psychosis Items Render**: Psychosis items (fake items that appears in other players hands when the client's mood is depressed) will not be rendered on invisible players, like the Phantom ability.

### Noelle's Roles
- **Graverobber HUD fix**: The Graverobber modifier now displays correct Coroner-style death information (time, reason, role) when inspecting a body.
- **Feather Modifier Fix**: Kin's Wathe conflicts with Noelle's Roles Feather modifier, Wathe: Extended reapplies the `SLOW_FALLING` effect server-side for Feather modifier players.

### Stupid Express
- **Lovers crash fix**: Catches a `NullPointerException` in the Lovers HUD renderer, ultimately causing the spectator client to crash if a lover died while it was looking at them.
- **Necromancer revival items fix**: Players revived by the Necromancer now correctly receive all role-specific items for their newly assigned role.

</details>

## ⚖️ Credits
- **[Wathe: Murder Mystery](https://modrinth.com/mod/wathe)** by [@RAT](https://modrinth.com/user/RAT)
- **[WathExtras](https://modrinth.com/mod/wathextras)** by [ColorInk](https://modrinth.com/organization/color-ink)

#### ❤️ Special thanks
- The **D.R. Harpy Transportation Ltd.** Discord server for being psychopaths
- @Celemimphar (Ish Plush model/texture)
- @math730 (French localization)
- @haiman322 (Chinese localization)

#### ✅ List of the currently supported add-ons
- **[Noelle's Roles](https://modrinth.com/mod/noelles-roles-tmm)** by [@agmas](https://modrinth.com/user/agmas)
- **[Stupid Express](https://modrinth.com/mod/stupid-express)** by [@flowingforever](https://modrinth.com/user/flowingforever)
- **[Starry Express](https://modrinth.com/mod/starexpress)** by [@AussieBox](https://modrinth.com/user/AussieBox)
- **[Kin's Wathe](https://modrinth.com/mod/kinswathe)** by [@Bsxin](https://modrinth.com/user/Bsxin)
- **[More Shooter Punishments](https://modrinth.com/mod/harpy-express-more-shooter-punishments)** by [@TheDeafCreeper](https://modrinth.com/user/TheDeafCreeper)