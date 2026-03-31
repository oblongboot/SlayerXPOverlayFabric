package dev.oblongboot.sxp.ui

import net.minecraft.client.gui.GuiGraphicsExtractor

interface Overlay {
    var x: Int
    var y: Int
    val width: Int
    val height: Int
    var shouldShow: Boolean
    
    fun draw()//ctx: GuiGraphicsExtractor)
    fun savePosition()
    fun show()
    fun hide()
}