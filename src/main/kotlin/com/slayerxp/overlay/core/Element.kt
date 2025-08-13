package com.slayerxp.overlay.core

import net.minecraft.client.gui.DrawContext

interface Element {
    val name: String
    val description: String
    var x: Int
    var y: Int
    val width: Int
    val height: Int
    
    fun render(ctx: DrawContext)
    fun onClick(mouseX: Int, mouseY: Int): Boolean
    fun onHover(mouseX: Int, mouseY: Int): Boolean
    fun isWithinBounds(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }
}