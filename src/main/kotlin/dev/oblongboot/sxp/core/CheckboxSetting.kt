package dev.oblongboot.sxp.core

import dev.oblongboot.sxp.utils.Render2D
import dev.oblongboot.sxp.settings.Config
import net.minecraft.client.gui.GuiGraphics
import java.awt.Color

class CheckboxSetting(
    name: String,
    val options: List<String>,
    defaultSelected: Set<Int> = emptySet(),
    description: String = ""
) : Setting<Set<Int>>(name, description, defaultSelected.toMutableSet()) {
    override var x = 0
    override var y = 0
    override val width = 240
    override val height = 30
    private val optionHeight = 22
    private val maxVisibleOptions = 4
    private var scrollOffset = 0
    private var animHeight = 0f
    private val animSpeed = 0.25f
    private var expanded = false

    override fun render(mouseX: Int, mouseY: Int) {
        val skija = dev.oblongboot.sxp.utils.skia.SkijaRenderer
        val isHovered = isWithinBounds(mouseX, mouseY)
        val baseColor = if (isHovered) skija.argb(160, 40, 80, 140) else skija.argb(100, 20, 40, 70)

        val visibleOptionCount = options.size.coerceAtMost(maxVisibleOptions)
        val targetHeight = if (expanded) visibleOptionCount * optionHeight else 0
        animHeight += (targetHeight - animHeight) * animSpeed
        
        skija.drawRoundedRect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, baseColor)
        
        if (isHovered) {
             skija.drawRoundedGlow(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, skija.argb(60, 0, 120, 255), 10f, 1f)
        }
        
        val borderColor = if (expanded || isHovered) skija.argb(255, 0, 150, 255) else skija.argb(150, 0, 100, 200)
        skija.drawRoundedRectBorderGradient(
            x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, 1f,
            borderColor, borderColor,
            dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT
        )

        val font = dev.oblongboot.sxp.ui.SettingsScreen.elementFont
        val textY = y + height / 3.8f// - 5f
        skija.drawText(name, x + 10f, textY, skija.argb(255, 255, 255, 255), font)

        val selectedSummary = if (value.isNotEmpty()) "${value.size} selected" else "NONE"
        val tw = skija.getTextWidth(selectedSummary, font)
        skija.drawText(selectedSummary, x + width - 35f - tw, textY, skija.argb(255, 180, 200, 230), font)

        val arrowSymbol = if (expanded) "▲" else "▼"
        val aw = skija.getTextWidth(arrowSymbol, font)
        skija.drawText(arrowSymbol, x + width - 15f - aw/2f, textY, skija.argb(255, 255, 255, 255), font)

        if (animHeight > 0.5f) {
            val actualVisibleOptions = if (expanded) visibleOptionCount else 0

            val maxScroll = (options.size - maxVisibleOptions).coerceAtLeast(0)
            scrollOffset = scrollOffset.coerceIn(0, maxScroll)
            
            val totalAnimHeight = animHeight
            if (totalAnimHeight > 2f) {
                skija.drawRoundedRect(x.toFloat(), (y + height).toFloat(), width.toFloat(), totalAnimHeight, 4f, skija.argb(220, 15, 25, 40))
                skija.drawRoundedRectBorderGradient(x.toFloat(), (y + height).toFloat(), width.toFloat(), totalAnimHeight, 4f, 1f, skija.argb(100, 0, 100, 200), skija.argb(100, 0, 100, 200), dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT)
            }

            for (i in 0 until actualVisibleOptions) {
                val optionIndex = i + scrollOffset
                if (optionIndex >= options.size) break

                val optionY = y + height + (i * optionHeight)
                val optionHovered = isWithinBounds(mouseX, mouseY, x, optionY, width, optionHeight)
                val isSelected = value.contains(optionIndex)

                if (optionHovered) {
                    skija.drawRoundedRect(x.toFloat(), optionY.toFloat(), width.toFloat(), optionHeight.toFloat(), 4f, skija.argb(80, 40, 80, 140))
                }

                val boxX = x + 8f
                val boxSize = 14f
                val boxY = optionY + (optionHeight - boxSize) / 2f
                
                if (isSelected) {
                    skija.drawRoundedRectGradient(boxX, boxY, boxSize, boxSize, 2f, skija.argb(200, 0, 150, 255), skija.argb(200, 0, 100, 200))
                } else {
                    skija.drawRoundedRect(boxX, boxY, boxSize, boxSize, 2f, skija.argb(100, 30, 40, 60))
                    skija.drawRoundedRectBorderGradient(boxX, boxY, boxSize, boxSize, 2f, 1f, skija.argb(150, 80, 100, 120), skija.argb(150, 60, 80, 100), dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT)
                }

                val textOffsetX = boxX + boxSize + 6f
                val textOffsetY = optionY + optionHeight / 2f - 1f
                val textColor = if (isSelected) skija.argb(255, 255, 255, 255) else skija.argb(220, 200, 200, 200)
                skija.drawText(options[optionIndex], textOffsetX, textOffsetY, textColor, font)
            }

            if (options.size > maxVisibleOptions) {
                val indicatorY = y + height + (actualVisibleOptions * optionHeight)
                val smallFont = dev.oblongboot.sxp.ui.SettingsScreen.smallFont
                if (scrollOffset > 0) {
                    skija.drawText("↑", x + width - 20f, y + height + 10f, skija.argb(200, 255, 255, 255), font)
                }
                if (scrollOffset < maxScroll) {
                    skija.drawText("↓", x + width - 20f, indicatorY - 5f, skija.argb(200, 255, 255, 255), font)
                }
                val positionText = "${scrollOffset + 1}-${(scrollOffset + actualVisibleOptions).coerceAtMost(options.size)} of ${options.size}"
                skija.drawText(positionText, x + 5f, indicatorY + 2f, skija.argb(150, 200, 200, 200), smallFont)
            }
        }
    }

    override fun onClick(mouseX: Int, mouseY: Int): Boolean {
        if (isWithinBounds(mouseX, mouseY)) {
            expanded = !expanded
            return true
        }

        if (expanded) {
            val actualVisibleOptions = maxVisibleOptions.coerceAtMost(options.size - scrollOffset)

            if (options.size > maxVisibleOptions) {
                val upArrowY = y + height + 5
                val downArrowY = y + height + (maxVisibleOptions * optionHeight) - 15

                if (isWithinBounds(mouseX, mouseY, x + width - 25, upArrowY - 5, 20, 15) && scrollOffset > 0) {
                    scrollOffset--
                    return true
                }

                if (isWithinBounds(mouseX, mouseY, x + width - 25, downArrowY - 5, 20, 15) && scrollOffset < (options.size - maxVisibleOptions)) {
                    scrollOffset++
                    return true
                }
            }

            for (i in 0 until actualVisibleOptions) {
                val optionY = y + height + (i * optionHeight)
                if (isWithinBounds(mouseX, mouseY, x, optionY, width, optionHeight)) {
                    val optionIndex = i + scrollOffset
                    value = if (value.contains(optionIndex)) value - optionIndex else value + optionIndex
                    return true
                }
            }
        }
        return false
    }

    override fun onHover(mouseX: Int, mouseY: Int): Boolean {
        if (isWithinBounds(mouseX, mouseY)) return true
        if (expanded) {
            val actualVisibleOptions = maxVisibleOptions.coerceAtMost(options.size - scrollOffset)
            for (i in 0 until actualVisibleOptions) {
                val optionY = y + height + (i * optionHeight)
                if (isWithinBounds(mouseX, mouseY, x, optionY, width, optionHeight)) return true
            }
        }
        return false
    }

    fun onScroll(mouseX: Int, mouseY: Int, scrollDelta: Double): Boolean {
        if (!expanded) return false

        val dropdownAreaHeight = maxVisibleOptions * optionHeight
        val isOverDropdown = isWithinBounds(mouseX, mouseY, x, y, width, height + dropdownAreaHeight)

        if (isOverDropdown && options.size > maxVisibleOptions) {
            val maxScroll = (options.size - maxVisibleOptions).coerceAtLeast(0)
            if (scrollDelta < 0 && scrollOffset < maxScroll) {
                scrollOffset++
                return true
            } else if (scrollDelta > 0 && scrollOffset > 0) {
                scrollOffset--
                return true
            }
        }
        return false
    }

    override fun reset() {
        value = default
    }

    override fun onValueChanged(oldValue: Set<Int>, newValue: Set<Int>) {
        Config.setMultiSelect(name, newValue.toList())
        println("$name changed: $newValue")
    }

    private fun isWithinBounds(mouseX: Int, mouseY: Int, bx: Int = x, by: Int = y, bw: Int = width, bh: Int = height): Boolean {
        return mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh
    }
}
