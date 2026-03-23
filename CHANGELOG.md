> #### This changelog also documents changes that are related to ***The Harpy Express: Extended*** modpack.

# stable-3.4.132

### 🆕 What's New
- Added French translation ([@math730](https://github.com/rezelyn/Wathe-Extended/issues/46))
- Added new panel variants for some Wathe's blocks
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
- Added new moquette blocks color variants
  - Black Moquette
  - Green Moquette
  - Purple Moquette
- Improved inventory ability UIs:
  - New textures for the slots of each unique roles ability rows, depending on the role
  - **Bodymaker** and **Guesser** UIs will now show a clickable list of *every available* roles you can choose from instead of writing them down by yourself
  - Rows are now split into multiple rows when the row starts to overflow outside the screen
  - Update row placements to avoid overlapping with other UI elements (e.g. the Guesser modifier row overlapping with the player's role ability row)
  - Added labels above each ability rows to clarify what that row is for inside the inventory screen
  - Updated in-game HUD ability tip texts style to be more consistent and simple (e.g. cooldown, price, ready state...)
- Added new HUD effects when Introverted modifier is inside a crowd of players
- Added an optional option to disable ability VFX/SFX (Currently only supports Starstruck, Robot and Bellringer)
- New ability triggering sound effects for the **Bellringer** and **Robot** roles
- New textures for the Hunting Knife, Pan, Medical Kit, and Sulfuric Acid Barrel
- New roles: *(More information in-game)*
  - **Hacker**

### 📋 Changes
- Improved Kin's Wathe stamina bar visuals
- Centralized creative tab for Wathe: Extended blocks and items
- Gameplay balancing:
  - Morph Psychosis is now disabled by default
  - Roles:
    - **Cleaner**
      - The Deep Cleaning ability will now be completely disabled under 10 players by default.
    - **Bodymaker**
      - When creating a fake Noisemaker body, the body will glow
      - The correct sound of the chosen death reason will be played when the body is created (e.g. Revolver sound, Grenade sound)
    - **Bartender**
      - Default Maximum Defense Vial is now 1 by default
      - Default Defense Vial price is now 200 coins by default
    - **Physician**
      - Default Pill cooldown is now 3 minutes
    - **Amnesiac**
      - No longer glows a different color to Killers by default
    - **Arsonist**
      - The game will now continue even after all Killers have been eliminated, and victory will require eliminating every player by default
  - Modifiers:
    - **Lovers**
      - When **Forbidden Lovers** option is enabled, reduce the chances of having Lovers for the next game by 75% (Configurable).
      - Lovers will now see each other glowing through walls by default
    - **Taxed**
      - Tax no longer affects passive income
      - Tax will now be applied if the player kills more than 1 player within the same minute
      - Default tax is now -50% of the player's kill income

### 🔧 Fixes
- Fixed various entries in the Guidebook that had missing information or were inaccurate
- Fixed pronouns overlapping player's role when looking at them in spectator mode
- Fixed pronouns still being rendered when looking at invisible players
- Fixed pronouns being rendered on Psycho Mode players when looking at them (#50)
- Fixed pronouns being rendered when Morph Psychosis is active and the player's mood is depressed (#50)
- Fixed Introverted modifier being assigned to the Robot, Dreamer and Thief
- Fixed Introverted modifier not working properly with the Starstruck ability
- Fixed Introverted modifier infinite coins bug with roles that have passive income
- Fixed Adaptive modifier being assigned to non-killers
- Fixed Kin's Wathe stamina bar overlapping issues
- Fixed Bartender's use of Defense Vials alerting the Drugmaker and Physician's Poison Sense abilities
- Fixed a crash caused by Wathe conflicting with Iris
- Fixed ability to pickup more than one food/drinkable item from different trays
- Fixed Guesser modifier being able to guess the Mimic (#51)

# stable-3.3.132

### 🔧 Fixes
- Fixed non-operator players being stuck inside beds when trying to sleep during a game

# stable-3.2.132

### 🆕 What's New
- Added Chinese translation ([@haiman233](https://github.com/rezelyn/Wathe-Extended/pull/39/changes))
- Added new player pronouns that would show up above their usernames when looking at them
  - Pronouns are customizable through the configuration screen or by using the `/pronouns` command
- Added a **"Show Chat During Game"** toggle in the configuration screen
  - Players cannot open the chat input or send messages/commands while in a game
  - Server admins (Permission Level >= 2) retain full chat and command access during a game
- Added a new ***very special*** plushie :3 (Thanks to [@Celemimphar](https://github.com/celemimphar/) for the amazing model and texture!)
- New modifiers:
  - **Introverted** (Suggested by @koniri.)
  - **Taxed** (Suggested by @shxnji)
  - **Adaptive** (Suggested by @koniri.)

### 📋 Changes
- Gameplay changes:
  - **Lovers**
    - New configurable alternative option: **Forbidden Lovers** (Suggested by @.anisla.)
      <br> Lovers pair will always be Killer and non-Killer when this option is enabled
  - **Muzzler**
    - Tape now replaces the Revolver in the shop
    - Tape application sound effects are now client-sided
  - **Starstruck**
    - New ability effect particles
    - New particles trail when ability is active
- **Guidebook UI overhaul:**
  - New sprites and textures for the book, tabs and navigation buttons
  - Added proper tab buttons to switch between Roles and Modifiers
  - Various layout improvements and fixes
  - Page scrolling now uses right-click instead of left-click (mouse wheel scrolling is still available)

### 🔧 Fixes
- Fixed **Initiate** converting into a Killer when the other Initiate is attacked while protected by a Defense Shield ([#17](https://github.com/rezelyn/Wathe-Extended/issues/17))
- Fixed **Executioner** converting into a Killer when their target is attacked while protected by a Defense Shield (#[18](https://https://github.com/rezelyn/Wathe-Extended/issues/18))
- Fixed dead players being automatically revived when disconnecting and reconnecting during an active game ([#33](https://github.com/rezelyn/Wathe-Extended/issues/33))
- Fixed the current task being reset to a new one when a player disconnects and reconnects ([#34](https://github.com/rezelyn/Wathe-Extended/issues/34))
- Fixed role and modifier Enabled/Disabled icons in the Guidebook not being synchronized correctly for non-operator players ([#36](https://github.com/rezelyn/Wathe-Extended/issues/36))
- Fixed(?) revived players not receiving their role's starting items when being revived by the Necromancer ([#40](https://github.com/rezelyn/Wathe-Extended/issues/40))
- Fixed roles such as **Executioner** and **Initiate** (and any other role that converts into a Killer Role from their goal/condition) being able to convert into a Killer Role that is disabled in the configuration (@mikrokimos)
- Fixed players being able to pick up a second gun from the ground by moving their existing gun with their cursor via the inventory screen
- Fixed status effects persisting after a game ended
- Using Body Bags will remove blood particles

# stable-3.1.132

### 🆕 What's New
- Added client-side options category within the configuration screen, accessible by anyone through the pause menu
- Added server-sided options categories within the configuration screen, accessible by server admins (Permission Level 2) through the pause menu
- Added a "Close" button in the Guidebook
- Added a new "Lobby Area" map variable
- Added visual debuggers that can be enabled to show boundaries/placement. ([#26](https://github.com/rezelyn/Wathe-Extended/issues/26))
    - Map Variables (playArea, readyArea, lobbyArea)
    - Key Assignments
    - RTP Slots
- Implemented a lot of values from various add-ons that can be directly modified through the configuration screen:
    - Enable/Disable Morph Psychosis
    - Enable/Disable Safe Preparation Time
    - View/Edit Safe Preparation Cooldown
    - View/Edit Maximum Modifiers players can have
    - View/Edit Modifier Multiplier relative to the Killer Dividend
    - **Wathe Tweaks:**
        - Initial Civilian Income
        - Initial Neutral Income
        - Initial Killer Income
        - Enable/Disabled Killer Drop Revolver
        - Revolver Shooting Punishement Mode
    - **Roles Options:**
        - Ability Prices
        - Ability Cooldowns
        - Item Prices
        - Player Limit
        - ...and more role-specific rules

### 📋 Changes
- The **Hunter** can now buy the default Knife, alongside the Hunting Knife
- The **Cleaner** will now receive coins when using the Sulfuric Acid Barrel
- The **Kidnapper** will now gain additional coins if they personnaly kill the player they've dazed
- The **Thief** can now steal more items, including:
    - Pan
    - Blowgun
    - Poison Injector
    - Pill
    - Delusion Vial
    - Defense Vial
    - Tape
- Coroner Instinct and Conductor Instinct are now disabled by default
- Reworked the configuration screen layout, now being more intuitive and better organized
- Improved World Protection function, now being only applied within the MapVariables areas (playArea, lobbyArea, readyArea)
- RTP slots are now only allowed to be created inside the readyArea
- Improved RTP slots management, IDs are now constant and will not change numbers when deleting other slots
- Reworked Wathe Extended commands tree parents
- **Guidebook UI Improvements:**
    - Added cool new icons for role abilities
    - Role item lists are now fully complete, covering everything a role can purchase or receive at the start of a game
    - Many entry descriptions have been rewritten to be more accurate and easier to understand, better explaining what roles and modifiers do
    - Removed the Items tab from the Guidebook in favor of per-role item pages

### 🔧 Fixes
- Fixed depressed players seeing hallucination items on invisible players, such as the Phantom ([#5](https://github.com/rezelyn/Wathe-Extended/issues/5))
- Fixed a crash triggered when one of the two Lovers disconnects during a game, which caused the disconnected lover's name to incorrectly appear on the other one's dead body coroner HUD for spectators ([#23](https://github.com/rezelyn/Wathe-Extended/issues/23))
- Fixed default map's room 2 door not being correctly assigned to the "Room 2" key ([#24](https://github.com/rezelyn/Wathe-Extended/issues/24))
- Missing entries for some roles in the Guidebook ([#32](https://github.com/rezelyn/Wathe-Extended/issues/32))
- Simple Voice Chat icons not being shown by default ([#25](https://github.com/rezelyn/Wathe-Extended/issues/25))
- **Feather** modifier Slow Falling effect not being applied anymore ([#28](https://github.com/rezelyn/Wathe-Extended/issues/28))
- **Graverobber** modifier Coroner HUD not showing up when looking at dead bodies
- Noelle's Roles bonus roles not being shown in the configuration screen
- Spectators will now automatically join the spectator's voice group when joining mid-game
- **Judge**'s ability can now only be used on alive players
- Teleportation effect from the Dream Imprint will no longer be effective to Psycho Mode
- Poisoned effect will be automatically cleared upon death
- Hit detection improved for the Blowgun, Hunting Knife and Pan
- **Killer**, **Vigilante** and **Civilian** roles are now shown inside the Guidebook
- Fixed lot of typos and grammatical issues within the Guidebook and the configuration screen

# stable-3.0.132

## 🎉 Stable release!
This update introduces a wide range of new content, improvements, and fixes! Including the brand-new **Wathe Extended mod**!
> ⚠️ **Please note:** this update may break older instances of the modpack. A fresh reinstall is recommended, and the same applies to dedicated servers.
> I'm still actively learning Java and the Fabric modding environment. If you encounter any issues, please open an issue on GitHub. I'll do my best to provide support and fix bugs!

### 🆕 What's New
- New cocktails, new blocks, and refreshed models for existing ones! **(WIP)**
- The default Letter item and Stary Express's Guidebook have been replaced with a brand new, redesigned Guidebook item and UI. **(WIP)**
- Improved the logic behind randomized player teleportation at the start of a game.
- Your current role and any active modifier(s) are now shown at the top of the inventory screen.
- New configurable options are now available through the Wathe Extended config screen:
    - Toggle Player Collisions
    - Toggle Random Teleportation at Game Start
    - Toggle Interaction Safeguards
    - Toggle Roles and Modifiers
    - Toggle OOB Items Recovery
    - View and Edit Map Variables
    - View and Edit RTP Coordinate Slots
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

### 📋 Changes
- Removed lots of mods to reduce the modpack weight and improve overall performance.
- Removed the Wathe Extended datapack from default saves, as it is now a standalone mod :3
- Removed the Christmas-themed map.
- Gameplay balancing:
    - **Amnesiac**
        - Can now see dead bodies glowing effect.
        - No longer glows a different color to Killers.
    - **Lovers**
        - Lovers can now see each other glowing through walls.
        - If one Lover leaves the game, their partner will also die.
    - **Arsonist**
        - Victory now requires eliminating every player.
        - The game will now continue even after all Killers have been eliminated.
    - *Poison* has been removed from the **Executioner** and **Vulture** upon converting into a Killer.
    - Players who drops their Revolver after shooting an innocent will be unable to pick up another one towards the game.
    - Added a safe preparation time at the start of each game to prevent spawn-killing and other unfair abuses.

### 🔧 Fixes
- Fixed **Amnesiac** and **Initiate** incorrectly glowing for non-killer roles.
- Fixed **Initiate** displaying the wrong glow color to non-killers.
- Fixed **Initiate** retaining their knife after the other Initiate's death.
- Fixed **Initiate** spawning with items belonging to other neutral roles.
- Fixed **Necromancer** being able to revive roles that are disabled.
- Fixed **Lovers** being assigned to both the **Executioner** and their designated target.
- Fixed revived players remaining in the Train Spectators voice chat group.
- Fixed *Delusion Vials* incorrectly checking the poisoned player's role rather than the poisoner's.
- Fixed spectators being able to access the inventory screen.
- Fixed the train reset failing to apply in time at game start, which caused visual bugs.
- Fixed Killer Instinct night-vision not functioning correctly.
- Numerous HUD fixes, primarily addressing FancyMenu-related issues.
- Various map fixes and improvements.

# beta-2.7.132

### 🆕 What's New
- Added a new in-game Guidebook that gives information about Roles and Modifiers from the game and add-ons, can be opened through the inventory
- New roles:
    - **Trapper**
    - **Initiate**
    - **Muzzler**
    - **Starstruck**
- Added modifiers:
    - **Graverobber**
    - **Feather**

### 📋 Changes
- Gameplay balancing:
    - The **Executioner** can now pick up guns
    - The **Amnesiac** is now considered a Neutral Role
    - The **Amnesiac** can no longer pick up guns, unless they've taken a good role
    - Guns don't drop when shooting at the Amnesiac

### 🔧 Fixes
- `/setEnabledModifier` not working properly
- Various translations fixes

# beta-2.5.132

### 📋 Changes
- Gameplay balancing:
    - **Mimic** can now punch players. If they punch an innocent player and that the player ends up dying, the **Mimic** will also die.
- **Noisemaker** role is now enabled by default.
- **Allergic** modifier is now enabled by default.

### 🔧 Fixes
- Modifiers multiplier dividend being 0 instead of 1.
- **The Insane Damned Paranoid Killer** can no longer get the ***Guesser*** modifier.
- Chameleon invisibility improvements.
- Wathe Extended Datapack:
    - Some function cleaning & improvements (might do rewrite a lot of things later for optimization, still very messy)
    - Improved interaction safeguards state control.
    - Supporter Commands chat spam.
    - Objectives and scoreboard will now persist between reloads, so functions are sync properly.
    - Datapack not loading properly due to it calling YAWP commands before actual config is loaded.
        - If you have any issues with the datapack not loading stuff properly, use `/reload` command.

# beta-2.4.132

## ⛄ Small Christmas Update!
This update adds several new roles and couple fixes, and includes a new holiday theme for the lobby. Have fun and happy holidays!
<br> I'd like to also thanks everyone that'd shown support regarding the modpack development, it means a lot! Note that supporting me on Patreon is fully optional and I'm not asking for it, doing it will not grant you access to the **Supporter Commands**, consider subscribing to [doctor4t](https://www.patreon.com/cw/doctor4t)'s instead, they are the one you should support first! But still, thank you so much, all of you are awesome! ❤️

### 🆕 What's New
- New roles:
    - **Amnesiac**
    - **Arsonist**
    - **Necromancer**
    - **Avaricious**
- Added modifiers:
    - **Guesser**
    - **Tiny**
    - **Chameleon**
      <br> Makes the player slowly fading away when standing still.
    - **Lovers**
    - **Allergic**
- Added *Snowball* plates in the lobby and at the end of the train, have fun :D
- Wathe Extended datapack:
    - Toggling Debug Mode will show interactions safeguards areas, turning it off will hide them. (CAN CAUSE CLIENT LAG!)
    - Added functions to disable or re-enable interaction safeguards

### 📋 Changes
- Gameplay balancing:
    - Limited "Knife Prepare" animation max use to 5 seconds to prevent "cheesing and waiting around a corner with a knife ready" really long strat
- Disabled player cape by default since it's conflicting with some roles and modifiers mechanics.
- Blood particle effects are now client-sided, and always shown by default.
- Wathe mod updated to version [1.3.2](https://modrinth.com/mod/wathe/version/1.3.2-1.21.1)

### 🔧 Fixes
- Jesters not seeing Killer Roles in pink-colored glowing effect while using Instinct
- Train horn not starting the murder mystery game mode at night how intended
- Wathe Extended datapack:
    - Debug items being given multiples times to creative players when another player switched to creative
    - Default facing positions of the random teleportation coordinates
    - *Teleport to Train* item not being given properly to players inside the lobby area
        - The item will also be removed to players that are inside the train in the lobby to avoid cheesing the random teleportation script
    - Stuff regarding the random teleportation logic
        - Random teleportation at the start of the game now being executed after the screen fades to black
        - Rewritten coordinates that were misplaced

# beta-2.3.130

### 🆕 What's New
- Randomized player teleportation across the train at game start.

### 📋 Changes
- Wathe Extended datapack:
    - Renamed objectives/score IDs from "tmm" to "wathe"
    - Reorganized functions
    - Centralized and optimized tick functions
  > Datapack intended to work with the default world-save only, but can be modified to fit with custom maps. <br> I'll try to make the datapack variables, functions and stuff like that easier to customize in future updates for the people that might want this datapack and its features to be a part of their custom maps, otherwise have fun tweaking the mess I've created to fit your own goods! :3
- Reverted auto-start timer to 10 seconds (previously 30)
- Updated default client configuration.
- Wathe mod updated to version [1.3](https://modrinth.com/mod/wathe/version/1.3-1.21.1)

### 🔧 Fixes
- Items that fall into the void (specifically when players are pushed or jump off the train while holding items) now properly teleport to the nearest dead body
    - Added a fallback if there's no dead bodies, items will teleport to the nearest alive player
- Fixed skin transparency

# beta-2.2.126

### 🆕 What's New
- New roles:
    - **Mimic**
    - **Guesser**
- Added blood particle effects when killing players *(Disabled by default, can be enabled/disabled using <kbd>B</kbd>)*
- Added some little QoL mods
- Added a cinematic intro at launch (might be buggy, but I thought it would look cool, so I made it :3)
- New overhauled main menu layout
- New modpack icon, thumbnail and backgrounds

### 📋 Changes
- Gameplay balancing:
    - **Conductor**
        - The *Master Key* will now be seen as a *Lockpick* for others if there's less than 10 players in the game.
    - **Voodoo**
        - Shooting the Voodoo now causes revolvers to drop from the shooter.
    - Disabled the backfire mechanic
- Game auto-start timer is now 30 seconds instead of 10.
- Items that falls outside the train (in the void) will now be teleported back to the closest dead body.

### 🔧 Fixes
- Players won't take fall damage and fire damage anymore
- Fixed the fast teleportation item not being given to players that'd joined late
- Fixed knockback not being applied when punching with a *Knife*
- Fixed misplaced interaction safeguards (should be fully fixed now!)

# beta-2.1.126

### 🔧 Fixes
- Fixed outdated mod configurations, should be better now for people updating the modpack if it was already installed (sorry)

# beta-2.0.126

### 🆕 What's New
- New roles:
  - **Vulture**
  - **Recaller**
  - **Better Vigilante**
- New items:
  - **Defense Vial**
    - Grants the drinker one extra hit (similar to Psycho Armor).
  - **Delusion Vial**
    - Mimics poison by applying visual effects but does not cause death.
- Added an item that fast teleports you to the Lobby train
- Added a system preventing players from standing close/inside train doors before the game starts (wich was allowing them to exit the train without needing a Master Key, Lockpick, or Crowbar).

### 📋 Changes
- Gameplay balancing:
  - **Bartender**
    - Can see poison and poisoned players.
    - Can now purchase a *Defense Vial* for 100 coins (50 coins per task).
  - **Jester**
    - Can now see the round timer.
    - Can shoot with their fake gun.
    - Gains coins passively over time.
    - Can purchase *Firecrackers*, *Lockpicks*, *Notes*, and *Delusion Vials*.
  - **Coroner**
    - Can see the roles of dead players (also visible to spectators).
  - **Executionner**
    - Gains coins passively over time.
    - Can purchase *Firecrackers*, *Lockpicks*, *Notes*, and *Delusion Vials*.
    - Their target's skin can now be seen on the UI
  - **Noisemaker**
    - Gains coins passively over time.
    - Can purchase *Firecrackers* for 75 coins.
  - When innocents shoots other innocents, assuming they've passed the backfire chance, they drop their gun and won't be able to pick one up until the end of the game
- **Phantom** is now enabled by default.
- **Executioner** is now enabled by default.
- The **Executioner**'s targets are now visible to spectators when hovering over the **Executioner**.

### 🔧 Fixes
- Some HUD/Menus fixes
- Removed Fresh Moves (With animated eyes) due to compatibility issues
- Removed Essential mod due to compatibility issues, can be added back if wanted but might break some stuff.
- Interaction safeguards are now fully completed, no more glitch abuses with trapdoors and other stuff!
- Fixed the ready-area boundaries.

# alpha-1.6.126

### 🆕 What's New
- Added Noelle's Roles add-on for TMM (see the description for more information about the roles)
- Added default mod configuration (in case some people want to change client-side settings)

### 🔧 Fixes
- Some HUD/Menus tweaks and fixes
- Various map fixes
- *...and maybe some more stuff that I've forgotten*

# alpha-1.5.126

### 🆕 What's New
- Added interaction safeguards to the lobby and within the playable train **(WIP)**

### 📋 Changes
- Removed unneeded modded items and blocks
- Adjusted shader settings to reduce blurriness and improve brightness **(WIP, still experiencing notable lag)**

### 🔧 Fixes
- Further reorganized and polished the train’s furniture and overall layout
- Minor tweaks to various mod configurations

# alpha-1.4.126

### 📋 Changes
- Removed unneeded mods

### 🔧 Fixes
- Fixed collisions, they should be working correctly now
- Room key assignments fixed (for real this time)

# alpha-1.3.126

### 📋 Changes
- Harpy Express mod updated to the latest release

### 🔧 Fixes
- Room keys assignment fixed
- Missing blocks and furniture models fixed
- Various HUD tweaks and fixes

# alpha-1.2.126

### 🆕 What's New
- Fully revamped HUD and menu elements with new themes and visual styles
- Added a default client configuration
- Added Shader configuration (WIP, shaders currently cause significant lag)
- Updated the world save file with a refined lobby and ongoing train customization (WIP)