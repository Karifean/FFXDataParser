package main;

import java.io.*;

public class FileAccessorWithMods {
    public static final String RESOURCES_ROOT = "src/main/resources/";
    public static final String MODS_ROOT = RESOURCES_ROOT + "mods/";
    private static final boolean DISABLE_MODS = false;

    public static File getRealFile(String path) {
        return new File(RESOURCES_ROOT + path);
    }

    public static File getModdedFile(String path) {
        return new File(MODS_ROOT + path);
    }

    public static File resolveFile(String path) {
        if (DISABLE_MODS) {
            return getRealFile(path);
        }
        File file = getModdedFile(path);
        if (!file.exists()) {
            return getRealFile(path);
        }
        return file;
    }

    public static DataInputStream readFile(String path) throws FileNotFoundException {
        return readFile(resolveFile(path));
    }

    public static DataInputStream readFile(File file) throws FileNotFoundException {
        return new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    }
}
