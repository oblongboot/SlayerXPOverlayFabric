package dev.oblongboot.sxp.utils.skia.helper

import io.github.humbleui.skija.*
import java.io.IOException
//CREDIT TO @altEpsilonPhoenix on discord (hes goated)
object FontHelper {

  const val DEFAULT_SIZE = 16f
  val ROOT = "assets/slayerxpoverlay/font/"

  private val fonts by lazy { mutableMapOf<String, Font>() }
  private val typefaces by lazy { mutableMapOf<String, Typeface>() }

  fun get(path: String, size: Float = DEFAULT_SIZE) = fonts.computeIfAbsent("$path:$size") {
    Font(
      loadTypeface(path), size
    ).apply {
      isSubpixel = false
      hinting = FontHinting.NORMAL
      edging = FontEdging.ANTI_ALIAS
    }
  }

  private fun loadTypeface(path: String) = typefaces.computeIfAbsent(path) {
    val resourcePath = "$ROOT$path"

    val bytes = javaClass.classLoader
      ?.getResourceAsStream(resourcePath)
      ?.use { it.readAllBytes() }
      ?: throw IOException("Font resource not found: $resourcePath")

    val font = FontMgr.getDefault().makeFromData(Data.makeFromBytes(bytes))
      ?: throw IllegalArgumentException("Invalid font data: $resourcePath")

    font
  }

}
