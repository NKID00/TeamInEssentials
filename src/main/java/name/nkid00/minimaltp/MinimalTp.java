package name.nkid00.minimaltp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.crash.CrashException;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import name.nkid00.minimaltp.command.HelpCommand;
import name.nkid00.minimaltp.command.ReloadCommand;
import name.nkid00.minimaltp.command.TpCommand;
import name.nkid00.minimaltp.command.TpaCommand;
import name.nkid00.minimaltp.command.TprCommand;

public class MinimalTp implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("MinimalTp");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Timer TELEPORT_TIMER = new Timer(true);

    public static final Style MSG_STYLE = Style.EMPTY.withColor(Formatting.YELLOW);
    public static final Style ACCEPT_STYLE = Style.EMPTY.withColor(Formatting.GREEN);
    public static final Style REFUSE_STYLE = Style.EMPTY.withColor(Formatting.RED);
    public static final Style CLICK_HELP_CMD_STYLE = Style.EMPTY
            .withUnderline(true)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "//tpa"));
    public static final Style CLICK_TPA_CMD_STYLE = Style.EMPTY
            .withColor(Formatting.DARK_GREEN)
            .withUnderline(true)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "//tpa"));
    public static final Style CLICK_TPR_CMD_STYLE = Style.EMPTY
            .withColor(Formatting.DARK_RED)
            .withUnderline(true)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "//tpr"));

    public static Options options;
    public static Database database;
    public static HashMap<UUID, TpRequest> TpRequests = new HashMap<>();

    @Override
    public void onInitialize() throws CrashException {
        // options and database
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            var base = new File(server.getRunDirectory(), "minimaltp");
            if (!base.exists()) {
                base.mkdir();
            }

            Options.file = new File(base, "options.json");
            Options.reload();

            Database.file = new File(base, "data.db");
            Database.connect();
        });
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
            Database.close();
        });

        // commands
        CommandRegistrationCallback.EVENT.register(HelpCommand::register);
        CommandRegistrationCallback.EVENT.register(ReloadCommand::register);
        CommandRegistrationCallback.EVENT.register(TpCommand::register);
        CommandRegistrationCallback.EVENT.register(TpaCommand::register);
        CommandRegistrationCallback.EVENT.register(TprCommand::register);

        // banner
        ServerPlayConnectionEvents.JOIN.register(Banner::register);
    }
}
