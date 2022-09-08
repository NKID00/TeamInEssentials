package name.nkid00.teaminess;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.networking.v1.PacketSender;

public class Banner {
    public static void register(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        handler.player.sendMessage(
                Text.literal("输入//help来使用硬核自研大数据人工智能黑科技模组").setStyle(Styles.MSG_STYLE));
    }

    public static void registerPotentialCommandConflict(ServerPlayNetworkHandler handler, PacketSender sender,
            MinecraftServer server) {
        handler.player.sendMessage(
                Text.literal("正在使用硬核自研大数据人工智能黑科技模组").setStyle(Styles.MSG_STYLE));
    }
}
