package com.slayerxp.overlay.features

import com.slayerxp.overlay.events.WorldRenderEvent
import com.slayerxp.overlay.util.Render3D
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.awt.Color
import net.minecraft.util.math.Box
import meteordevelopment.orbit.EventHandler

class BossHighlightFeat {
    @EventHandler
    fun onWorldRenderLast(event: WorldRenderEvent.Last) {
        val ctx = event.context

        // Render3D.renderFilledBox( // just to test rendering works
        //     ctx,
        //     x = 0.0, y = 70.0, z = 0.0,
        //     width = 1.0, height = 2.0, depth = 1.0,
        //     color = Color(255, 0, 0, 100),
        //     phase = true
        // ) R.I.P the ominious debug box that sat in the void or something idk
    }
}