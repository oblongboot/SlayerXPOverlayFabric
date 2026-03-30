package dev.oblongboot.sxp.utils.skia.gl

import org.lwjgl.opengl.GL30.*
import java.util.*

object States {

  private val glVersion: Int
  private val states = Stack<State>()

  fun push() {
    states += State(glVersion).push()
  }

  fun pop() {
    require(states.isNotEmpty()) { "No state to restore." }
    states.pop().pop()
  }

  init {
    val major = IntArray(1)
    val minor = IntArray(1)
    glGetIntegerv(GL_MAJOR_VERSION, major)
    glGetIntegerv(GL_MINOR_VERSION, minor)
    glVersion = major[0] * 100 + minor[0] * 10
  }

}
