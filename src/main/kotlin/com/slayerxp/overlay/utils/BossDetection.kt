package com.slayerxp.overlay.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.util.math.Box
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import kotlin.math.abs
import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.slayerxp.overlay.ui.BVOverlay

var isBossSpawned = false

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
    var bossUUID = null
    ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { c ->
        if (!running) return@EndTick;
        val armorStands = getArmorStands();

        for (stand in armorStands) {
            if (stand.name.string == String.format("Spawned by: %s", pName) && lastUUID[0] != stand.uuid.toString()) {
                sw.start();
                isBossSpawned = true
                running = false;
                if (isBossSpawned && bossUUID != null) {
                    val bossStillExists = armorStands.any { it.uuid.toString() == bossUUID }
                    if (!bossStillExists) {
                        isBossSpawned = false
                        bossUUID = null
                    }
                }
                // Why can't I pass a string by reference in kotlin
                // Fuck it, it's an array now
                lastUUID[0] = stand.uuid.toString();

                val cSlayer = Scoreboard.getSlayerType()
                // Add config checks after they are made
                if (cSlayer == "Blaze" && Config.isToggled("BurningVengeanceDamage")) {
                    burningDamage();
                }
                if (cSlayer == "Blaze" && Config.isToggled("BurningVengeanceTimer")) {
                    burningTimer(pName);
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

fun burningTimer(pName: String?) {
    var running = true;

    ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { c ->
        if (!running) return@EndTick;
        var entity1: ArmorStandEntity? = null;
        var entity2: ArmorStandEntity? = null;
        val armorStands = getArmorStands(5.0);

        for (stand in armorStands) {
            if (entity1 == null && stand.name.string.contains("Spawned by: $pName")) {
                entity1 = stand;
            }
            else if (entity2 == null && stand.name.string.contains("ASHEN ♨7")) {
                entity2 = stand;
            }
        }

        if (entity1 != null && entity2 != null && abs(entity1.x - entity2.x) <= 1 && abs(entity1.z - entity2.z) <= 1) {
            running = false;
            countdown();
        }
    });
}

fun countdown() {
    var running = true;
    val timer = StopwatchUtil();
    timer.start();

    ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { c ->
        if (!running) return@EndTick;

        var num = 6000 - timer.getElapsedTime();
        if (num > 0) {
            BVOverlay.label = String.format("§3%.2fs", num / 1000.0);
        }
        else {
            running = false;
            BVOverlay.label = "";
            timer.stopAndReset();
        }
    });
}

