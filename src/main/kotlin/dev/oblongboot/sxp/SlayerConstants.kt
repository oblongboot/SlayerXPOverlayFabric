package dev.oblongboot.sxp

data class SlayerItem(
    val magicFind: Boolean,
    val requiredXp: Long,
    val name: String
)

object SlayerConstants {
    val mainTable: Map<String, SlayerItem> = mapOf(
        "NULL_SPHERE" to SlayerItem(
            magicFind = false,
            requiredXp = -1,
            name = "Null Sphere"
        ),

        "TWILIGHT_ARROW_POISON" to SlayerItem(
            magicFind = false,
            requiredXp = 3300,
            name = "Twilight Arrow Poison"
        ),

        "SUMMONING_EYE" to SlayerItem(
            magicFind = true,
            requiredXp = 74250,
            name = "Summoning Eye"
        ),

        "MANA_STEAL_1" to SlayerItem(
            magicFind = true,
            requiredXp = 11183,
            name = "Enchanted Book (Mana Steal I)"
        ),

        "TRANSMISSION_TUNER" to SlayerItem(
            magicFind = true,
            requiredXp = 22366,
            name = "Transmission Tuner"
        ),

        "NULL_ATOM" to SlayerItem(
            magicFind = true,
            requiredXp = 10120,
            name = "Null Atom"
        ),

        "HAZMAT_ENDERMAN" to SlayerItem(
            magicFind = true,
            requiredXp = 32202,
            name = "Hazmat Enderman"
        ),

        "POCKET_ESPRESSO_MACHINE" to SlayerItem(
            magicFind = true,
            requiredXp = 128809,
            name = "Pocket Espresso Machine"
        ),

        "SMARTY_PANTS_1" to SlayerItem(
            magicFind = true,
            requiredXp = 28338,
            name = "Enchanted Book (Smarty Pants I)"
        ),

        "HANDY_BLOOD_CHALICE" to SlayerItem(
            magicFind = true,
            requiredXp = 283380,
            name = "Handy Blood Chalice"
        ),

        "SINFUL_DICE" to SlayerItem(
            magicFind = true,
            requiredXp = 108992,
            name = "Sinful Dice"
        ),

        "EXCEEDINGLY_RARE_ENDER_ARTIFACT_UPGRADER" to SlayerItem(
            magicFind = true,
            requiredXp = 1771125,
            name = "Exceedingly Rare Ender Artifact Upgrader"
        ),

        "ETHERWARP_MERGER" to SlayerItem(
            magicFind = true,
            requiredXp = 118075,
            name = "Etherwarp Merger"
        ),

        "JUDGEMENT_CORE" to SlayerItem(
            magicFind = true,
            requiredXp = 885562,
            name = "Judgement Core"
        ),

        "ENDER_SLAYER_7" to SlayerItem(
            magicFind = true,
            requiredXp = 3542250,
            name = "Enchanted Book (Ender Slayer VII)"
        )
    )

    val cosmeticTable: Map<String, SlayerItem> = mapOf(
        "ENDERSNAKE_RUNE" to SlayerItem(
            magicFind = false,
            requiredXp = 9438,
            name = "Endersnake Rune I"
        ),

        "END_RUNE" to SlayerItem(
            magicFind = true,
            requiredXp = 75505,
            name = "End Rune I"
        ),

        "VOID_CONQUEREROR_ENDERMAN_SKIN" to SlayerItem(
            magicFind = true,
            requiredXp = 302020,
            name = "Void Conqueror Enderman Skin"
        ),

        "ENCHANT_RUNE" to SlayerItem(
            magicFind = true,
            requiredXp = 1078642,
            name = "Enchant Rune I"
        )
    )
}