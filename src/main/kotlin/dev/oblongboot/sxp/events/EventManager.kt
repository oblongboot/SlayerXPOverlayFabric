package dev.oblongboot.sxp.events

import meteordevelopment.orbit.EventBus

object EventManager {
    @JvmField
    val EVENT_BUS = EventBus()
    @JvmStatic
    fun post(event: Any) {
        EVENT_BUS.post(event)
    }
}
