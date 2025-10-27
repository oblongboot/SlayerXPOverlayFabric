package com.slayerxp.overlay.core

import com.slayerxp.overlay.utils.Render2D
import com.slayerxp.overlay.settings.Config
import net.minecraft.client.gui.DrawContext
import java.awt.Color

class CheckboxSetting(
    name: String,
    val options: List<String>,
    defaultSelected: Set<Int> = emptySet(),
    description: String = ""
) : Setting<Set<Int>>(name, description, defaultSelected.toMutableSet()) {
    override val width = 240
    override val height = 30
    private val optionHeight = 22
    private val maxVisibleOptions = 4
    private var scrollOffset = 0
    private var animHeight = 0f
    private val animSpeed = 0.25f
    private var expanded = false

    override fun render(ctx: DrawContext) {
        val baseColor = Color(50, 60, 90, 180)
        val isHovered = isWithinBounds(Render2D.Mouse.x.toInt(), Render2D.Mouse.y.toInt())
        val hoverColor = if (isHovered) baseColor.brighter() else baseColor

        val visibleOptionCount = options.size.coerceAtMost(maxVisibleOptions)
        val targetHeight = if (expanded) visibleOptionCount * optionHeight else 0
        animHeight += (targetHeight - animHeight) * animSpeed
        Render2D.drawWhateverTheFuckThisIs(ctx, x, y, width, height, 6, hoverColor)
        Render2D.drawOutline(ctx, x, y, width, height, Color(0, 180, 255))
        val textY = y + (height - Render2D.textRenderer.fontHeight) / 2
        Render2D.drawString(ctx, name, x + 10, textY, 1f, true)

        val selectedSummary = if (value.isNotEmpty()) "${value.size} selected" else "NONE"
        Render2D.drawString(ctx, selectedSummary, x + width - 100, textY, 1f, true)

        val arrowSymbol = if (expanded) "▲" else "▼"
        Render2D.drawString(ctx, arrowSymbol, x + width - 15, textY, 1f, true)

        if (animHeight > 0.5f) {
            val actualVisibleOptions = if (expanded) visibleOptionCount else 0

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
                val isSelected = value.contains(optionIndex)

                val optionColor = when {
                    isSelected -> Color(0, 120, 80, 220)
                    optionHovered -> Color(70, 80, 100, 200)
                    else -> Color(50, 60, 80, 180)
                }

                Render2D.drawWhateverTheFuckThisIs(ctx, x, optionY, width, optionHeight, 3, optionColor)
                Render2D.drawOutline(ctx, x, optionY, width, optionHeight, Color(0, 130, 200))


                val boxX = x + 8
                val boxSize = 14
                val boxColor = if (isSelected) Color(0, 180, 80, 220) else Color(60, 60, 70, 180)
                Render2D.drawWhateverTheFuckThisIs(ctx, boxX, optionY + 4, boxSize, boxSize, 3, boxColor)
                Render2D.drawOutline(ctx, boxX, optionY + 4, boxSize, boxSize, Color.WHITE)
                if (isSelected) Render2D.drawString(ctx, "✔", boxX + 3, optionY + 4, 1f, true)


                val textOffsetX = boxX + boxSize + 6
                val textOffsetY = optionY + (optionHeight - Render2D.textRenderer.fontHeight) / 2
                Render2D.drawString(ctx, options[optionIndex], textOffsetX, textOffsetY, 1f, true)
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
