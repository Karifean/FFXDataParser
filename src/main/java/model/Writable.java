package model;

import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;

import java.util.stream.Stream;

public interface Writable {
    Stream<KeyedString> streamKeyedStrings(String localization);
    LocalizedKeyedStringObject getKeyedString(String title);
    int[] toBytes(String localization);
}
