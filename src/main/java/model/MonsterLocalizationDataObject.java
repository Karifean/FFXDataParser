package model;

import main.DataAccess;
import main.DataReadingManager;
import main.StringHelper;
import script.model.StackObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
