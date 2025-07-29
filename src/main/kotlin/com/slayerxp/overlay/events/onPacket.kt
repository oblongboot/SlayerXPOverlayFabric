package com.slayerxp.overlay.events

import net.minecraft.network.packet.Packet

abstract class onPacket(val packet: Packet<*>) {
    class Incoming(packet: Packet<*>) : onPacket(packet)
}
