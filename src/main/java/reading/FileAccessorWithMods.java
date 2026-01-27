package reading;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileAccessorWithMods {
    public static final String RESOURCES_ROOT = "src/main/resources/";
    public static String GAME_FILES_ROOT = RESOURCES_ROOT;
    public static final String MODS_FOLDER = "mods/";
    private static final boolean DISABLE_MODS = false;
    private static final String CSV_LINE_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    public static File getRealFile(String path) {
        return new File(GAME_FILES_ROOT + path);
    }

    public static File getModdedFile(String path) {
        return new File(GAME_FILES_ROOT + MODS_FOLDER + path);
    }

    public static File resolveFile(String path, boolean print) {
        if (print) {
            System.out.println("--- " + path + " ---");
        }
        File moddedFile = DISABLE_MODS ? null : getModdedFile(path);
        if (moddedFile != null && moddedFile.exists()) {
            return moddedFile;
        } else {
            return getRealFile(path);
        }
    }

    public static DataInputStream readFile(File file) throws FileNotFoundException {
        return new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    }

    public static List<String> textFileToLineList(File file) throws IOException {
        List<String> list = new ArrayList<>();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
            }
        }
        return list;
    }

    public static List<String[]> csvToList(File file) throws IOException {
        List<String[]> list = new ArrayList<>();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] split = line.split(CSV_LINE_REGEX);
                    for (int i = 0; i < split.length; i++) {
                        String single = split[i];
                        if (single.startsWith("\"")) {
                            split[i] = single.substring(1, single.length() - 1);
                        }
                    }
                    list.add(split);
                }
            }
        }
        return list;
    }

    public static void writeByteArrayToMods(String path, int[] bytes) {
        writeByteArrayToFile(GAME_FILES_ROOT + MODS_FOLDER + path, bytes);
    }

    public static void writeByteArrayToFile(String path, int[] bytes) {
        createDirectories(path);
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

    public static void writeStringToMods(String path, String string) {
        writeStringToFile(GAME_FILES_ROOT + MODS_FOLDER + path, string);
    }

    public static void writeStringToFile(String path, String string) {
        createDirectories(path);
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            fileOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Failed to write file");
            e.printStackTrace();
        }
    }

    private static void createDirectories(String path) {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1) {
            return;
        }
        try {
            Files.createDirectories(Paths.get(path.substring(0, lastSlash)));
        } catch (IOException e) {
            System.err.println("Failed to create directories");
            e.printStackTrace();
        }

    }
}
