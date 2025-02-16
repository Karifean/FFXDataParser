package reading;

import java.io.IOException;

public interface DataObjectCreator<T> {
    T create(int[] bytes, int[] stringBytes, String localization) throws IOException;
}
