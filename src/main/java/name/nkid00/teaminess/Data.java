package name.nkid00.minimaltp;

import java.io.File;

// stores dynamic data in `minimaltp/*.json`
public class Data {
    public static File file;

    public static void load() {
        MinimalTp.LOGGER.info("Loading data");
        if (MinimalTp.data == null) {
            MinimalTp.data = new Data();
        }
        // TODO: load data
    }

    public static void save() {
        MinimalTp.LOGGER.info("Saving data");
        // TODO: save data
    }
}
