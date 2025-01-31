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

#### ParserModes

| ParserMode                          | Options                               | Description                                                |
|--------------------------------------|:--------------------------------------|------------------------------------------------------------|
| `GREP`                          | `plain text string` | Convert a string into FFX byte encoding and prints command lines for GREP-ing for them |
| `TRANSLATE`                     | `bytes` | Converts FFX encoded string bytes into a plain text string |
| `READ_ALL_ABILITIES`            | | Prints information on all abilities (from `command.bin`, `monmagic1.bin`, `monmagic2.bin` and `item.bin`) |
| `READ_KEY_ITEMS`                | | Prints information on key items (from `important.bin`) |
| `READ_GEAR_ABILITIES`           | | Prints information on auto-abilities for gear (from `a_ability.bin`) |
| `READ_TREASURES`                | | Prints information on treasure pickups in the game (from `takara.bin`) |
| `READ_GEAR_SHOPS`                | | Prints information on gear shops in the game (from `arms_shop.bin`) |
| `READ_ITEM_SHOPS`                | | Prints information on item shops in the game (from `item_shop.bin`) |
| `READ_MONSTER_LOCALIZATIONS`     | `<locale, ex: us>` | Prints localized strings for monsters (from `monster1/2/3.bin`) |
| `READ_WEAPON_FILE`           | `<file1> [<file2>] ...` | Reads weapons from a dedicated weapon file (known valid target files are `weapon.bin`, `buki_get.bin`, `shop_arms.bin`) |
| `READ_STRING_FILE`              | `<file1> [<file2>] ...` | Reads all strings (indexed) from a dedicated string file                                                           |
| `READ_SPHERE_GRID_NODE_TYPES`              | | Reads types of spheres and nodes on the sphere grid (from `sphere.bin` and `panel.bin`) |
| `READ_SPHERE_GRID_LAYOUT`              | `<layoutFile, ex: dat02.dat> <nodeContentsFile, ex: dat10.dat>` | Reads out a sphere grid's layout including its actual node contents. |
| `READ_CUSTOMIZATIONS`              | | Reads customizations for gear and aeons. (from `kaizou.bin` and `sum_grow.bin`) |
| `PARSE_ATEL_FILE`     | `<file/folder1> [<file/folder2>] ...` | Reads the ATEL script from a file (event, encounter or monster) and prints it to the console. If given a folder, recurses through all atel files within. |
| `PARSE_MONSTER`            | `<mIndex1, ex: 234> [<mIndex2, ex: 235>] ...` | Reads the monster files with the given decimal index and prints their information. |
| `PARSE_ENCOUNTER`          | `<btlIndex1, ex: sins04_07> [<btlIndex2, ex: zzzz03_33>] ...` | Reads the encounter files with the given name and prints their information. | 
| `PARSE_ALL_ENCOUNTERS`          | | Reads all encounter files and prints their information. | 
| `PARSE_EVENT`              | `<evIndex1, ex: lmyt0000> [<evIndex2, ex: kami0300>] ...` | Reads the event files with the given name and prints their information. |
| `PARSE_ALL_EVENTS`              | | Reads all event files (except blitzball events) and prints their information. |


#### Example usages:

`java -jar target/FFXDataParser-1.0-SNAPSHOT.jar . GREP Time to go, escargot!`

`java -jar target/FFXDataParser-1.0-SNAPSHOT.jar . READ_ALL_ABILITIES`

`java -jar target/FFXDataParser-1.0-SNAPSHOT.jar . PARSE_ATEL_FILE ffx_ps2/ffx/master/jppc/event/obj/do/dome0600/dome0600.ebp`

`java -jar target/FFXDataParser-1.0-SNAPSHOT.jar . PARSE_MONSTER 105`
