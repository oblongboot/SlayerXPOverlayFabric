package com.slayerxp.overlay.utils

object StopwatchUtil {
    var startTime: Long
    var elapsedTime: Long
    var isRunning: Boolean

    init {
        this.startTime = 0
        this.elapsedTime = 0
        this.isRunning = false
    }

    fun start() {
        if (!isRunning) {
            this.startTime = System.currentTimeMillis()
            this.isRunning = true
        }
    }

    fun stop() {
        if (this.isRunning) {
            this.elapsedTime = System.currentTimeMillis() - this.startTime
            this.isRunning = false
        }
    }

    fun reset() {
        this.elapsedTime = 0
        if (this.isRunning) {
            this.startTime = System.currentTimeMillis()
        }
    }

    fun getElapsedTime(): Long {
        return if (this.isRunning) this.elapsedTime + (System.currentTimeMillis() - this.startTime)
        else this.elapsedTime
    }

    fun stopAndReset() {
        if (this.isRunning) {
            this.isRunning = false
        }
        this.elapsedTime = 0
    }
}

