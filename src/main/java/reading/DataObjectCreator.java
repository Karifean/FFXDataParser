package reading;

import java.io.IOException;

public interface DataObjectCreator<T> {
    T create(int[] bytes, int[] stringBytes) throws IOException;
}
