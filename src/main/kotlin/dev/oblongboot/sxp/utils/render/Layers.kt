package dev.oblongboot.sxp.utils.render

import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.render.*

// stolen from [progreso](https://github.com/ya-ilya/progreso/blob/master/progreso-client/src/main/kotlin/org/progreso/client/util/render/Render3D.kt)
// all credit to ya-ilya
object Layers {

    val LINES = RenderLayer.of(
        "slayerxpoverlay/lines",
        RenderSetup
            .builder(RenderPipelines.LINES)
            .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .outputTarget(OutputTarget.ITEM_ENTITY_TARGET)
            .build()
    )
    
    val LINES_ESP = RenderLayer.of(
        "slayerxpoverlay/lines_esp",
        RenderSetup
            .builder(Pipelines.LINES_ESP)
            .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .outputTarget(OutputTarget.ITEM_ENTITY_TARGET)
            .build()
    )

    val QUADS: RenderLayer = RenderLayer.of(
        "slayerxpoverlay/quads",
        RenderSetup
            .builder(Pipelines.QUADS)
            .build()
    )

    val QUADS_ESP: RenderLayer = RenderLayer.of(
        "slayerxpoverlay/quads_esp",
        RenderSetup
            .builder(Pipelines.QUADS_ESP)
            .build()
    )

//    val BEACON_BEAM_OPAQUE = RenderLayer.of(
//        "slayerxpoverlay/beacon_beam_opaque",
//        1536,
//        false,
//        true,
//        RendererPipelines.BEACON_BEAM_OPAQUE,
//        RenderLayer.MultiPhaseParameters
//            .builder()
//            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
//            .build(false)
//    )
//
//    val BEACON_BEAM_OPAQUE_ESP = RenderLayer.of(
//        "slayerxpoverlay/beacon_beam_opaque_esp",
//        1536,
//        false,
//        true,
//        RendererPipelines.BEACON_BEAM_OPAQUE_ESP,
//        RenderLayer.MultiPhaseParameters
//            .builder()
//            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
//            .build(false)
//    )
//
//    val BEACON_BEAM_TRANSLUCENT = RenderLayer.of(
//        "slayerxpoverlay/beacon_beam_translucent",
//        1536,
//        false,
//        true,
//        RendererPipelines.BEACON_BEAM_TRANSLUCENT,
//        RenderLayer.MultiPhaseParameters
//            .builder()
//            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
//            .build(false)
//    )
//
//    val BEACON_BEAM_TRANSLUCENT_ESP = RenderLayer.of(
//        "slayerxpoverlay/beacon_beam_translucent_esp",
//        1536,
//        false,
//        true,
//        RendererPipelines.BEACON_BEAM_TRANSLUCENT_ESP,
//        RenderLayer.MultiPhaseParameters
//            .builder()
//            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
//            .build(false)
//    )

}