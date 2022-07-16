package name.nkid00.minimaltp;

import java.io.File;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

// stores dynamic data in `minimaltp/*.json`
public class Data {
    public static File file;

    public static void load() {
        MinimalTp.LOGGER.info("Loading data");
        if (MinimalTp.database == null) {
            MinimalTp.database = new Data();
        }
        // TODO: load data
    }

    public static void save() {
        MinimalTp.LOGGER.info("Saving data");
        // TODO: save data
    }
}
