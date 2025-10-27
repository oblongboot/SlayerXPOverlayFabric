package com.slayerxp.overlay.mixin;

import com.slayerxp.overlay.events.EventManager;
import com.slayerxp.overlay.events.OnPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "handlePacket", at = @At("HEAD"))
    private static void onPacketReceived(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (listener instanceof net.minecraft.client.network.ClientPlayNetworkHandler) {
            
            //System.out.println("Packet intercepted: " + packet.getClass().getSimpleName());
            EventManager.INSTANCE.getEVENT_BUS().post(new OnPacket.Incoming(packet));
        } // bleh
    }
}