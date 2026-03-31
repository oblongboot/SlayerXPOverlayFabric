//package dev.oblongboot.sxp.utils.render
//
//import net.minecraft.client.renderer.RenderPipelines
//import net.minecraft.client.renderer.rendertype.LayeringTransform
//import net.minecraft.client.renderer.rendertype.OutputTarget
//import net.minecraft.client.renderer.rendertype.RenderSetup
//import net.minecraft.client.renderer.rendertype.RenderType
//
//// stolen from [progreso](https://github.com/ya-ilya/progreso/blob/master/progreso-client/src/main/kotlin/org/progreso/client/util/render/Render3D.kt)
//// all credit to ya-ilya
//object Layers {
//
//    val LINES = RenderType.create(
//        "slayerxpoverlay/lines",
//        RenderSetup
//            .builder(RenderPipelines.LINES)
//            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
//            .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
//            .createRenderSetup()
//    )
//
//    val LINES_ESP = RenderType.create(
//        "slayerxpoverlay/lines_esp",
//        RenderSetup
//            .builder(Pipelines.LINES_ESP)
//            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
//            .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
//            .createRenderSetup()
//    )
//
//    val QUADS: RenderType = RenderType.create(
//        "slayerxpoverlay/quads",
//        RenderSetup
//            .builder(Pipelines.QUADS)
//            .createRenderSetup()
//    )
//
//    val QUADS_ESP: RenderType = RenderType.create(
//        "slayerxpoverlay/quads_esp",
//        RenderSetup
//            .builder(Pipelines.QUADS_ESP)
//            .createRenderSetup()
//    )
//
////    val BEACON_BEAM_OPAQUE = RenderLayer.of(
////        "slayerxpoverlay/beacon_beam_opaque",
////        1536,
////        false,
////        true,
////        RendererPipelines.BEACON_BEAM_OPAQUE,
////        RenderLayer.MultiPhaseParameters
////            .builder()
////            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
////            .build(false)
////    )
////
////    val BEACON_BEAM_OPAQUE_ESP = RenderLayer.of(
////        "slayerxpoverlay/beacon_beam_opaque_esp",
////        1536,
////        false,
////        true,
////        RendererPipelines.BEACON_BEAM_OPAQUE_ESP,
////        RenderLayer.MultiPhaseParameters
////            .builder()
////            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
////            .build(false)
////    )
////
////    val BEACON_BEAM_TRANSLUCENT = RenderLayer.of(
////        "slayerxpoverlay/beacon_beam_translucent",
////        1536,
////        false,
////        true,
////        RendererPipelines.BEACON_BEAM_TRANSLUCENT,
////        RenderLayer.MultiPhaseParameters
////            .builder()
////            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
////            .build(false)
////    )
////
////    val BEACON_BEAM_TRANSLUCENT_ESP = RenderLayer.of(
////        "slayerxpoverlay/beacon_beam_translucent_esp",
////        1536,
////        false,
////        true,
////        RendererPipelines.BEACON_BEAM_TRANSLUCENT_ESP,
////        RenderLayer.MultiPhaseParameters
////            .builder()
////            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
////            .build(false)
////    )
//
//}