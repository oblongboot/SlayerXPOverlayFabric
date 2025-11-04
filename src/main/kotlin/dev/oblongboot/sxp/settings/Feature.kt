package dev.oblongboot.sxp.settings

interface Feature {
    val name: String
    val description: String
    val default: Boolean
    val category: String
    var enabled: Boolean
    fun onToggle(newState: Boolean) {}
}
