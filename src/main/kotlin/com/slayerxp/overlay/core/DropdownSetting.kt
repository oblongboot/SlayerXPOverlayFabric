package com.slayerxp.overlay.core

import com.slayerxp.overlay.utils.Render2D
import com.slayerxp.overlay.settings.Config
import net.minecraft.client.gui.DrawContext
import java.awt.Color

class DropdownSetting(
    name: String,
    val options: List<String>,
    defaultIndex: Int = 0,
    description: String = "",
    private val onValueChangeAction: (() -> Unit)? = null
) : Setting<Int>(name, description, defaultIndex) {
    override val width = 240
    override val height = 30
    private var expanded = false
    private val optionHeight = 22
    private var scrollOffset = 0
    private var animHeight = 0f
    private val animSpeed = 0.25f

    override fun render(ctx: DrawContext) {
        val currentText = options.getOrNull(value) ?: "N/A"
        val baseColor = Color(50, 60, 90, 180) 
        val isHovered = isWithinBounds(Render2D.Mouse.x.toInt(), Render2D.Mouse.y.toInt())
        val hoverColor = if (isHovered) baseColor.brighter() else baseColor
        val maxVisibleOptions = 3 
        val visibleOptionCount = options.size.coerceAtMost(maxVisibleOptions)
        val targetHeight = if (expanded) visibleOptionCount * optionHeight else 0 
        animHeight += (targetHeight - animHeight) * animSpeed

        Render2D.drawWhateverTheFuckThisIs(ctx, x, y, width, height, 6, hoverColor)
        Render2D.drawOutline(ctx, x, y, width, height, Color(0, 180, 255))
        
        val textY = y + (height - Render2D.textRenderer.fontHeight) / 2
        Render2D.drawString(ctx, name, x + 10, textY, 1f, true)
        
        val displayText = if (value >= 0 && value < options.size) {
            options[value]
        } else {
            "DROPDOWN"
        }
        Render2D.drawString(ctx, displayText, x + width - 80, textY, 1f, true)
        val arrowX = x + width - 15
        val arrowSymbol = if (expanded) "▲" else "▼"
        Render2D.drawString(ctx, arrowSymbol, arrowX, textY, 1f, true)

        if (animHeight > 0.5f) {
            val actualVisibleOptions = if (expanded) maxVisibleOptions else 0

            val maxScroll = (options.size - maxVisibleOptions).coerceAtLeast(0)
            scrollOffset = scrollOffset.coerceIn(0, maxScroll)

            for (i in 0 until actualVisibleOptions) {
                val optionIndex = i + scrollOffset
                if (optionIndex >= options.size) break
                
                val optionY = y + height + (i * optionHeight)
                val optionHovered = isWithinBounds(
                    Render2D.Mouse.x.toInt(),
                    Render2D.Mouse.y.toInt(),
                    x,
                    optionY,
                    width,
                    optionHeight
                )
                
                val optionColor = when {
                    optionIndex == value -> Color(0, 120, 200, 220) 
                    optionHovered -> Color(60, 75, 110, 200) 
                    else -> Color(40, 50, 75, 180) 
                }

                Render2D.drawWhateverTheFuckThisIs(ctx, x, optionY, width, optionHeight, 3, optionColor)
                Render2D.drawOutline(ctx, x, optionY, width, optionHeight, Color(0, 130, 200))
                
                val optionTextY = optionY + (optionHeight - Render2D.textRenderer.fontHeight) / 2
                Render2D.drawString(ctx, options[optionIndex], x + 10, optionTextY, 1f, true)
            }
            
        
            if (options.size > maxVisibleOptions) {
                val indicatorY = y + height + (actualVisibleOptions * optionHeight)
                if (scrollOffset > 0) {
                    Render2D.drawString(ctx, "↑", x + width - 20, y + height + 5, 1f, true)
                }
                
                if (scrollOffset < maxScroll) {
                    Render2D.drawString(ctx, "↓", x + width - 20, indicatorY - 15, 1f, true)
                }
                val positionText = "${scrollOffset + 1}-${(scrollOffset + actualVisibleOptions).coerceAtMost(options.size)} of ${options.size}"
                Render2D.drawString(ctx, positionText, x + 5, indicatorY + 2, 0.8f, true)
            }
        }
    }

    override fun onClick(mouseX: Int, mouseY: Int): Boolean {
        if (isWithinBounds(mouseX, mouseY)) {
            expanded = !expanded
            if (expanded) {
                val maxVisibleOptions = 3
                if (value >= 0) {
                    scrollOffset = (value - maxVisibleOptions / 2).coerceIn(0, (options.size - maxVisibleOptions).coerceAtLeast(0))
                }
            }
            return true
        }

        if (expanded) {
            val maxVisibleOptions = 3
            
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

            for (i in 0 until maxVisibleOptions.coerceAtMost(options.size - scrollOffset)) {
                val optionY = y + height + (i * optionHeight)
                if (isWithinBounds(mouseX, mouseY, x, optionY, width, optionHeight)) {
                    value = i + scrollOffset
                    expanded = false
                    return true
                }
            }
            
            expanded = false
        }
        return false //bleh
    }

    override fun onHover(mouseX: Int, mouseY: Int): Boolean {
        if (isWithinBounds(mouseX, mouseY)) return true
        if (expanded) {
            val maxVisibleOptions = 3
            
            if (options.size > maxVisibleOptions) {
                val upArrowY = y + height + 5
                val downArrowY = y + height + (maxVisibleOptions * optionHeight) - 15
                
                if (isWithinBounds(mouseX, mouseY, x + width - 25, upArrowY - 5, 20, 15) ||
                    isWithinBounds(mouseX, mouseY, x + width - 25, downArrowY - 5, 20, 15)) {
                    return true
                }
            }
            
            for (i in 0 until maxVisibleOptions.coerceAtMost(options.size - scrollOffset)) {
                val optionY = y + height + (i * optionHeight)
                if (isWithinBounds(mouseX, mouseY, x, optionY, width, optionHeight)) return true
            }
        }
        return false
    }

    override fun reset() {
        value = default
    }

    fun onScroll(mouseX: Int, mouseY: Int, scrollDelta: Double): Boolean {
        if (!expanded) return false

        val maxVisibleOptions = 3
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

    override fun onValueChanged(oldValue: Int, newValue: Int) {
        Config.setDropdown(name, newValue)
        onValueChangeAction?.invoke()
        println("$name changed: ${options.getOrNull(oldValue)} -> ${options.getOrNull(newValue)}")
    }

    private fun isWithinBounds(mouseX: Int, mouseY: Int, bx: Int = x, by: Int = y, bw: Int = width, bh: Int = height): Boolean {
        return mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh
    }
}