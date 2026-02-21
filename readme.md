# FFXDataParser

## Getting started

1. Get your own legal copy of Final Fantasy X/X-2 Remaster
2. Use **vbfextract** to extract everything from `FFX_Data.vbf`
3. Copy the extracted `ffx_ps2` folder into `src/main/resources/` or put it elsewhere locally (will need to set the `SourcesRootPath` to wherever you put it)
4. Build and run the parser with modes of your choice

## Build & Usage

Building the parser is done by running the following command line in this folder:

`mvn package`

This requires having maven installed on your command line PATH; if you have it installed locally somewhere else, replace the 'mvn' with the complete path to the mvn(.exe) file.

By default, this will create a file inside the `target` folder named `FFXDataParser-1.0-SNAPSHOT.jar`.
You can then run the parser with the following command:

`java -jar target/FFXDataParser-1.0-SNAPSHOT.jar <SourcesRootPath> <ParserMode> [<ModeArgument>] ...`

`SourcesRootPath` must be a path to a folder that contains within it the `ffx_ps2` folder and must end in a `/`. All other paths are relative to this one. If you set it to `.` it will automatically resolve to `src/main/resources/` 

## Parser Modes

| ParserMode                          | Options                               | Description                                                |
|--------------------------------------|:--------------------------------------|------------------------------------------------------------|
| `GREP` | `plain text string` | Convert a string into FFX byte encoding and prints command lines for GREP-ing for them |
| `TRANSLATE` | `bytes` | Converts FFX encoded string bytes into a plain text string |
| `READ_ALL_COMMANDS` | | Prints information on all commands (from `command.bin`, `monmagic1.bin`, `monmagic2.bin` and `item.bin`) |
| `READ_KEY_ITEMS` | | Prints information on key items (from `important.bin`) |
| `READ_GEAR_ABILITIES` | | Prints information on auto-abilities for gear (from `a_ability.bin`) |
| `READ_TREASURES` | | Prints information on treasure pickups in the game (from `takara.bin`) |
| `READ_GEAR_SHOPS` | | Prints information on gear shops in the game (from `arms_shop.bin`) |
| `READ_ITEM_SHOPS` | | Prints information on item shops in the game (from `item_shop.bin`) |
| `READ_MONSTER_LOCALIZATIONS` | `<locale, ex: us>` | Prints localized strings for monsters (from `monster1/2/3.bin`) |
| `READ_WEAPON_FILE` | `<file1> [<file2>] ...` | Reads weapons from a dedicated weapon file (known valid target files are `weapon.bin`, `buki_get.bin`, `shop_arms.bin`) |
| `READ_STRING_FILE` | `<file1> [<file2>] ...` | Reads all strings (indexed) from a dedicated string file                                                           |
| `READ_SPHERE_GRID_NODE_TYPES` | | Reads types of spheres and nodes on the sphere grid (from `sphere.bin` and `panel.bin`) |
| `READ_SPHERE_GRID_LAYOUT` | `<layoutFile, ex: dat02.dat> <nodeContentsFile, ex: dat10.dat>` | Reads out a sphere grid's layout including its actual node contents. |
| `READ_CUSTOMIZATIONS` | | Reads customizations for gear and aeons. (from `kaizou.bin` and `sum_grow.bin`) |
| `PARSE_ATEL_FILE` | `<file/folder1> [<file/folder2>] ...` | Reads the ATEL script from a file (event, battle or monster) and prints it to the console. If given a folder, recurses through all atel files within. |
| `PARSE_MONSTER` | `<mIndex1, ex: 234> [<mIndex2, ex: 235>] ...` | Reads the monster files with the given decimal index and prints their information. |
| `PARSE_ALL_MONSTERS` | | Reads all monster files (m000-m360) and prints their information. | 
| `PARSE_BATTLE` | `<btlIndex1, ex: sins04_07> [<btlIndex2, ex: zzzz03_33>] ...` | Reads the battle files with the given name and prints their information. | 
| `PARSE_ALL_BATTLES` | | Reads all battle files and prints their information. | 
| `PARSE_EVENT` | `<evIndex1, ex: lmyt0000> [<evIndex2, ex: kami0300>] ...` | Reads the event files with the given name and prints their information. |
| `PARSE_ALL_EVENTS` | | Reads all event files (except blitzball events) and prints their information. |
| `READ_MACROS` | | Reads macro strings. (from `macrodic.dcp`) |
| `READ_ENCOUNTER_TABLE` | | Reads the encounter tables. (from `btl.bin`) |
| `READ_MIX_TABLE` | | Reads the mix table. (from `prepare.bin`) |
| `READ_WEAPON_NAMES` | | Reads the weapon names/appearance table. (from `w_name.bin`) |
| `READ_PC_STATS` | | Reads player character initial stats and growth values. (from `ply_save.bin` and `ply_rom.bin`) |
| `READ_CTB_BASE` | | Reads the CTB to Tickspeed/ICV table. (from `ctb_base.bin`) |
| `MAKE_EDITS` | | Apply string edits from csv files and write localized files in all languages into the `mods` folder. |
| `RECOMPILE` | `<scriptId1, ex: lmyt000> [<scriptIndex2, ex: mtgz08_00>] [<scriptIndex3, ex: m111>] ...` | Recompile the given script/s and save them to the mods folder. Intended mainly for internal testing of the recompilation feature. |
| `ADD_ATEL_SPACE` | `<type, "event"/"battle"/"monster"> <scriptId, ex: lmyt0000> <workerIndex in hex, ex: 0A> <count in decimal, ex: 900>` | Recompile the given script while also adding a new entry point to the given worker that consists of <count> bytes of 00. |
| `REMAKE_SIZE_TABLE` | | Rewrites the sizetbl.vita.bin (specifically the monster file section) according to actual monster file lengths. Output is written into mods folder. |


### Example usages:

`java -jar target/FFXDataParser-1.2-SNAPSHOT.jar . GREP Time to go, escargot!`

`java -jar target/FFXDataParser-1.2-SNAPSHOT.jar . READ_ALL_ABILITIES`

`java -jar target/FFXDataParser-1.2-SNAPSHOT.jar . PARSE_ATEL_FILE ffx_ps2/ffx/master/jppc/event/obj/do/dome0600/dome0600.ebp`

`java -jar target/FFXDataParser-1.2-SNAPSHOT.jar . PARSE_MONSTER 105`

`java -jar target/FFXDataParser-1.2-SNAPSHOT.jar . ADD_ATEL_SPACE event genk0000 3 900`

## Expected CSV Formats for MAKE_EDITS

The parser mode `MAKE_EDITS` expects .csv files with specific names and formats to be placed inside the resources/edits folder.

The mode also unifies non-string data among affected objects, taking the DEFAULT_LOCALIZATION (normally `us`) as the authoritative copy. For example, if an attack's power is changed in the new_uspc version of it, this will be propagated to all languages.

CSVs are generally expected to have one header line that contains identifiers that assigns certain columns specific roles, and then have 1 line per edit to apply. Columns with no valid identifier in the header line are simply ignored.

The following files can be placed, with the following expected columns (i.e. their header line must consist of the given string):

- `commands.csv` - To make edits to name or description of commands.
  - `id` - Contains command ID written out in hexadecimal, ex. `3037` or `404B` etc.
  - `type` - Must contain either `name` or `description`.
  - `direct copy` - Optionally contains another command ID written out in hexadecimal, in which case it simply clones the string from the given command and does nothing else.
  - `us`/`de`/`fr`/`sp`/`it`/`jp`/`ch`/`kr` - String to insert in the given language.
    - If left empty, no edit is made for that locale.
    - The string must only contain characters valid in the given locale.
    - Newlines must be written as `{\n}`, real newlines are forbidden.
    - Other string commands can also be written using {bracket} notation (you can find the interpreted commands in StringHelper.java functions)
- `autoAbilities.csv` - To make edits to name or description of auto abilities.
  - Exact same columns as commands.csv, IDs are expected to use format `8...`, ex. `800B`
- `keyItems.csv` - To make edits to name or description of key items.
  - Exact same columns as commands.csv, IDs are expected to use format `A...`, ex. `A029`
- `monsterNameSensor.csv` - To make edits to name or sensor texts of monsters.
  - `id` - Contains monster ID written out in decimal, ex. `37` or `251` etc.
  - `type` - Either `name` or `sensor`. (`scan` also works)
  - `direct copy` - Optionally contains another monster ID written out in decimal, in which case it simply clones the string from the given monster and does nothing else.
  - `us`/`de`/`fr`/`sp`/`it`/`jp`/`ch`/`kr` - String to insert in the given language, usual rules apply.
- `monsterScan.csv` - To make edits to scan texts of monsters.
  - Contains the same columns as monsterNameSensor.csv. 
  - The `type` column must still be present and should say `scan` for each row.
  - Note: This file is parsed identically to the monsterNameSensor.csv; all three types (`name`, `sensor`, `scan`) actually work in all three, I just found it more convenient to separate name/sensor from scan texts.
- `events.csv` - To make edits to event/field script file texts.
  - `id` - Contains event ID written out as a string, ex. `bika0000` or `mtgz0700` etc.
  - `string index` - Numeric ID of the given string in decimal
  - `us`/`de`/`fr`/`sp`/`it`/`jp`/`ch`/`kr` - String to insert in the given language, usual rules apply.
- `battles.csv` - To make edits to encounter/battle script file texts.
  - `id` - Contains battle ID written out as a string, ex. `cdsp07_00` or `stbv01_10` etc.
  - `string index` - Numeric ID of the given string in decimal
  - `us`/`de`/`fr`/`sp`/`it`/`jp`/`ch`/`kr` - String to insert in the given language, usual rules apply.