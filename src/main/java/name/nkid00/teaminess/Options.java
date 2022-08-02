package name.nkid00.teaminess;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

// stores options in `teaminess/options.json`
public class Options {
    public static File file;

    public long teleportInterval = 3; // seconds
    public long requestExpirationInterval = 120; // seconds
    public boolean immediateTeleportationInTeam = true;
    public boolean immediateTeleportationBetweenTeam = false;
    public boolean allowFormattingCode = true;

    public static void load() {
        Teaminess.LOGGER.info("Loading options");
        try (var reader = new FileReader(file)) {
            Teaminess.options = Teaminess.GSON.fromJson(reader, Options.class);
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            Teaminess.LOGGER.info("Generating default options");
            Teaminess.options = new Options();
        }
        Teaminess.LOGGER.info("Formatting options");
        try (var writer = new FileWriter(file)) {
            Teaminess.GSON.toJson(Teaminess.options, writer);
        } catch (IOException | JsonIOException e2) {
            throw new CrashException(new CrashReport("配置文件生成失败", e2));
        }
    }
}
