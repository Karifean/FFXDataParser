### Preparation

1. Use **vbfextract** to extract all files from `FFX_Data.vbf`
2. Copy the extracted `ffx_ps2` folder into `src/main/resources/`

### Usage

`java -jar src/main/java/main/Main.jar <ParsingModeId> <FileOrDirectoryToParse> [<FileOrDirectoryToParse2>] [<FileOrDirectoryToParse3>] ...`

Example: `java -jar src/main/java/main/Main.jar 6 "ffx_ps2\ffx\master\jppc\event\obj\do\dome0600\dome0600.ebp"`

#### ParsingModes (numerical)

| Id | ParsingMode                  | Description |
|----|------------------------------|-------------|
| 1  | MODE_READ_TEXT_FROM_FILES    |             |
| 2  | MODE_GREP                    |             |
| 3  | MODE_TRANSLATE               |             |
| 4  | MODE_READ_ALL_ABILITIES      |             |
| 5  | MODE_READ_KEY_ITEMS          |             |
| 6  | MODE_READ_MONSTER_AI         |             |
| 7  | MODE_RUN_SPECIFIC_MONSTER_AI |             |
| 8  | MODE_READ_TREASURES          |             |
| 9  | MODE_READ_WEAPON_PICKUPS     |             |
| 10 | MODE_FIND_EQUAL_FILES        |             |
| 11 | MODE_READ_STRING_FILE        |             |
| 12 | MODE_READ_GEAR_ABILITIES     |             | 