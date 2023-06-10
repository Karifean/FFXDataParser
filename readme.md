# FFXDataParser

## Getting started

1. Get your own legal copy of Final Fantasy X/X-2 Remaster
2. Use **vbfextract** to extract everything from `FFX_Data.vbf`
3. Copy the extracted `ffx_ps2` folder into `src/main/resources/`
4. Run the parser

## Usage

`java -jar src/main/java/main/Main.jar <ParsingMode> [<ParsingOptions>] ...`

Example: `java -jar src/main/java/main/Main.jar PARSE_SCRIPT_FILE ffx_ps2\ffx\master\jppc\event\obj\do\dome0600\dome0600.ebp`

#### ParsingModes

| ParsingMode                          | Options                               | Description                                                |
|--------------------------------------|:--------------------------------------|------------------------------------------------------------|
| `GREP`                          | `plain text string` | Convert a string into FFX byte encoding and prints command lines for GREP-ing for them |
| `TRANSLATE`                     | `bytes` | Converts FFX encoded string bytes into a plain text string |
| `READ_ALL_ABILITIES`            | | Prints information on all abilities (from `command.bin`, `monmagic1.bin`, `monmagic2.bin` and `item.bin`) |
| `READ_KEY_ITEMS`                | | Prints information on key items (from `important.bin`) |
| `READ_GEAR_ABILITIES`           | | Prints information on auto-abilities for gear (from `a_ability.bin`) |
| `READ_TREASURES`                | | Prints information on treasure pickups in the game (from `takara.bin`) |
| `READ_WEAPON_FILE`           | `<file/folder1> [<file/folder2>] ...` | Reads weapons from a dedicated weapon file (known valid target files are `weapon.bin`, `buki_get.bin`, `shop_arms.bin`) |
| `READ_STRING_FILE`              | `<file/folder1> [<file/folder2>] ...` | Reads all strings (indexed) from a dedicated string file                                                           |
| `PARSE_SCRIPT_FILE`     | `<file/folder1> [<file/folder2>] ...` | Reads the script from a file and prints it to the console. If given a folder, recurses through all script files within. |
| `PARSE_MONSTER`            | `<mIndex1, ex: 234> [<mIndex2, ex: 235>] ...` | Reads the monster files with the given decimal index and prints their information. |
| `PARSE_ENCOUNTER`          | `<btlIndex1, ex: sins04_07> [<btlIndex2, ex: zzzz03_33>] ...` | Reads the encounter files with the given name and prints their information. | 
| `PARSE_EVENT`              | `<evIndex1, ex: lmyt0000> [<evIndex2, ex: kami0300>] ...` | Reads the event files with the given name and prints their information. | 