package model;

import main.DataReadingManager;

/**
 * monster1.bin (only uses Name/Sensor/Scan text strings)
 * monster2.bin (only uses Name/Sensor/Scan text strings)
 * monster3.bin (only uses Name/Sensor/Scan text strings)
 */
public class MonsterLocalizationDataObject extends MonsterStatDataObject {

    private String localization = DataReadingManager.DEFAULT_LOCALIZATION;

    public MonsterLocalizationDataObject(int[] bytes, int[] stringBytes, String localization) {
        super(bytes, stringBytes, localization);
        this.localization = localization;
    }

    @Override
    public String toString() {
        return super.getStrings(localization);
    }
}
