package dev.oblongboot.sxp.events

import meteordevelopment.orbit.EventBus
import meteordevelopment.orbit.EventHandler
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Modifier

object EventManager {

    @JvmField
    val EVENT_BUS = EventBus()

    @JvmStatic
    fun post(event: Any) {
        EVENT_BUS.post(event)
    }

    @JvmStatic
    fun discover(basePackage: String) {
        val reflections = Reflections(
            ConfigurationBuilder()
                .forPackage(basePackage)
                .setScanners(Scanners.SubTypes.filterResultsBy { true })
        )

        val classes = reflections.getSubTypesOf(Any::class.java)

        for (clazz in classes) {
            try {
                if (clazz.isInterface || Modifier.isAbstract(clazz.modifiers)) continue

                val hasHandler = clazz.declaredMethods.any {
                    it.isAnnotationPresent(EventHandler::class.java)
                }
                if (!hasHandler) continue

                val instance = try {
                    clazz.getDeclaredField("INSTANCE").get(null)
                } catch (e: Throwable) {
                    val ctor = clazz.declaredConstructors.firstOrNull { it.parameterCount == 0 }
                        ?: continue
                    ctor.isAccessible = true
                    ctor.newInstance()
                }

                println("ay")

                EVENT_BUS.subscribe(instance)

            } catch (e: Throwable) {
                println("Failed to register ${clazz.name}: ${e.message}")
            }
        }
    }
}