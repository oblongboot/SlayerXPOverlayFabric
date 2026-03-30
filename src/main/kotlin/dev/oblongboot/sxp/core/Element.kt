package dev.oblongboot.sxp.core

import net.minecraft.client.gui.GuiGraphics

interface Element {
    val name: String
    val description: String
    var x: Int
    var y: Int
    val width: Int
    val height: Int
    
    fun render(mouseX: Int, mouseY: Int)
    fun onClick(mouseX: Int, mouseY: Int): Boolean
    fun onHover(mouseX: Int, mouseY: Int): Boolean
    fun isWithinBounds(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }
}