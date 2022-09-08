package name.nkid00.teaminess;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Styles {

    public static final Style NORMAL_MSG = Style.EMPTY.withColor(Formatting.YELLOW);
    public static final Style ACCEPTED = Style.EMPTY.withColor(Formatting.GREEN);
    public static final Style REJECTED = Style.EMPTY.withColor(Formatting.RED);
    public static final Style CLICK_TPACCPET_CMD = Style.EMPTY
            .withColor(Formatting.DARK_GREEN)
            .withUnderline(true)
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("点击执行")))
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "//tpaccept"));
    public static final Style CLICK_TPREJECT_CMD = Style.EMPTY
            .withColor(Formatting.DARK_RED)
            .withUnderline(true)
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("点击执行")))
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "//tpreject"));
}
