# Wathe: Extended
#### ⚠️ **This is the core mod of "[The Harpy Express: Extended](https://modrinth.com/modpack/the-harpy-express-extended)" modpack and is made to work along with this one.**
Using this mod outside the modpack is not recommended; you are free to do so, but at your own risk.
<br> I will not provide support or address issues reported by users who choose to use the mod outside the base modpack.

#### ⚖️ Credits
- **[Wathe: Murder Mystery](https://modrinth.com/mod/wathe)** by [@RAT](https://modrinth.com/user/RAT)
- **[WathExtras](https://modrinth.com/mod/wathextras)** by [ColorInk](https://modrinth.com/organization/color-ink)

#### ❤️ Special thanks
- The **D.R. Harpy Transportation Ltd.** Discord server for being psychopaths
- [@Celemimphar](https://github.com/celemimphar/) (Ish Plush model/texture)
- [@math730](https://github.com/math730) (French localization)
- [@haiman233](https://github.com/haiman233) (Chinese localization)

#### ✅ List of the currently supported add-ons
- **[Noelle's Roles](https://modrinth.com/mod/noelles-roles-tmm)** by [@agmas](https://modrinth.com/user/agmas)
- **[Stupid Express](https://modrinth.com/mod/stupid-express)** by [@flowingforever](https://modrinth.com/user/flowingforever)
- **[Starry Express](https://modrinth.com/mod/starexpress)** by [@AussieBox](https://modrinth.com/user/AussieBox)
- **[Kin's Wathe](https://modrinth.com/mod/kinswathe)** by [@Bsxin](https://modrinth.com/user/Bsxin)
- **[More Shooter Punishments](https://modrinth.com/mod/harpy-express-more-shooter-punishments)** by [@TheDeafCreeper](https://modrinth.com/user/TheDeafCreeper)

---

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
- **Panel Block variants**
  - Adds more variants to the decorative thin panel blocks with more of Wathe blocks
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
A YACL configuration screen accessible from the pause menu, organized into five categories, that can be used to directly tweak the game.
<br> **Note:** Only OP players have access to all the categories, non-OP players can only see the Client category.

#### Client
- **Pronouns** - Set custom pronouns (e.g. she/her) displayed under the username when other players look at you up close.
- **Stamina Bar** - Toggle the stamina bar HUD element.
- **Show Chat During Game** - Restores the chat HUD in read-only mode during an active game; OP players can still send messages.
- **Ultra Performance Mode** - Completely disables the scenery during a game for better performance.
- **Disable Screen Shake** - Disables the screen shake effect.
- **Fog/HUD/Snow visual toggles** - Each can be individually toggled ON/OFF.
- **Debug tools** (OP only):
  - *Show Area Boundaries* - Renders colored outlines around the Play, Ready, and Lobby areas.
  - *Show Key Assignments* - Highlights doors with key IDs assigned to them.
  - *Show RTP Slots* - Renders boxes at each configured teleportation slot's exact position and yaw/pitch.

#### Game
- **Gamerules group** - Player collisions toggle, ability VFX/SFX suppression, morph psychosis toggle, safe preparation time (toggle + configurable cooldown in seconds).
- **Wathe Options group** - Backfire chance, Shooter punishment mode (drop revolver/prevent all gun pickup/kill shooter), Initial coin income per sides (Civilian, Neutral, Killer), Kill income amount, Killer revolver drop toggle.
- **Roles Options group** - Per-role configuration for virtually every supported role across add-ons that provides configurable options (Price, Cooldown, Duration, Player limits, Special flags, etc...).
- **Modifiers Options group** - Modifier maximum per player, modifier multiplier, Guesser options (allow civilians, wrong guess outcome), Lovers options (Forbidden Lovers, know immediately, win conditions, glow), Allergic outcome chances and durations, Introverted/Taxed/Adaptive parameters.

#### Map Variables
- **Flags** - World protection, Random Teleportation (RTP), Item Bounds Check, Jump in Lobby, Auto Start timer.
- **Positions** - Spawn Position, Ready Area Spawn Position, Spectator Spawn Position.
- **Areas** - Lobby Area, Play Area, Ready Area map variables.
- **Offsets** - Play Area Offset, Reset Paste Offset.
- **RTP Slots** - Inline view and editing of all configured teleportation slots.

#### Roles & Modifiers
- Dedicated tabs showing every registered role and modifier with a toggle to enable/disable each one individually.

### 🔪 Gameplay Balancing
- **Forbidden Lovers**: An alternative way on how the Lovers modifier from Stupid Express will work in-game, the lovers will always consist of a civilian/neutral and a killer, the Forbidden Lovers wins by surviving together until the end of the game. Turning this option on will also decrease the chances of lovers being assigned.
- **Ability VFX/SFX Suppression**: A global toggle that, when enabled, suppresses all ability trigger visual and sound effects to avoid players abusing this to clear themselves.
- **Cleaner Ability Player Limit**: The Deep Cleaning ability from Kin's Wathe Cleaner will automatically be disabled when the number of alive survival players falls below a configurable threshold, preventing it from being overpowered in the late game when there are very few players left, ultimately softlocking the remaining players to have a chance to win.

### 📖 Guidebook GUI
A full custom in-game screen that can be opened by right-clicking the Guidebook item:
- Contains two tabs: **Roles** and **Modifiers**.
- Roles are categorized as **Civilian**, **Killer**, and **Neutral** on the left page.
- Right page displays the selected entry's description, abilities, and items, can be navigated with next/previous buttons.
- Custom open/close and page-flip sounds.

### 🏷️ Pronouns System
- Players set their pronouns via the configuration screen or with the `/watheextended:pronouns set <pronouns>` command.
- Pronouns are saved persistently in `config/watheextended/cache/pronouns.json` on the server.
- Displayed above a player's username when a nearby player looks at them at close range.
- Synced to all players via a custom network packet on join.

### 🗺️ Map Features
- **Random Teleportation (RTP)**: At game start, players in the ready area are each assigned a unique teleportation slot and teleported there with seamless screen-fade timing (40 ticks after fade begins). Configurable via `/watheextended:rtp slot` command.
- **Item Bounds Check**: Items that falls out of the play area boundaries during an active game are automatically teleported back to the nearest alive player or dead body.
- **World Protection**: Prevents player interaction with containers, buttons, levers, trapdoors, doors, and other interactive blocks within the map variables areas.

## Bug Fixes
On top of what the mod adds, the mod also includes a number of bug fixes for issues that exist in Wathe and some of the add-ons, improving overall stability and gameplay experience.

<details>
<summary>Bug fixes list</summary>

### Wathe Core
- **Defense Shield kill bypass fix**: The `AllowPlayerDeath` event is now properly fired before executing a kill, fixing cases where Defense Shields were bypassed (e.g. pill, defense vial, dream imprint).
- **Disabled killer role conversion fix**: If a role conversion condition triggers (Executioner succeeding, Vulture eating enough bodies, etc.), the player now converts to a random *enabled* killer role and will never convert to a role that has been disabled in the configuration.
- **Duplicate gun pickup bug fix**: Players could previously pick up extra guns by clicking a gun on the ground while already holding one in their cursor stack. This is now blocked.
- **Mood component NBT persistence fix**: `nextTaskTimer` and `timesGotten` task data are now properly serialized/deserialized in NBT, preventing mood task progress from being lost across server restarts.
- **Status Effects Cleared on Stop**: All active potion effects are cleared from players when the game is in the stopping phase.
- **Spectator on rejoin**: If a player was killed during an ongoing game and rejoins the server after disconnecting, they will be automatically placed in spectator mode instead of being revived.

### Wathe Client
- **PlayerBodyRenderer null crash fix**: Guards against a `NullPointerException` when `PlayerMoodComponent` is null during player body rendering.
- **Ability row layout fix**: Player-picker widgets (e.g. Morphling, Swapper, Voodoo, Judge, and Bodymaker UIs) are now arranged in wrapping rows based on screen width, preventing them from going off-screen when playing on servers with many players.

### Noelle's Roles
- **Graverobber HUD fix**: The Graverobber modifier now displays correct Coroner-style death information (time, reason, role) when inspecting a body.
- **Feather Modifier Fix**: Kin's Wathe conflicts with Noelle's Roles Feather modifier, Wathe: Extended is continuously reapplying the `SLOW_FALLING` effect server-side for Feather modifier players so it never expires.
- **Guesser Modifier and Mimic compat**: The Guesser will no longer be able to see and guess the Mimic if it is actually present in the current game.

### Stupid Express
- **Lovers crash fix**: Catches a `NullPointerException` that would crash the game in the Lovers HUD renderer during spectator mode, ultimately causing the client to crash if a player died while the spectator was looking at them.
- **Necromancer revival items fix**: Players revived by the Necromancer now correctly receive all role-specific items for their assigned role.
- **Thief item compatibility**: Adds Kin's Wathe items (Pan, Poison Injector, Blowgun, Pill) and Noelle's Roles / Starry Express items (Delusion Vial, Defense Vial, Tape) to the Thief's steal-able items list.

</details>
