package name.nkid00.teaminess;

import java.io.File;

// stores dynamic data in `teaminess/*.json`
public class Data {
    public static File file;

    public static void load() {
        Teaminess.LOGGER.info("Loading data");
        if (Teaminess.data == null) {
            Teaminess.data = new Data();
        }
        // TODO: load data
    }

    public static void save() {
        Teaminess.LOGGER.info("Saving data");
        // TODO: save data
    }
}
