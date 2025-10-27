package com.slayerxp.overlay.utils

/*
 * Code from Devonian (https://github.com/Synnerz/devonian/blob/main/src/main/kotlin/com/github/synnerz/devonian/utils/Scheduler.kt) 
 * Under GPL 3.0 License
 */

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity

object Scheduler {
    private val tasks = mutableListOf<Task>()
    data class Task(var delay: Int, val cb: () -> Unit)

    init {
        ClientTickEvents.START_CLIENT_TICK.register {
            synchronized(tasks) {
                tasks.removeAll {
                    if (it.delay-- > 0) return@removeAll false

                    MinecraftClient.getInstance().submit(it.cb)

                    return@removeAll true
                }
            }
        }
    }

    @JvmOverloads
    fun scheduleTask(delay: Int = 1, cb: () -> Unit) {
        synchronized(tasks) {
            tasks.add(Task(delay, cb))
        }
    }

    @JvmOverloads
    fun scheduleStandName(entity: Entity, cb: () -> Unit, depth: Int = 0) {
        if (depth > 10) return
        scheduleTask(2) {
            if (entity.name.string !== "Armor Stand") {
                cb()
                return@scheduleTask
            }

            scheduleStandName(entity, cb, depth + 1)
        }
    }
}