package name.nkid00.teaminess;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

// stores options in `teaminess/options.json`
public class Options {
    public static File file;

    public long teleportInterval = 3; // seconds
    public long responseInterval = 120; // seconds
    public boolean confirmInTeam = false;
    public boolean confirmBetweenTeams = true;
    public boolean allowFormattingCode = true;

    public static void load() {
        Teaminess.LOGGER.info("Loading options");
        try (var reader = new FileReader(file)) {
            Teaminess.options = Teaminess.GSON.fromJson(reader, Options.class);
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            Teaminess.LOGGER.info("Generating default options");
            Teaminess.options = new Options();
        }
    }

    public static void save() {
        Teaminess.LOGGER.info("Saving options");
        try (var writer = new FileWriter(file)) {
            Teaminess.GSON.toJson(Teaminess.options, writer);
        } catch (IOException | JsonIOException e2) {
            Teaminess.LOGGER.warn("Failed to save options");
        }
    }
}
