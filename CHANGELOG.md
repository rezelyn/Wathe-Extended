# Changelog
> #### This changelog also documents changes that are related to ***The Harpy Express: Extended*** modpack.

## [Unreleased]

## [beta-4.0.0-h1.4] - TBD

> [!WARNING]
> **This update may break older instances of the modpack!**
> A fresh installation of the modpack is recommended, and the same applies to dedicated servers.
> Client and server configurations and presets prior to this version are not compatible.
> A lot of changes have been done to the backend of the mod, if you encounter any issues, please report them on GitHub, thank you!

### Added

- New configurable options:
    - Show notification from **Dreamer** imprints (Default: Disabled)
    - Show notification from **Arsonist** dousing (Default: Enabled)
        - The doused notification can be delayed by a configurable amount (Default: 10s)
- New mood mechanics and options:
    - Prevent players from sprinting while being depressed (Default: Enabled)
    - Disable role abilities after being depressed for a configurable delay in seconds (Default: Enabled, 0s)
- New proning mechanic, allowing players to crawl on the ground by using the <kbd>C</kbd> key, with configurable keybind option to toggle between **Hold** and **Toggle** modes (Default: Toggle)
- New visual debugger that shows every configured spawns positions (Lobby, Ready Area, and Play Area)
- Ability to change perspective (F5) when the game is not active
- Pressing the inventory key while spectating now opens the Guidebook screen
- Replaced the separate Wathe and HarpyModLoader name overlays with a new unified player name renderer:
    - Spectators can see the current role, modifier(s), player name, pronouns, Killer Cohort label, and some role/modifier-specific labels, all in one shared adaptive overlay

### Changed

- Improved optional add-on compatibility: mixins for Noelle's Roles, Starry Express, Stupid Express, Kin's Wathe, and Wathe Extra Items are skipped when the corresponding mod is absent (#100)
- **Breaking:** simplified the client & server config accessors and switched to pretty-printed JSON configuration files. Old config files and saved presets are no longer supported
- World protection improvements:
    - World protection now prevents players from breaking or placing blocks inside the lobby, ready, and play area boundaries
    - Added Wathe's Privacy Glasses and the blocks from [Wathe Extra Items](https://modrinth.com/mod/watheextraitems) and [WathExtra](https://modrinth.com/mod/wathextras) to the list of protected blocks
- Changed & added some visual effects:
    - A vignette now darkens the screen edges of the Introverted if it is inside a crowd of players
    - A vignette will now darken the screen edges of depressed players, and the vignette's intensity increases over time while depressed, goes away when the player is no longer depressed
    - Improved Last Stand vignette rendering and coloring
- Updated Chinese translation (@haiman233)
- Updated Stupid Express to `2026.10.1-h1.4`
    - [Stupid Express Changelog](https://modrinth.com/mod/stupid-express/version/2026.10.1-h1.4)
- Updated README.md
- Updated CHANGELOG.md
- Changed versioning format

### Removed

- Removed the shaking animations to the mood HUD when the Introverted was inside a crowd of players
- Reverted the mixin patches for Stupid Express stale imports and the Lovers modifier spectator crash

### Fixed

- Configured map effects now apply correctly at game start, and the default gamemode is restored correctly after Secret Murder rounds and server restarts
- Secret Murder chance option not being synced with Wathe's world NBT setting; it now follows that setting
- Killer Cohort overlay appearing for some roles that shouldn't be able to (**Arsonist**, **Executioner**, **Jester**, etc.) (#99)
- **Licensed Villain** and **Dreamer** having access to Instinct
- **Licensed Villain** dropping their *Revolver* when shooting innocents
- **Introverted** modifier being assigned to the **Licensed Villain**
- **Recaller** ability not teleporting them if they are seated
- **Swapper** ability not teleporting one of the selected players if they were seated
- Players' stamina not being reset to maximum when a game starts
- Players sitting outside the ready area when a game starts being put into the game instead of being set as spectators (#84)
- Wathe Extra Items mixin to randomize enabled items at round start not being applied (@yoy333)

## [stable-3.8.141] - Sep 27, 2026

### Added

- Filter in the Guidebook to only show enabled entries for roles & modifiers (@yoy333)
- Settings for gamemode selection and map effects, available under the Map category:
    - **Gamemode** (Murder (Modded), Secret Murder (Modded), Murder, Loose Ends, Secret Murder, Discovery)
    - **In-Game Time** (Day, Night, Sundown)
    - **Lobby Time** (Day, Night, Sundown)
    - **Game Duration** (Default: 10m)
    - **Use Generic Map Effect** (Default: False, intended for use with custom maps)
- Configurable option:
    - **Secret Murder Rounds Chance** (Default: 0%)
- Preset manager improvements:
    - Button to directly overwrite an existing preset
    - Hovering over a preset group now displays metadata listing every enabled and disabled role and modifier
- Extra context to the custom ability UI counter text for **Vulture** and **Dreamer**
- Compatibility between the custom ability UI and the **Infected** from Noelle's Roles

### Changed

- Secret Murder rounds now inherit the Modded ruleset by default when the active gamemode is Murder (Modded). If the base Murder gamemode is active instead, Secret Murder rounds are played without modded roles or modifiers
- Instinct configuration values are now defined in ticks and displayed as seconds (previously percentages) in the configuration screen
- Fog, Snowflakes, and HUD settings are now fully client-side and can be adjusted between rounds by any player via the Client configuration category

### Fixed

- **Phantom** ability cooldown defaulting to 0 seconds instead of 60 seconds
- **Dreamer** not receiving a new *Dream Imprint* after successfully saving a player from a hit, which could prevent them from converting into a Killer role when more than 2 players needed to be imprinted
- **Dreamer** role conversion always resulting in **Cleaner**; it now correctly converts into a random enabled Killer role
- Ability UI text still being displayed while spectating
- "Killer Cohorts" text not being drawn when looking at other Killers and some Killer-aligned Neutrals
- **Thief** not being affected by the new Instinct mechanic
- **Recaller** ability UI text
- Pronouns text fade animations not applying correctly
- **Introverted** modifier being assigned to the **Vulture** and the **Infected**
- **Infected** being unable to see players they've infected through walls

## [stable-3.7.141] - Sep 18, 2026

### Added

- Configuration preset manager, found in the configuration screen under the "Presets" category:
    - Presets store every value from the current config file, meaning anything under the "Map" or "Client" categories is excluded
    - Presets are stored server-side, so any player with full access to the configuration screen can load or delete them
- **Game Guide** tab in the Guidebook, providing an in-game reference for the game's mechanics, features, and how gameplay works
- Option to change the Instinct keybind activation method: **Hold** or **Toggle** ON/OFF (Default: Hold)
- New reworked Instinct mechanic so it now plays a meaningful role for Killers and Neutrals:
    - Instinct is no longer unlimited (Default: 60s)
    - Instinct depletes over time while active (Default: -3s/s)
    - Instinct regenerates over time while unused (Default: +1s/s)
    - Added new ambience sound effects that play while Instinct is active
    - Added a new (configurable) HUD element displaying the current Instinct capacity
- Shooter punishment modes with refined logic:

|        Mode        | Action                                                                                                                                    |
|:------------------:|:------------------------------------------------------------------------------------------------------------------------------------------|
|    **Default**     | **Target** is killed. **Shooter** drops their *Revolver* and cannot pick it back up, but can still pick up guns dropped by other players. |
| **Prevent Pickup** | **Target** is killed. **Shooter** drops their *Revolver* and cannot pick up any more *Revolvers* for the rest of the game.                |
|  **Kill Shooter**  | **Target** survives. **Shooter** is killed instead and drops their *Revolver*.                                                            |
|   **Kill Both**    | **Target** and **Shooter** are both killed, and the *Revolver* is dropped.                                                                |

- Missing French and Simplified Chinese translations

### Changed

- Updated Wathe to `1.4.1-1.21.1`
    - [Wathe Changelog](https://modrinth.com/mod/wathe/version/1.4-1.21.1)
- Updated the "Backfire Chance" configuration screen option to be compatible with the new backfire-per-innocent-kill mechanic
- Replaced some raw **float**/**integer** settings with **sliders** on the config screen, which are slightly less flexible but more accessible
- Updated README.md

### Fixed

- Various typos and outdated information in some item lore descriptions
- Various default configuration screen values being outdated or different from the defaults set by the mod
- **Violator** modifier not being able to jump in-game (#84, #83)
- Jump Mode option not being applied correctly in-game
- Crash when sleeping in a poisoned bed (#86)
- Outdated and missing entries for various roles in the Guidebook (#84, #83)
- Players being unable to pick up dropped *Revolvers* (#73)
- Compatibility issues between Stupid Express and Wathe 1.4
- Compatibility issues between Kin's Wathe and Wathe 1.4

## [stable-3.6.132] - Sep 3, 2026

### Added

- Creative, OP-only items to help set up and test a map:
    - **Create RTP Slot**
        - Saves your current position and rotation as a new random teleportation slot, as long as you are standing inside the Ready Area
    - **Trigger RTP**
        - Teleports every player to the registered teleportation slots
        - Useful when players are slow to board the train
    - **Add Players** / **Remove Players**
        - Spawns and despawns fake players to test the game with (requires [Carpet](https://modrinth.com/mod/carpet) mod)
- Configurable options:
    - **Passive Income**
        - **Base Passive Income**
            - Changes the amount of coins granted by the vanilla passive income
        - **Adjust Passive Income**
            - When enabled, passive income scales with the distance to the nearest player (applies to non-Innocents only)
        - **Max Passive Income Distance**
            - Distance in blocks from the nearest player at which the scaled passive income drops to zero (Default: 10)
        - **Minimum Passive Income**
            - Lower bound applied to the scaled passive income, so distant players still earn something (Default: 0)
    - **Role Options**
        - **Morphling**
            - Morph Psychosis: insane players see morphed players as their real selves
        - **Bartender**
            - Defense Vial Price and Defense Maximum Time
        - **Trapper**
            - Sees Names and Role Mine Price
        - **Guesser**
            - Can Use Instinct
        - **Infected**
            - Kill Time and Cough Chance
        - **Recon**
            - Sees Names
        - **Executioner**
            - Can Pick Up Gun
- New Guidebook entries for:
    - **Infected**
    - **Introvert**
    - **Stealthy**

### Changed

- Updated Noelle's Roles to `1.7.1-h1.3`
    - [Noelle's Roles Changelog](https://modrinth.com/mod/noelles-roles-tmm/version/1.7.1-h1.3)

### Fixed

- Sitting/sleeping players not getting randomly teleported at game start (@ItsSyfe)
- Vulture Guidebook entry wrongly claiming it has the Instinct and Athletic abilities

## [stable-3.5.132] - Sep 3, 2026

### Added

- "Items" tab inside the Wathe: Extended configuration screen that lets you view and edit the prices and cooldowns of every usable item from Wathe and supported add-ons
- `/pronouns clear <targets>` command, allowing OP players to clear other players' pronouns (@Basinity)
- "Teleport to Scenery" item for creative OP players that teleports them to the Spectator Spawn Position upon use
- Moquette block color variants
- HUD elements for the **Arsonist**, now showing the number of doused players required before being able to ignite
- Configurable options:
    - **Kill Increase Time**
        - Changes the number of seconds added to the timer when a Killer gets a kill
    - **Jump Mode**
        - Changes the behavior of the jump restriction mechanic: **Default**, **Lobby Only**, or **Everywhere**
    - **Item Prices & Cooldowns**
        - Configurable prices and cooldowns for every shop item from Wathe and supported add-ons (see the "Items" tab in the configuration screen)
    - **Role Abilities**
        - Configurable "Cancel Ability" option for **Phantom** and **Morphling**, allowing them to cancel their ability at any time by pressing the ability key again instead of waiting for the full duration to end
        - Configurable ability duration for:
            - **Phantom**
            - **Morphling**
        - Configurable ability cooldown for:
            - **Phantom**
            - **Morphling**
- Optional mechanic: "Last Stand"
    - If enabled, this changes the behavior of any remote death mechanic (e.g. a correct **Guesser** guess, a **Voodoo** trigger on a Voodoo Doll, or **Lovers** heartbreak).
      <br> Instead of being killed immediately, the damned player is notified and has a short period of time before they actually die (Default: 30s)
- New Guidebook entries:
    - **Awesome Binglus**
    - **The Insane Damned Paranoid Killer**
    - **Better Vigilante**
- New roles: *(More information in-game)*
    - **Technician**

### Changed

- Major rework of the configuration screen:
    - Role- and modifier-specific options are now grouped into their own unique groups within the "Roles" and "Modifiers" tabs, instead of being mixed together in the "Game" category
    - Several visual and readability improvements
    - Renamed some options and descriptions to make it clearer what they do
- Using the inventory key while inside the Guidebook screen now closes the screen
- Improved item tooltips: they now explain more clearly what an item does and how to use it, and show styled cooldowns where applicable
- Gameplay balancing:
    - Roles:
        - **Hunter**
            - Can no longer buy the default Knife, Poison Vial, and Scorpion from the shop
        - **Kidnapper**
            - Can no longer buy the Crowbar from the shop
        - **Physician**
            - Pill is now invisible in hand to other players
        - **Cleaner**
            - No longer receives bonus coins by default for using the Sulfuric Acid Barrel on dead bodies (configurable)
            - The bonus coin value for using the Sulfuric Acid Barrel on dead bodies is now configurable (Default: 50 coins)
        - **Awesome Binglus**
            - Now starts with 4 Notes
            - Gains 50 coins per completed task
            - Can buy Notes in the shop
            - Can stack up to 64 Notes
            - Can see Notes through walls using Instinct
        - **Morphling**
            - Can now cancel their ability at any time by pressing the ability key again, instead of waiting for the full duration to end
        - **Phantom**
            - Can now cancel their ability at any time by pressing the ability key again, instead of waiting for the full duration to end
    - Items:
        - **Grenade**
            - Is now put on cooldown after being thrown, preventing players from spamming it (Default: 1m30s)

### Removed

- [WathExtras](https://modrinth.com/mod/wathextras)' LGBTQIA+ Pride-themed cocktail assets. Make sure to check out their mod!

### Fixed

- Missing localization entries for some blocks added in the previous update
- Tasks not being completed even though the player had performed the required action
- *Grenade* not being put on cooldown after being thrown
- **Guesser** player-picker row having a gap if Mimic was present in a game
- **Guesser** player-picker row not wrapping into multiple rows when there were too many players in a game, causing it to overflow outside the screen
- **Initiate** not correctly converting to **Amnesiac** when the other Initiate died from self-inflicted causes or when killed by a non-Initiate, and surviving **Initiate** not converting either in those cases (#52, #58)
- Neutral roles converting into Killer roles that were disabled in the configuration (#60, #68)
- Role announcement displaying a different role than the one the player actually has after converting into a different role
- Role announcement displaying a different role than the one the player actually has when using the `/forceRole` command
- Pronouns not switching to the morphed player's pronouns when using the Morphling ability (#69)
- **Introverted** modifier effect being active before receiving the first task of a game
- Moquette block face culling issues (#57)
- Color formatting issues in the Guidebook

## [stable-3.4.132] - Mar 23, 2026

### Added

- French translation (@math730)
- Panel variants for some of Wathe's blocks:
    - Black Hull Panel
    - Black Hull Sheets Panel
    - Bubinga Bookshelf Panel
    - Bubinga Herringbone Panel
    - Bubinga Planks Panel
    - Dark Steel Panel
    - Ebony Bookshelf Panel
    - Ebony Herringbone Panel
    - Ebony Planks Panel
    - Gold Panel
    - Mahogany Bookshelf Panel
    - Mahogany Herringbone Panel
    - Mahogany Planks Panel
    - Marble Tiles Panel
    - Metal Sheet Panel
    - Pristine Gold Panel
    - Stainless Steel Panel
    - Tarnished Gold Panel
- Moquette block color variants:
    - Black Moquette
    - Green Moquette
    - Purple Moquette
- HUD effects when the Introverted modifier is inside a crowd of players
- Optional setting to disable ability VFX/SFX (currently only supports Starstruck, Robot, and Bellringer)
- New roles: *(More information in-game)*
    - **Hacker**

### Changed

- Improved inventory ability UIs:
    - New slot textures for each role's ability rows, depending on the role
    - **Bodymaker** and **Guesser** UIs now show a clickable list of *every available* role you can choose from instead of requiring you to type them
    - Rows are now split into multiple rows when they start to overflow outside the screen
    - Updated row placements to avoid overlapping with other UI elements (e.g. the **Guesser** row overlapping with the player's role ability row)
    - Added labels above each ability row to clarify what the row is for inside the inventory screen
    - Updated in-game HUD ability tip text styles to be more consistent and simple (e.g. cooldown, price, ready state)
- Improved Kin's Wathe stamina bar visuals
- Centralized the creative tab for Wathe: Extended blocks and items
- Gameplay balancing:
    - Morph Psychosis is now disabled by default
    - Roles:
        - **Cleaner**
            - The Deep Cleaning ability is now completely disabled under 10 players by default
        - **Bodymaker**
            - Fake Noisemaker bodies now glow when created
            - The sound matching the chosen death reason is now played when the body is created (e.g. Revolver sound, Grenade sound)
        - **Bartender**
            - Default Maximum Defense Vials is now 1
            - Default Defense Vial price is now 200 coins
        - **Physician**
            - Default Pill cooldown is now 3 minutes
        - **Amnesiac**
            - No longer glows a different color to Killers by default
        - **Arsonist**
            - By default, the game now continues even after all Killers have been eliminated, and victory requires eliminating every player
    - Modifiers:
        - **Lovers**
            - When the **Forbidden Lovers** option is enabled, the chance of having Lovers in the next game is reduced by 75% (configurable)
            - Lovers now see each other glowing through walls by default
        - **Taxed**
            - Tax no longer affects passive income
            - Tax is now applied if the player kills more than 1 player within the same minute
            - Default tax is now -50% of the player's kill income

### Fixed

- Various Guidebook entries that had missing or inaccurate information
- Pronouns overlapping the player's role when looking at them in spectator mode
- Pronouns still being rendered when looking at invisible players
- Pronouns being rendered on Psycho Mode players when looking at them (#50)
- Pronouns being rendered when Morph Psychosis is active and the player's mood is depressed (#50)
- **Introverted** modifier being assigned to the **Robot**, **Dreamer**, and **Thief**
- **Introverted** modifier not working properly with the **Starstruck** ability
- Infinite coins bug when the **Introverted** modifier was combined with roles that have passive income
- **Adaptive** modifier being assigned to non-Killers
- Kin's Wathe stamina bar overlapping issues
- **Bartender**'s use of *Defense Vials* alerting the **Drugmaker**'s and **Physician**'s Poison Sense abilities
- Crash caused by Wathe conflicting with Iris
- Ability to pick up more than one food/drinkable item from different trays
- **Guesser** modifier being able to guess the **Mimic** (#51)

## [stable-3.3.132] - Mar 17, 2026

### Fixed

- Non-operator players getting stuck inside beds when trying to sleep during a game (#41)

## [stable-3.2.132] - Mar 17, 2026

### Added

- Chinese translation (@haiman233)
- Player pronouns that show up above usernames when looking at a player
    - Pronouns are customizable through the configuration screen or by using the `/pronouns` command
- **"Show Chat During Game"** toggle in the configuration screen
    - Players cannot open the chat input or send messages/commands while in a game
    - Server admins (Permission Level >= 2) retain full chat and command access during a game
- A new ***very special*** plushie :3 (Thanks to [@Celemimphar](https://github.com/celemimphar/) for the amazing model and texture!)
- New modifiers:
    - **Introverted** (Suggested by @koniri.)
    - **Taxed** (Suggested by @shxnji)
    - **Adaptive** (Suggested by @koniri.)
- **Lovers**: new configurable alternative option, **Forbidden Lovers** (Suggested by @.anisla.)
  <br> The Lovers pair is always one Killer and one non-Killer when this option is enabled
- **Starstruck**: new ability effect particles and a new particle trail while the ability is active

### Changed

- **Guidebook UI overhaul:**
    - New sprites and textures for the book, tabs, and navigation buttons
    - Added proper tab buttons to switch between Roles and Modifiers
    - Various layout improvements and fixes
    - Page scrolling now uses right-click instead of left-click (mouse wheel scrolling is still available)
- **Muzzler**
    - Tape now replaces the Revolver in the shop
    - Tape application sound effects are now client-side

### Fixed

- **Initiate** converting into a Killer when the other Initiate is attacked while protected by a Defense Shield (#17)
- **Executioner** converting into a Killer when their target is attacked while protected by a Defense Shield (#18)
- Dead players being automatically revived when disconnecting and reconnecting during an active game (#33)
- Current task being reset to a new one when a player disconnects and reconnects (#34)
- Role and modifier Enabled/Disabled icons in the Guidebook not being synchronized correctly for non-operator players (#36)
- (?) Revived players not receiving their role's starting items when revived by the Necromancer (#40)
- Roles such as **Executioner** and **Initiate** (and any other role that converts into a Killer role based on its goal or condition) being able to convert into a Killer role that is disabled in the configuration (@mikrokimos)
- Players being able to pick up a second gun from the ground by moving their existing gun with the cursor via the inventory screen
- Status effects persisting after a game ended
- Blood particles remaining after using Body Bags

## [stable-3.1.132] - Mar 8, 2026

### Added

- Client-side options category within the configuration screen, accessible by anyone through the pause menu
- Server-side options categories within the configuration screen, accessible by server admins (Permission Level 2) through the pause menu
- "Close" button in the Guidebook
- "Lobby Area" map variable
- Visual debuggers that can be enabled to show boundaries/placement (#26):
    - Map Variables (playArea, readyArea, lobbyArea)
    - Key Assignments
    - RTP Slots
- Exposed many values from various add-ons so they can be modified directly through the configuration screen:
    - Enable/Disable Morph Psychosis
    - Enable/Disable Safe Preparation Time
    - View/Edit Safe Preparation Cooldown
    - View/Edit the maximum number of modifiers players can have
    - View/Edit the Modifier Multiplier relative to the Killer Dividend
    - **Wathe Tweaks:**
        - Initial Civilian Income
        - Initial Neutral Income
        - Initial Killer Income
        - Enable/Disable Killer Drop Revolver
        - Revolver Shooting Punishment Mode
    - **Role Options:**
        - Ability Prices
        - Ability Cooldowns
        - Item Prices
        - Player Limit
        - ...and more role-specific rules
- New icons for role abilities in the Guidebook
- **Killer**, **Vigilante**, and **Civilian** roles are now shown inside the Guidebook

### Changed

- The **Hunter** can now buy the default Knife alongside the Hunting Knife
- The **Cleaner** now receives coins when using the Sulfuric Acid Barrel
- The **Kidnapper** now gains additional coins if they personally kill the player they've dazed
- The **Thief** can now steal more items, including:
    - *Pan*
    - *Blowgun*
    - *Poison Injector*
    - *Pill*
    - *Delusion Vial*
    - *Defense Vial*
    - *Tape*
- Coroner Instinct and Conductor Instinct are now disabled by default
- Reworked the configuration screen layout to be more intuitive and better organized
- Improved the World Protection function, which now applies only within the MapVariables areas (playArea, lobbyArea, readyArea)
- RTP slots can now only be created inside the readyArea
- Improved RTP slot management: IDs are now constant and no longer change when other slots are deleted
- Reworked the Wathe: Extended command tree parents
- **Guidebook UI improvements:**
    - Role item lists are now fully complete, covering everything a role can purchase or receive at the start of a game
    - Rewrote many entry descriptions to be more accurate and easier to understand, better explaining what roles and modifiers do
- Improved hit detection for the *Blowgun*, *Hunting Knife*, and *Pan*

### Removed

- Items tab from the Guidebook, in favor of per-role item pages

### Fixed

- Depressed players seeing hallucination items on invisible players, such as the **Phantom** (#5)
- Crash triggered when one of the two Lovers disconnected during a game, which caused the disconnected Lover's name to incorrectly appear on the other one's dead body Coroner HUD for spectators (#23)
- Default map's room 2 door not being correctly assigned to the "Room 2" key (#24)
- Missing Guidebook entries for some roles (#32)
- Simple Voice Chat icons not being shown by default (#25)
- **Feather** modifier's Slow Falling effect no longer being applied (#28)
- **Graverobber** modifier's Coroner HUD not showing up when looking at dead bodies
- Noelle's Roles bonus roles not being shown in the configuration screen
- Spectators not automatically joining the spectators' voice group when joining mid-game
- **Judge**'s ability being usable on dead players; it can now only be used on living players
- Teleportation effect from the *Dream Imprint* affecting players in Psycho Mode
- Poisoned effect persisting after death; it is now cleared automatically
- Many typos and grammatical issues within the Guidebook and the configuration screen

## [stable-3.0.132] - Feb 28, 2026

Stable release! This update introduces a wide range of new content, improvements, and fixes, including the brand-new **Wathe: Extended mod**.

> [!WARNING]
> This update may break older instances of the modpack. A fresh reinstall is recommended, and the same applies to dedicated servers.
> I'm still actively learning Java and the Fabric modding environment. If you encounter any issues, please open an issue on GitHub. I'll do my best to provide support and fix bugs!

### Added

- New cocktails, new blocks, and refreshed models for existing ones! **(WIP)**
- Current role and any active modifier(s) are now shown at the top of the inventory screen
- Safe preparation time at the start of each game to prevent spawn-killing and other unfair abuses
- Configurable options available through the Wathe: Extended config screen:
    - **Toggle Player Collisions**
    - **Toggle Random Teleportation at Game Start**
    - **Toggle Interaction Safeguards**
    - **Toggle Roles and Modifiers**
    - **Toggle OOB Items Recovery**
    - **View and Edit Map Variables**
- New roles: *(More information in-game)*
    - **Bellringer**
    - **Bodymaker**
    - **Cleaner**
    - **Cook**
    - **Detective**
    - **Dreamer**
    - **Drugmaker**
    - **Hunter**
    - **Judge**
    - **Kidnapper**
    - **Licensed Villain**
    - **Physician**
    - **Robot**
    - **Thief**
- New modifiers: *(More information in-game)*
    - **Magnate**
    - **Taskmaster**
    - **Violator**

### Changed

- The default Letter item and Starry Express's Guidebook have been replaced with a brand new, redesigned Guidebook item and UI **(WIP)**
- Improved logic for randomized player teleportation at the start of a game
- Gameplay balancing:
    - **Amnesiac**
        - Can now see the dead bodies' glowing effect
        - No longer glows a different color to Killers
    - **Lovers**
        - Lovers can now see each other glowing through walls
        - If one Lover leaves the game, their partner also dies
    - **Arsonist**
        - Victory now requires eliminating every player
        - The game now continues even after all Killers have been eliminated
    - Removed *Poison* from the **Executioner** and **Vulture** upon converting into a Killer
    - Players who drop their Revolver after shooting an innocent can no longer pick up another one for the rest of the game


### Fixed

- **Amnesiac** and **Initiate** incorrectly glowing for non-Killer roles
- **Initiate** displaying the wrong glow color to non-Killers
- **Initiate** retaining their knife after the other Initiate's death
- **Initiate** spawning with items belonging to other Neutral roles
- **Necromancer** being able to revive disabled roles
- **Lovers** being assigned to both the **Executioner** and their designated target
- Revived players remaining in the Train Spectators voice chat group
- *Delusion Vials* incorrectly checking the poisoned player's role rather than the poisoner's
- Spectators being able to access the inventory screen
- Train reset failing to apply in time at game start, which caused visual bugs
- Killer Instinct night vision not functioning correctly

[Unreleased]: https://github.com/rezelyn/Wathe-Extended/compare/beta-4.0.0-h1.4...HEAD
[beta-4.0.0-h1.4]: https://github.com/rezelyn/Wathe-Extended/compare/3.4.132...3.5.132
[stable-3.8.141]: https://github.com/rezelyn/Wathe-Extended/compare/3.7.141...3.8.141
[stable-3.7.141]: https://github.com/rezelyn/Wathe-Extended/compare/3.6.132...3.7.141
[stable-3.6.132]: https://github.com/rezelyn/Wathe-Extended/compare/3.5.132...3.6.132
[stable-3.5.132]: https://github.com/rezelyn/Wathe-Extended/compare/3.4.132...3.5.132
[stable-3.4.132]: https://github.com/rezelyn/Wathe-Extended/compare/3.3.132...3.4.132
[stable-3.3.132]: https://github.com/rezelyn/Wathe-Extended/compare/3.2.132...3.3.132
[stable-3.2.132]: https://github.com/rezelyn/Wathe-Extended/compare/3.1.132...3.2.132
[stable-3.1.132]: https://github.com/rezelyn/Wathe-Extended/compare/3.0.132...3.1.132
[stable-3.0.132]: https://github.com/rezelyn/Wathe-Extended/releases/tag/3.0.132