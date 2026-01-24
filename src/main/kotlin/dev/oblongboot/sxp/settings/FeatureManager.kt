package dev.oblongboot.sxp.settings

import dev.oblongboot.sxp.settings.impl.*

/**
 * Handles registration, lookup, and state management for all [Feature] instances.
 *
 * This manager acts as the central point for:
 * - Registering and tracking all features
 * - Synchronizing enabled states with the saved [Config]
 * - Notifying features when their toggled state changes
 */
object FeatureManager {
    /** A list of all registered [Feature]s. */
    private val features = mutableListOf<Feature>()

    /**
     * Registers a [Feature] instance into the manager.
     *
     * Should typically be called once for each feature during initialization.
     *
     * @param feature The [Feature] instance to register.
     */
    fun registerFeature(feature: Feature) {
        features.add(feature)
    }

    /**
     * Finds a registered [Feature] by its name.
     *
     * The search is case-insensitive.
     *
     * @param name The name of the feature to look up.
     * @return The matching [Feature] instance, or `null` if not found.
     */
    fun getFeatureByName(name: String): Feature? {
        return features.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Retrieves a map of all saved configuration states for toggles, dropdowns, and multiselects.
     *
     * Includes both dynamically registered [Feature] toggles and any known UI elements that are
     * not directly tied to a feature (e.g., dropdowns or checkboxes).
     *
     * @return A [Map] of config keys and their associated values.
     */
    fun getAllConfigStates(): Map<String, Any> {
        val states = mutableMapOf<String, Any>()

        features.forEach { feature ->
            states[feature.name] = Config.isToggled(feature.name)
        }

        val knownDropdowns = listOf("MessageColor")
        knownDropdowns.forEach { dropdownName ->
            states[dropdownName] = Config.getDropdown(dropdownName, 0)
        }

        val knownCheckboxes = listOf("MultiSelectTest", "BossInfoCheckbox")
        knownCheckboxes.forEach { checkboxName ->
            states[checkboxName] = Config.getMultiSelect(checkboxName, setOf(0))
        }

        // I dont think this was implemented because it wasnt working for me
        val knownSwitches = listOf("ShortPrefix", "IsGradient", "AutoCallMaddox", "MiniBossAlert")
        knownSwitches.forEach {switchName ->
            states[switchName] = Config.isToggled(switchName)
        }

        return states
    }

    /**
     * Loads all feature toggle states from the saved [Config].
     *
     * This should be called when initializing the overlay or reloading config files
     * to ensure that each [Feature]'s `enabled` state reflects the stored values.
     */
    fun loadAllFeatureStates() {
        features.forEach { feature ->
            val state = Config.isToggled(feature.name)
            feature.enabled = state
        }
    }

    /**
     * Notifies a specific [Feature] that its toggle state has changed.
     *
     * This triggers the [Feature.onToggle] callback for the feature with the matching name.
     * Typically called from UI elements (e.g., [SwitchConfig]) when the user enables/disables a feature.
     *
     * @param name The name of the feature whose toggle state changed.
     * @param newValue The new enabled state (`true` if enabled, `false` if disabled).
     */
    fun notifyToggleChanged(name: String, newValue: Boolean) {
        getFeatureByName(name)?.onToggle(newValue)
    }
}
