package name.nkid00.teaminess;

import name.nkid00.teaminess.model.TpRequest;
import name.nkid00.teaminess.model.Waypoint;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.util.Pair;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import name.nkid00.teaminess.command.*;
import name.nkid00.teaminess.message.ChatMessage;

public class Teaminess implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Teaminess");
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    public static final ScheduledExecutorService TELEPORT_TIMER = Executors.newScheduledThreadPool(7);

    public static final boolean POTENTIAL_COMMAND_CONFLICT = FabricLoader.getInstance().isModLoaded("worldedit");
    public static final boolean COMPATIBLE_MAP_MODS = FabricLoader.getInstance().isModLoaded("xaerominimap");

    public static Options options;
    public static Data data;
    public static ConcurrentHashMap<UUID, TpRequest> TpRequests = new ConcurrentHashMap<>(64);
    public static ConcurrentHashMap<String, Waypoint> WaypointMap = new ConcurrentHashMap<>(16);
    public static volatile Pair<String, Waypoint> latestWaypointPair = new Pair<>("", Waypoint.EMPTY);

    @Override
    public void onInitialize() {
        var loader = FabricLoader.getInstance();

        // Options (static and shared globally)
        Options.file = loader.getConfigDir().resolve("teaminess.json").toFile();
        Options.load();
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> Options.save());

        // Data (dynamic and stored respectively for each world)
        /*Data.file = loader.getConfigDir().resolve("teaminess/data.json").toFile();
        Data.load();
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> Data.save());*/

        // Commands
        if (POTENTIAL_COMMAND_CONFLICT) {
            Teaminess.LOGGER.warn("Commands are disabled due to potential conflict with WorldEdit");
        } else {
            CommandRegistrationCallback.EVENT.register(HelpCommand::register);
            CommandRegistrationCallback.EVENT.register(ReloadOptionsCommand::register);
            CommandRegistrationCallback.EVENT.register(TpCommand::register);
            CommandRegistrationCallback.EVENT.register(TpacceptCommand::register);
            CommandRegistrationCallback.EVENT.register(TprejectCommand::register);
            CommandRegistrationCallback.EVENT.register(WaypointCommand::register);
        }

        // Banners
        if (POTENTIAL_COMMAND_CONFLICT) {
            ServerPlayConnectionEvents.JOIN.register(Banner::registerPotentialCommandConflict);
        } else {
            ServerPlayConnectionEvents.JOIN.register(Banner::register);
        }

        // ChatMessage
        ServerMessageEvents.CHAT_MESSAGE.register(ChatMessage::onChatMessage);
    }
}
