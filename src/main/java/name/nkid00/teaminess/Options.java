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

    /**
     * Seconds ihe action of teleporting will be done in.
     * <p>U. seconds</p>
     */
    public long teleportInterval = 3;
    /**
     * Live time (Cooling time) of the request of the teleportation.
     * <p>U. seconds</p>
     */
    public long requestAliveTime = 120;
    /**
     * Trust own team so that the teleportation request would be automatically accepted and done immediately
     * if it was sent by the player in the same team.
     */
    public boolean trustOwnTeam = true;
    /**
     * Trust other team so that the teleportation request would be automatically accepted and done immediately
     * if it was sent by the player in the other team.
     */
    public boolean trustOtherTeams = false;
    /**
     * Make it legal to use the charactor formatted in <code>\u00A7</code> in the game.
     */
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
