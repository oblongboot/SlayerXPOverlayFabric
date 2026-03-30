package dev.oblongboot.sxp.utils.skia

import io.github.humbleui.skija.BackendRenderTarget
import io.github.humbleui.skija.impl.Stats
import org.jetbrains.annotations.Contract
//CREDIT TO @altEpsilonPhoenix on discord (hes goated)
class WrappedBackendRenderTarget(
  val width: Int,
  val height: Int,
  val sampleCnt: Int,
  val stencilBits: Int,
  val fbId: Int,
  val fbFormat: Int,
  ptr: Long
) : BackendRenderTarget(ptr) {

  companion object {

    @Contract("_, _, _, _, _, _ -> new")
    fun makeGL(
      width: Int, height: Int, sampleCnt: Int, stencilBits: Int, fbId: Int, fbFormat: Int
    ): WrappedBackendRenderTarget {
      Stats.onNativeCall()
      return WrappedBackendRenderTarget(
        width,
        height,
        sampleCnt,
        stencilBits,
        fbId,
        fbFormat,
        _nMakeGL(width, height, sampleCnt, stencilBits, fbId, fbFormat)
      )
    }

  }

}
