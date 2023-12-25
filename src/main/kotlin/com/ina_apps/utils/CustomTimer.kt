package com.ina_apps.utils

import java.util.*

class CustomTimer {

    private val timer = Timer()

    fun createNewTimer(
        period: Long,
        task: () -> Unit
    ) {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                task()
            }
        }, 0, period)
    }

    fun canselAllTimers() {
        timer.cancel()
    }
}
