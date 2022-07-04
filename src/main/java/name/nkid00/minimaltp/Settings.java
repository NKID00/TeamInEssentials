package name.nkid00.minimaltp;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public class Settings {
    public long teleport_interval = 3; // seconds
    public long request_expiration_interval = 120; // seconds
    public boolean immediate_teleportation_in_team = true;
    public boolean immediate_teleportation_between_team = false;

    static final Style MSG_STYLE = Style.EMPTY.withColor(Formatting.YELLOW);
    static final Style ACCEPT_STYLE = Style.EMPTY.withColor(Formatting.GREEN);
    static final Style REFUSE_STYLE = Style.EMPTY.withColor(Formatting.RED);
    static final Style ACCEPT_CMD_STYLE = Style.EMPTY
            .withColor(Formatting.DARK_GREEN)
            .withUnderline(true)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "//tpa"));
    static final Style REFUSE_CMD_STYLE = Style.EMPTY
            .withColor(Formatting.DARK_RED)
            .withUnderline(true)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "//tpr"));
}
