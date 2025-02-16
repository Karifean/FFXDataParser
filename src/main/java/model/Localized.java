package model;

import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;

import java.util.stream.Stream;

public interface Localized<T extends Localized<T>> {
    void setLocalizations(T localization);
}
