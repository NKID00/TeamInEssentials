package name.nkid00.minimaltp.model;

import java.util.Random;

import net.minecraft.text.Text;

public class Waypoint {
    public int colorId; // index-color map was in MinimalTp.XAERO_COLORMAP
    public Location location;
    public Text recorder;
    public final Long timestamp;
    public Long latestEditTimestamp;

    private static Long getTimeStamp() {
        return System.currentTimeMillis();
    }

    public Waypoint() {
        this(null, Text.literal("MinimalTp Mod"));
    }

    public Waypoint(Location location, Text recorder) {
        this(location, recorder, new Random().nextInt(15));
    }

    public Waypoint(Location location, Text recorder, int colorId) {
        this.location = location;
        this.recorder = recorder;
        this.colorId = colorId;
        this.timestamp = Waypoint.getTimeStamp();
        this.latestEditTimestamp = this.timestamp;
    }

    public boolean isEmpty() {
        return this.location == null || this.location.position() == null;
    }

    public boolean equals(Waypoint w) {
        if (this.location == null && w.location == null)
            return true;
        if (this.location == null)
            return false;
        return this.location.equals(w.location);
    }

    public boolean equalsStrict(Waypoint w) {
        return this.equals(w) && this.recorder == w.recorder;
    }

    /*
     * // Remenber to update latestEditTimestamp when the Waypoint was once edited.
     * public Waypoint changeLocation(Location newLocation) {
     * this.location = newLocation;
     * this.latestEditTimestamp = Waypoint.getTimeStamp();
     * return this;
     * }
     */

    public static boolean isNameLegal(String name) {
        return name.trim().length() > 0;
    }
}