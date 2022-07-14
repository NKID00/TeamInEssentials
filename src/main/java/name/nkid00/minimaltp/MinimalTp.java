package name.nkid00.minimaltp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import name.nkid00.minimaltp.command.Reload;
import name.nkid00.minimaltp.command.Tp;
import name.nkid00.minimaltp.command.Tpa;
import name.nkid00.minimaltp.command.Tpr;

public class MinimalTp implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("minimaltp");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Timer TELEPORT_TIMER = new Timer(true);

    public static final Style MSG_STYLE = Style.EMPTY.withColor(Formatting.YELLOW);
    public static final Style ACCEPT_STYLE = Style.EMPTY.withColor(Formatting.GREEN);
    public static final Style REFUSE_STYLE = Style.EMPTY.withColor(Formatting.RED);
    public static final Style ACCEPT_CMD_STYLE = Style.EMPTY
            .withColor(Formatting.DARK_GREEN)
            .withUnderline(true)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "//tpa"));
    public static final Style REFUSE_CMD_STYLE = Style.EMPTY
            .withColor(Formatting.DARK_RED)
            .withUnderline(true)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "//tpr"));

    public static File configFile;
    public static Settings settings;
    public static HashMap<UUID, TpRequest> TpRequests = new HashMap<>();

    @Override
    public void onInitialize() throws CrashException {
        // commands
        CommandRegistrationCallback.EVENT.register(Tp::register);
        CommandRegistrationCallback.EVENT.register(Tpa::register);
        CommandRegistrationCallback.EVENT.register(Tpr::register);
        CommandRegistrationCallback.EVENT.register(Reload::register);

        // config
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            configFile = new File(server.getRunDirectory(), "minimaltp.json");
            reloadConfig();
        });

        // banner
        ServerPlayConnectionEvents.JOIN
                .register((handler, sender, server) -> handler.player.getCommandSource().sendFeedback(
                        Text.literal("输入//tp来使用硬核自研大数据人工智能黑科技模组")
                                .setStyle(MinimalTp.MSG_STYLE),
                        false));
    }

    public static void reloadConfig() {
        try (var reader = new FileReader(configFile)) {
            settings = GSON.fromJson(reader, Settings.class);
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            settings = new Settings();
            try (var writer = new FileWriter(configFile)) {
                GSON.toJson(settings, writer);
            } catch (IOException | JsonIOException e2) {
                throw new CrashException(new CrashReport("配置文件生成失败", e2));
            }
        }
    }
}
