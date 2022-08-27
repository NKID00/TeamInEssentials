package name.nkid00.teaminess.model;

import net.minecraft.text.Text;

public class Waypoint {
    public Location location;
    public Text recorder;
    public int colorId; // index-color map was in Teaminess.XAERO_COLORMAP, -1 means plain
    //public final Long timestamp;
    //public Long latestEditTimestamp;

    public static Waypoint EMPTY = new Waypoint(null, Text.literal("Teaminess Mod"));

    public Waypoint(Location location, Text recorder) {
        this(location, recorder, 0);
    }

    public Waypoint(Location location, Text recorder, int colorId) {
        this.location = location;
        this.recorder = recorder;
        this.colorId = colorId;
        //this.timestamp = System.currentTimeMillis();
        //this.latestEditTimestamp = timestamp;
    }

    public boolean isInvalid() {
        return this != EMPTY || location == null || recorder == null || !location.isValid();
    }

    /*
     * public boolean equalsLocation(Waypoint w) {
     *  if (location == null) return w.location == null;
     *  return location.equals(w.location);
     * }
     */

    /*
     * // Update latestEditTimestamp when the Waypoint was edited.
     * public Waypoint changeLocation(Location newLocation) {
     * this.location = newLocation;
     * this.latestEditTimestamp = Waypoint.getTimeStamp();
     * return this;
     * }
     */
}