package dev.oblongboot.sxp.utils.RenderUtils

import dev.oblongboot.sxp.utils.RenderUtils.Pipelines as RendererPipelines
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer

// also taken from old version of [Devonian](https://github.com/Synnerz/devonian/blob/main/src/main/kotlin/com/github/synnerz/devonian/utils/render/DPipelines.kt)
// all credit to DocilElm
object Layers {
    val LINES = RenderLayer.of(
        "slayerxpoverlay/lines",
        1536,
        false,
        true,
        RendererPipelines.LINES,
        RenderLayer.MultiPhaseParameters
            .builder()
            .build(false)
    )
    
    val LINES_ESP = RenderLayer.of(
        "slayerxpoverlay/lines_esp",
        1536,
        false,
        true,
        RendererPipelines.LINES_ESP,
        RenderLayer.MultiPhaseParameters
            .builder()
            .build(false)
    )
    
    val TRIANGLE_STRIP = RenderLayer.of(
        "slayerxpoverlay/triangle_strip",
        1536,
        false,
        true,
        RendererPipelines.TRIANGLE_STRIP,
        RenderLayer.MultiPhaseParameters
            .builder()
            .build(false)
    )
    
    val TRIANGLE_STRIP_ESP = RenderLayer.of(
        "slayerxpoverlay/triangle_strip_esp",
        1536,
        false,
        true,
        RendererPipelines.TRIANGLE_STRIP_ESP,
        RenderLayer.MultiPhaseParameters
            .builder()
            .build(false)
    )
    
    val BEACON_BEAM_OPAQUE = RenderLayer.of(
        "slayerxpoverlay/beacon_beam_opaque",
        1536,
        false,
        true,
        RendererPipelines.BEACON_BEAM_OPAQUE,
        RenderLayer.MultiPhaseParameters
            .builder()
            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
            .build(false)
    )
    
    val BEACON_BEAM_OPAQUE_ESP = RenderLayer.of(
        "slayerxpoverlay/beacon_beam_opaque_esp",
        1536,
        false,
        true,
        RendererPipelines.BEACON_BEAM_OPAQUE_ESP,
        RenderLayer.MultiPhaseParameters
            .builder()
            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
            .build(false)
    )
    
    val BEACON_BEAM_TRANSLUCENT = RenderLayer.of(
        "slayerxpoverlay/beacon_beam_translucent",
        1536,
        false,
        true,
        RendererPipelines.BEACON_BEAM_TRANSLUCENT,
        RenderLayer.MultiPhaseParameters
            .builder()
            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
            .build(false)
    )
    
    val BEACON_BEAM_TRANSLUCENT_ESP = RenderLayer.of(
        "slayerxpoverlay/beacon_beam_translucent_esp",
        1536,
        false,
        true,
        RendererPipelines.BEACON_BEAM_TRANSLUCENT_ESP,
        RenderLayer.MultiPhaseParameters
            .builder()
            .texture(RenderPhase.Texture(BeaconBlockEntityRenderer.BEAM_TEXTURE, false))
            .build(false)
    )
}