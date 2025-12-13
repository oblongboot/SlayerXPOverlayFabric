package dev.oblongboot.sxp.utils.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import net.minecraft.client.gl.RenderPipelines

// stolen from [progreso](https://github.com/ya-ilya/progreso/blob/master/progreso-client/src/main/kotlin/org/progreso/client/util/render/Render3D.kt)
// all credit to ya-ilya
object Pipelines {

    val LINES_ESP = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
            .withLocation("slayerxpoverlayfabric/lines_esp")
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).build()
    )

    val QUADS: RenderPipeline = RenderPipelines
        .register(
            RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                .withLocation("slayerxpoverlayfabric/quads")
                .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                .build()
        )

    val QUADS_ESP: RenderPipeline = RenderPipelines
        .register(
            RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                .withLocation("slayerxpoverlayfabric/quads_esp")
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).build()
        )

//    val BEACON_BEAM_OPAQUE = RenderPipeline.builder(RenderPipelines.RENDERTYPE_BEACON_BEAM_SNIPPET)
//        .withLocation("slayerxpoverlayfabric/beacon_beam_opaque")
//        .build()
//
//    val BEACON_BEAM_OPAQUE_ESP = RenderPipeline.builder(RenderPipelines.RENDERTYPE_BEACON_BEAM_SNIPPET)
//        .withLocation("slayerxpoverlayfabric/beacon_beam_opaque_esp")
//        .withDepthWrite(false)
//        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
//        .build()
//
//    val BEACON_BEAM_TRANSLUCENT = RenderPipeline.builder(RenderPipelines.RENDERTYPE_BEACON_BEAM_SNIPPET)
//        .withLocation("slayerxpoverlayfabric/beacon_beam_translucent")
//        .withDepthWrite(false)
//        .withBlend(BlendFunction.TRANSLUCENT)
//        .build()
//
//    val BEACON_BEAM_TRANSLUCENT_ESP = RenderPipeline.builder(RenderPipelines.RENDERTYPE_BEACON_BEAM_SNIPPET)
//        .withLocation("slayerxpoverlayfabric/beacon_beam_translucent_esp")
//        .withDepthWrite(false)
//        .withBlend(BlendFunction.TRANSLUCENT)
//        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
//        .build()

}