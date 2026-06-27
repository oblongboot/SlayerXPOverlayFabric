package dev.oblongboot.sxp.utils.skia

import dev.oblongboot.sxp.events.impl.SkiaDrawEvent
import meteordevelopment.orbit.EventHandler

object WhatTheFuck {
    @EventHandler
    fun onSkiaDraw(event: SkiaDrawEvent) {
        SkijaRenderer.bindEvent(event)
        try {
            SkijaRenderer.runDrawables()
            SkijaRenderer.runTopDrawables()
        } finally {
            SkijaRenderer.unbindEvent()
        }
    }
}
