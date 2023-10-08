package name.nkid00.teaminess;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Store options in {@code teaminess/options.json} in config directory.
 */
public class Options {
    public static File file;

    /**
     * Seconds ihe action of teleporting will be done in.
     * <p>
     * <b>U.</b> seconds
     * </p>
     */
    public long teleportInterval = 3;
    /**
     * Live time (Cooling time) of the teleportation request.
     * <p>
     * <b>U.</b> seconds
     * </p>
     */
    public long requestAliveTime = 120;
    /**
     * Trust own team so that the teleportation request would be automatically accepted and done
     * immediately if it was sent by the player in the same team.
     * <p>
     * In other words, the player as target will skip the confirm and deafaultly accept the
     * teleportation request.
     * </p>
     */
    public boolean trustOwnTeam = true;
    /**
     * Trust other team so that the teleportation request would be automatically accepted and done
     * immediately if it was sent by the player in the other team.
     * <p>
     * In other words, the player as target will skip the confirm and defaultly accept the
     * teleportation request.
     * </p>
     */
    public boolean trustOtherTeams = false;
    /**
     * Make it legal to use the charactor in format {@code \u00A7} in the game.
     */
    public boolean allowFormattingCode = true;

    /**
     * Load the options file.
     */
    public static void load() {
        Teaminess.LOGGER.info("Loading options");
        try (var reader = new FileReader(file)) {
            Teaminess.options = Teaminess.GSON.fromJson(reader, Options.class);
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            Teaminess.LOGGER.info("Generating default options");
            Teaminess.options = new Options();
        }
    }

    /**
     * Save the options file.
     */
    public static void save() {
        Teaminess.LOGGER.info("Saving options");
        try (var writer = new FileWriter(file)) {
            Teaminess.GSON.toJson(Teaminess.options, writer);
        } catch (IOException | JsonIOException e2) {
            Teaminess.LOGGER.warn("Failed to save options");
        }
    }
}
