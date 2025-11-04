package dev.oblongboot.sxp.events

import net.minecraft.network.packet.Packet

abstract class OnPacket(val packet: Packet<*>) {
    class Incoming(packet: Packet<*>) : OnPacket(packet)
}
