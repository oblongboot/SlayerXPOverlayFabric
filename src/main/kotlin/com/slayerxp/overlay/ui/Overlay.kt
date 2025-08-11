package com.slayerxp.overlay.ui

import net.minecraft.client.gui.DrawContext

interface Overlay {
    var x: Int
    var y: Int
    val width: Int
    val height: Int
    var shouldShow: Boolean
    
    fun draw(ctx: DrawContext)
    fun savePosition()
    fun show()
    fun hide()
}