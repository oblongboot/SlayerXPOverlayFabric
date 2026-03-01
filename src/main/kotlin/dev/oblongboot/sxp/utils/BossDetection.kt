package dev.oblongboot.sxp.utils

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.AABB
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import dev.oblongboot.sxp.utils.Scoreboard.getScoreboardText
import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.utils.ChatUtils.modMessage
import dev.oblongboot.sxp.ui.BVOverlay

var isBossSpawned = false

fun getArmorStands(radius: Double = 35.0, yRange: Double = 10.0): MutableList<ArmorStand> {
    val client = Minecraft.getInstance();

    // Random checks to make sure a world and player exist
    val world = client.level ?: return mutableListOf();
    val player = client.player ?: return mutableListOf();

    val box = AABB(
        player.x - radius, player.y - yRange, player.z - radius,
        player.x + radius, player.y + yRange, player.z + radius
    );

    return world.getEntitiesOfClass(ArmorStand::class.java, box) {
        it.distanceToSqr(player) <= radius * radius
    }
}

fun bossChecker(sw: StopwatchUtil, lastUUID: Array<String>) {
    val client = Minecraft.getInstance();
    var running = true;
    ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { c ->
        if (!running) return@EndTick;
        if (getScoreboardText().toString().contains("Slay the boss!")) {
            sw.start()
            running = false
        }
    });
}


