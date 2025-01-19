package model;

import main.DataReadingManager;

public interface Nameable {
    default String getName() {
        return getName(DataReadingManager.DEFAULT_LOCALIZATION);
    }
    String getName(String localization);
}
