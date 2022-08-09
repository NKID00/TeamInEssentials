package name.nkid00.teaminess.model;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Waypoint {
    public Location location;
    public Text recorder;
    public int colorId; // index-color map was in Teaminess.XAERO_COLORMAP, -1 means plain
    public final Long timestamp;
    //public Long latestEditTimestamp;

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
        this.timestamp = System.currentTimeMillis();
        //this.latestEditTimestamp = timestamp;
    }

    public boolean isEmpty() {
        return location == null || location.isEmpty();
    }

    public boolean hasLocation() {
        return !(location == null || location.position() == null);
    }

    public boolean hasColor() {
        return colorId >= 0;
    }

    /*public boolean equalsLocation(Waypoint w) {
        if (location == null) return w.location == null;
        return location.equals(w.location);
    }*/

    /*
     * // Update latestEditTimestamp when the Waypoint was edited.
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
    // the colorId reflect map is Teaminess.XAERO_COLORMAP
    public Text chatFormatString(String name) {
        if (isEmpty())
            return Text.literal(name);

        Style style = Style.EMPTY;

        if (hasColor())
            style = style.withColor(Formatting.byColorIndex(colorId));

        if (hasLocation()) {
            String[] hoverStrComposition = {
                    String.valueOf(location.position().getX()),
                    String.valueOf(location.position().getY()),
                    String.valueOf(location.position().getZ()),
                    location.dimension().toString()
            };
            Text hoverText = Text.literal(String.join(", ", hoverStrComposition));
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        }

        return Text.literal(name).setStyle(style);
    }
}