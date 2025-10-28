package com.slayerxp.overlay.utils

class StopwatchUtil {
    var startTime: Long
    var elapsedMillis: Long
    var isRunning: Boolean

    init {
        this.startTime = 0
        this.elapsedMillis = 0
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
            this.elapsedMillis = System.currentTimeMillis() - this.startTime
            this.isRunning = false
        }
    }

    fun reset() {
        this.elapsedMillis = 0
        if (this.isRunning) {
            this.startTime = System.currentTimeMillis()
        }
    }

    fun getElapsedTime(): Long {
        return if (this.isRunning) this.elapsedMillis + (System.currentTimeMillis() - this.startTime)
        else this.elapsedMillis
    }

    fun stopAndReset() {
        if (this.isRunning) {
            this.isRunning = false
        }
        this.elapsedMillis = 0
    }
}

