package name.nkid00.minimaltp;

import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

public class Banner {
    public static void register(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        handler.player.sendMessage(
                Text.empty()
                        .setStyle(MinimalTp.MSG_STYLE)
                        .append("输入//help来使用硬核自研大数据人工智能黑科技模组"),
                MessageType.SYSTEM);
    }

    public static void registerPotentialCommandConflict(ServerPlayNetworkHandler handler, PacketSender sender,
            MinecraftServer server) {
        handler.player.sendMessage(
                Text.empty()
                        .setStyle(MinimalTp.MSG_STYLE)
                        .append("正在使用硬核自研大数据人工智能黑科技模组"),
                MessageType.SYSTEM);
    }
}
