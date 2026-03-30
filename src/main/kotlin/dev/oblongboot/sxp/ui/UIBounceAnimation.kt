package dev.oblongboot.sxp.ui

class UIBounceAnimation(private val duration: Long = 400L) {

    private var startTime: Long = -1L
    private var running = false

    fun start() {
        startTime = System.currentTimeMillis()
        running = true
    }

    fun reset() {
        startTime = System.currentTimeMillis()
    }

    fun stop() {
        running = false
    }

    fun isRunning(): Boolean = running

    fun getProgress(): Float {
        if (startTime == -1L) return 0f

        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed >= duration) {
            running = false
            return 1f
        }

        return elapsed.toFloat() / duration
    }

    fun get(): Float {
        val t = getProgress()
        return easeOutBack(t)
    }
}

fun easeOutBack(t: Float, s: Float = 1.70158f): Float {
    val x = t - 1f
    return 1f + (s + 1f) * x * x * x + s * x * x
}