package me.kambing.gardenia.injection;

import io.netty.channel.ChannelHandlerContext;
import me.kambing.gardenia.Gardenia;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    @Inject(method = "channelRead0", at = @At("HEAD"))
    private void read(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callback) {
        if (packet == null) return;
        try {
            if (packet.getClass().getSimpleName().startsWith("S")) {
                Gardenia.instance.moduleManager.receivePacketToAllModules(packet);
            }
        } catch (Exception ignored) {
        }
    }
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void send(Packet<?> packet, CallbackInfo callback) {
        if (packet == null) return;
        try {
            if (packet.getClass().getSimpleName().startsWith("C")) {
                Gardenia.instance.moduleManager.receivePacketToAllModules(packet);
            }
        } catch (Exception ignored) {
        }
    }
}
