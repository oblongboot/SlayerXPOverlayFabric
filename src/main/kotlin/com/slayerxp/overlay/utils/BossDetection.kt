package com.slayerxp.overlay.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.util.math.Box
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import com.slayerxp.overlay.settings.Config

import com.slayerxp.overlay.utils.ChatUtils.modMessage

fun getArmorStands(radius: Double = 35.0, yRange: Double = 10.0): MutableList<ArmorStandEntity> {
    val client = MinecraftClient.getInstance();

    // Random checks to make sure a world and player exist
    val world = client.world ?: return mutableListOf();
    val player = client.player ?: return mutableListOf();

    val box = Box(
        player.x - radius, player.y - yRange, player.z - radius,
        player.x + radius, player.y + yRange, player.z + radius
    );

    return world.getEntitiesByClass(ArmorStandEntity::class.java, box) {
        it.squaredDistanceTo(player) <= radius * radius
    }
}

// Fuck Primal Fears. idc that they still break this. Only broken half a month out of the year
fun bossChecker(sw: StopwatchUtil, lastUUID: Array<String>) {
    val client = MinecraftClient.getInstance();
    val pName = client.player?.name?.string;
    var running = true;

    ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { c ->
        if (!running) return@EndTick;
        val armorStands = getArmorStands();

        for (stand in armorStands) {
            if (stand.name.string == String.format("Spawned by: %s", pName) && lastUUID[0] != stand.uuid.toString()) {
                sw.start();
                running = false;
                // Why can't I pass a string by reference in kotlin
                // Fuck it, it's an array now
                lastUUID[0] = stand.uuid.toString();

                val cSlayer = Scoreboard.getSlayerType()
                // Add config checks after they are made
                if (cSlayer == "Blaze" && Config.isToggled("BurningVengeanceDamage")) {
                    burningDamage();
                }
                break;
            }
        }
    });
}

fun burningDamage() {
    var running = true;

    ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { c ->
        if (!running) return@EndTick;
        val armorStands = getArmorStands(5.0);

        for (stand in armorStands) {
            if (stand.name.string.contains("ﬗ")) {
                running = false;
                modMessage(String.format("Burning Vengeance Damage: %s", stand.name.string));
                break;
            }
        }
    });
}

