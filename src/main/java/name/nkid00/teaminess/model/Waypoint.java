package name.nkid00.teaminess.model;

import net.minecraft.text.Text;

/**
 * An object which contains information about one location and data about its record.
 */
public class Waypoint {
    private Location location;
    private Text recorder;
    private int colorId; // Refer to enum Formatting

    public Location getLocation() {
        return location;
    }

    public Text getRecorder() {
        return recorder;
    }

    public int getColorId() {
        return colorId;
    }

    public static Waypoint EMPTY = new Waypoint(null, Text.literal("Teaminess Mod"));

    public Waypoint(Location location, Text recorder) {
        this(location, recorder, 15);
    }

    public Waypoint(Location location, Text recorder, int colorId) {
        this.location = location;
        this.recorder = recorder;
        this.colorId = colorId;
    }

    /**
     * If any property of object is empty then it is judged as invalid.
     */
    public boolean isInvalid() {
        return this == EMPTY || location == null || recorder == null || !location.isValid();
    }
}
