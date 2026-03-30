package dev.oblongboot.sxp.features

import dev.oblongboot.sxp.events.WorldRenderEvent
import meteordevelopment.orbit.EventHandler
import dev.oblongboot.sxp.utils.ChatUtils
import dev.oblongboot.sxp.events.impl.SkiaDrawEvent
import dev.oblongboot.sxp.utils.skia.SkijaRenderer
import net.minecraft.client.Minecraft
import io.github.humbleui.skija.Font
import io.github.humbleui.skija.Typeface

class BossHighlightFeat {
    @EventHandler
    fun onWorldRenderLast(event: SkiaDrawEvent) {
    }
}