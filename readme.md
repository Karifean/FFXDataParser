# FFXDataParser

## Getting started

1. Get your own legal copy of Final Fantasy X/X-2 Remaster
2. Use **vbfextract** to extract everything from `FFX_Data.vbf`
3. Copy the extracted `ffx_ps2` folder into `src/main/resources/`
4. Run the parser

## Usage

`java -jar src/main/java/main/Main.jar <ParsingModeId> [<ParsingOptions>] ...`

Example: `java -jar src/main/java/main/Main.jar 14 "ffx_ps2\ffx\master\jppc\event\obj\do\dome0600\dome0600.ebp"`

#### ParsingModes (numerical)

| Id | ParsingMode                          | Options                               | Description                                                |
|----|--------------------------------------|:--------------------------------------|------------------------------------------------------------|
| 1  | `MODE_GREP`                          |                                       |                                                            |
| 2  | `MODE_TRANSLATE`                     |                                       |                                                            |
| 4  | `MODE_READ_ALL_ABILITIES`            |                                       |                                                            |
| 5  | `MODE_READ_KEY_ITEMS`                |                                       |                                                            |
| 6  | `MODE_READ_GEAR_ABILITIES`           |                                       |                                                            |
| 7  | `MODE_READ_SPECIFIC_MONSTER_WITH_AI` |                                       |                                                            |
| 8  | `MODE_READ_TREASURES`                |                                       |                                                            |
| 9  | `MODE_READ_WEAPON_PICKUPS`           |                                       |                                                            |
| 10 | `MODE_READ_STRING_FILE`              |                                       |                                                            |
| 14 | `MODE_PARSE_GENERIC_SCRIPT_FILE`     | `<file/folder1> [<file/folder2>] ...` | Reads the script from a file and prints it to the console. |
| 15 | `MODE_PARSE_MONSTER_FILE`            |                                       |                                                            |
| 16 | `MODE_PARSE_ENCOUNTER_FILE`          |                                       |                                                            | 
| 17 | `MODE_PARSE_EVENT_FILE`              |                                       |                                                            | 