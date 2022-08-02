package name.nkid00.teaminess.model;

import name.nkid00.teaminess.Teaminess;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class Waypoint {
    public int colorId; // index-color map was in Teaminess.XAERO_COLORMAP, -1 means plain
    public Location location;
    public Text recorder;
    public final Long timestamp;
    public Long latestEditTimestamp;

    private static Long getTimeStamp() {
        return System.currentTimeMillis();
    }

    public Waypoint() {
        this(null, Text.literal("Teaminess Mod"));
    }

    public Waypoint(Location location, Text recorder) {
        this(location, recorder, -1);
    }

    public Waypoint(Location location, Text recorder, int colorId) {
        this.location = location;
        this.recorder = recorder;
        this.colorId = colorId;
        this.timestamp = Waypoint.getTimeStamp();
        this.latestEditTimestamp = this.timestamp;
    }

    public boolean isEmpty() {
        return this.equals(new Waypoint());
    }

    public boolean hasLocation() {
        return !(this.location == null || this.location.position() == null);
    }

    public boolean hasColor() {
        return this.colorId >= 0;
    }

    public boolean equalsLocation(Waypoint w) {
        if (this.location == null && w.location == null)
            return true;
        if (this.location == null)
            return false;
        return this.location.equals(w.location);
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

    // Format the name with color whose index was stored in the Waypoint.colorId
    // the colorid reflect map is Teaminess.XAERO_COLORMAP
    public Text chatFormatString(String name) {
        if (this.isEmpty())
            return Text.literal(name);

        Style style = Style.EMPTY;

        if (this.hasColor())
            style = style.withColor(Teaminess.XAERO_COLORMAP[this.colorId]);

        if (this.hasLocation()) {
            String[] hoverStrComposition = {
                    String.valueOf(this.location.position().getX()),
                    String.valueOf(this.location.position().getY()),
                    String.valueOf(this.location.position().getZ()),
                    this.location.dimension().toString()
            };
            Text hoverText = Text.literal(String.join(", ", hoverStrComposition));
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        }

        return Text.literal(name).setStyle(style);
    }
}