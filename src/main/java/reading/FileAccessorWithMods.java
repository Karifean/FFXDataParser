package reading;

import java.io.*;

public class FileAccessorWithMods {
    public static final String RESOURCES_ROOT = "src/main/resources/";
    public static String GAME_FILES_ROOT = RESOURCES_ROOT;
    public static final String MODS_FOLDER = "mods/";
    private static final boolean DISABLE_MODS = false;

    public static File getRealFile(String path) {
        return new File(GAME_FILES_ROOT + path);
    }

    public static File getModdedFile(String path) {
        return new File(GAME_FILES_ROOT + MODS_FOLDER + path);
    }

    public static File resolveFile(String path, boolean print) {
        File file;
        if (DISABLE_MODS) {
            file = getRealFile(path);
        } else {
            file = getModdedFile(path);
            if (!file.exists()) {
                file = getRealFile(path);
            }
        }
        if (print) {
            System.out.println("--- " + file.getPath() + " ---");
        }
        return file;
    }

    public static DataInputStream readFile(String path) throws FileNotFoundException {
        return readFile(resolveFile(path, false));
    }

    public static DataInputStream readFile(File file) throws FileNotFoundException {
        return new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    }
}
