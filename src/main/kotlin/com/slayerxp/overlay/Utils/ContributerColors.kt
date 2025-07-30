package com.slayerxp.overlay.util

import com.slayerxp.overlay.utils.APIUtils
import kotlinx.coroutines.*
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Style
import java.util.*
import kotlin.time.Duration.Companion.minutes

object ContributerColors {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile
    private var contributors: Map<String, String> = emptyMap()

    private var changeWords = true

    private val textCache = CacheUtils<OrderedText, OrderedText>(131_072, 5.minutes)
    private val stringVisitableCache = CacheUtils<StringVisitable, StringVisitable>(65_565, 5.minutes)

    init {
        loadContributors()
    }

    private fun loadContributors() {
        scope.launch {
            val url = "https://raw.githubusercontent.com/oblongboot/SlayerXPOverlayFabric/data/Contributers.json"
            val result = APIUtils.requestJson<Map<String, String>>(url)
            if (result != null) {
                contributors = result
                println("Loaded ${contributors.size} contributors")
            } else {
                println("Failed to load contributor colors")
            }
        }
    }

    fun setChangeWords(value: Boolean) {
        changeWords = value
    }

    fun transformText(orderedText: OrderedText?): OrderedText? {
        if (orderedText == null || !changeWords) return orderedText

        return textCache.getOrPut(orderedText) {
            val rawString = orderedText.toString()
            val modified = replaceContributorsWithColor(rawString)
            OrderedText.styledForwardsVisitedString(modified, Style.EMPTY)
        }
    }

    fun transformStringVisitable(stringVisitable: StringVisitable?): StringVisitable? {
        if (stringVisitable == null || !changeWords) return stringVisitable

        return stringVisitableCache.getOrPut(stringVisitable) {
            val inputString = stringVisitable.string
            val replaced = replaceContributorsWithColor(inputString)
            literalStringVisitable(replaced)
        }
    }

    private fun literalStringVisitable(str: String): StringVisitable = object : StringVisitable {
        override fun <T : Any> visit(visitor: StringVisitable.Visitor<T>): Optional<T> {
            return visitor.accept(str)
        }

        override fun <T : Any> visit(styledVisitor: StringVisitable.StyledVisitor<T>, style: Style): Optional<T> {
            return styledVisitor.accept(style, str)
        }

        override fun getString(): String = str
    }

    private fun replaceContributorsWithColor(input: String): String {
        var result = input
        for ((name, color) in contributors) {
            val regex = Regex("\\b${Regex.escape(name)}\\b", RegexOption.IGNORE_CASE)
            result = result.replace(regex, color)
        }
        return result
    }
}
