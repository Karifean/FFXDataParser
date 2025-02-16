package reading;

import model.Localized;

import java.util.function.IntFunction;

import static main.DataReadingManager.*;

public class LocalizedDataFileReader<T extends Localized<? super T>> extends DataFileReader<T> {

    public LocalizedDataFileReader(DataObjectCreator<T> objectCreator, IntFunction<T[]> arrayCreator) {
        super(objectCreator, arrayCreator);
    }

    public T[] read(String path, boolean print) {
        T[] objects = toArray(getLocalizationRoot(DEFAULT_LOCALIZATION) + path, DEFAULT_LOCALIZATION, print);
        if (objects == null) {
            return null;
        }
        LOCALIZATIONS.forEach((key, name) -> {
            T[] localizations = toArray(getLocalizationRoot(key) + path, key, false);
            if (localizations != null) {
                for (int i = 0; i < localizations.length && i < objects.length; i++) {
                    if (objects[i] != null && localizations[i] != null) {
                        objects[i].setLocalizations(localizations[i]);
                    }
                }
            }
        });
        return objects;
    }
}
