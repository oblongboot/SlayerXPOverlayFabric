package dev.oblongboot.sxp.mixin;

import dev.oblongboot.sxp.events.EventManager;
import dev.oblongboot.sxp.events.OnPacket;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {

    @Inject(method = "genericsFtw", at = @At("HEAD"))
    private static void onPacketReceived(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (listener instanceof net.minecraft.client.multiplayer.ClientPacketListener) {
            
            //System.out.println("Packet intercepted: " + packet.getClass().getSimpleName());
            EventManager.INSTANCE.post(new OnPacket.Incoming(packet));
        } // bleh
    }
}