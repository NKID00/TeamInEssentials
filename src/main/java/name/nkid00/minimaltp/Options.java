package name.nkid00.minimaltp;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

// stores options in `minimaltp/options.json`
public class Options {
    public static File file;

    public long teleportInterval = 3; // seconds
    public long requestExpirationInterval = 120; // seconds
    public boolean immediateTeleportationInTeam = true;
    public boolean immediateTeleportationBetweenTeam = false;
    public boolean allowFormattingCode = true;

    public static void reload() {
        MinimalTp.LOGGER.info("Loading options");
        try (var reader = new FileReader(file)) {
            MinimalTp.options = MinimalTp.GSON.fromJson(reader, Options.class);
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            MinimalTp.LOGGER.info("Generating default options");
            MinimalTp.options = new Options();
        }
        MinimalTp.LOGGER.info("Formatting options");
        try (var writer = new FileWriter(file)) {
            MinimalTp.GSON.toJson(MinimalTp.options, writer);
        } catch (IOException | JsonIOException e2) {
            throw new CrashException(new CrashReport("配置文件生成失败", e2));
        }
    }
}
