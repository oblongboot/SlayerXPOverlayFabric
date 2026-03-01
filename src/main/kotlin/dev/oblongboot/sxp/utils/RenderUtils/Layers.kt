package dev.oblongboot.sxp.utils.RenderUtils

import dev.oblongboot.sxp.utils.RenderUtils.Pipelines as RendererPipelines
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.blockentity.BeaconRenderer

// also taken from old version of [Devonian](https://github.com/Synnerz/devonian/blob/main/src/main/kotlin/com/github/synnerz/devonian/utils/render/DPipelines.kt)
// all credit to DocilElm
object Layers {
    val LINES = RenderType.create(
        "slayerxpoverlay/lines",
        1536,
        false,
        true,
        RendererPipelines.LINES,
        RenderType.CompositeState
            .builder()
            .createCompositeState(false)
    )
    
    val LINES_ESP = RenderType.create(
        "slayerxpoverlay/lines_esp",
        1536,
        false,
        true,
        RendererPipelines.LINES_ESP,
        RenderType.CompositeState
            .builder()
            .createCompositeState(false)
    )
    
    val TRIANGLE_STRIP = RenderType.create(
        "slayerxpoverlay/triangle_strip",
        1536,
        false,
        true,
        RendererPipelines.TRIANGLE_STRIP,
        RenderType.CompositeState
            .builder()
            .createCompositeState(false)
    )
    
    val TRIANGLE_STRIP_ESP = RenderType.create(
        "slayerxpoverlay/triangle_strip_esp",
        1536,
        false,
        true,
        RendererPipelines.TRIANGLE_STRIP_ESP,
        RenderType.CompositeState
            .builder()
            .createCompositeState(false)
    )
    
    val BEACON_BEAM_OPAQUE = RenderType.create(
        "slayerxpoverlay/beacon_beam_opaque",
        1536,
        false,
        true,
        RendererPipelines.BEACON_BEAM_OPAQUE,
        RenderType.CompositeState
            .builder()
            .setTextureState(RenderStateShard.TextureStateShard(BeaconRenderer.BEAM_LOCATION, false))
            .createCompositeState(false)
    )
    
    val BEACON_BEAM_OPAQUE_ESP = RenderType.create(
        "slayerxpoverlay/beacon_beam_opaque_esp",
        1536,
        false,
        true,
        RendererPipelines.BEACON_BEAM_OPAQUE_ESP,
        RenderType.CompositeState
            .builder()
            .setTextureState(RenderStateShard.TextureStateShard(BeaconRenderer.BEAM_LOCATION, false))
            .createCompositeState(false)
    )
    
    val BEACON_BEAM_TRANSLUCENT = RenderType.create(
        "slayerxpoverlay/beacon_beam_translucent",
        1536,
        false,
        true,
        RendererPipelines.BEACON_BEAM_TRANSLUCENT,
        RenderType.CompositeState
            .builder()
            .setTextureState(RenderStateShard.TextureStateShard(BeaconRenderer.BEAM_LOCATION, false))
            .createCompositeState(false)
    )
    
    val BEACON_BEAM_TRANSLUCENT_ESP = RenderType.create(
        "slayerxpoverlay/beacon_beam_translucent_esp",
        1536,
        false,
        true,
        RendererPipelines.BEACON_BEAM_TRANSLUCENT_ESP,
        RenderType.CompositeState
            .builder()
            .setTextureState(RenderStateShard.TextureStateShard(BeaconRenderer.BEAM_LOCATION, false))
            .createCompositeState(false)
    )
}