package reading;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    public static DataInputStream readFile(File file) throws FileNotFoundException {
        return new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    }

    public static void writeByteArrayToFile(String path, int[] bytes) {
        try {
            Files.createDirectories(Paths.get(path.substring(0, path.lastIndexOf('/'))));
        } catch (IOException e) {
            System.err.println("Failed to create directories");
            e.printStackTrace();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            for (int aByte : bytes) {
                fileOutputStream.write(aByte);
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Failed to write file");
            e.printStackTrace();
        }
    }
}
