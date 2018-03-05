package odb

import com.google.common.util.concurrent.AbstractScheduledService
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.TimeUnit

class DBCheck: AbstractScheduledService() {
    var running = false
    var rate: Scheduler? = setRate()
    fun setRate(initDelay:Long = 8L, period:Long = 1L, unit: TimeUnit = TimeUnit.SECONDS): Scheduler {
        rate = Scheduler.newFixedRateSchedule(initDelay, period, unit)
        return rate!!
    }
    override fun scheduler(): Scheduler {
        return Scheduler.newFixedRateSchedule(8, 1, TimeUnit.SECONDS)
    }

    override fun runOneIteration() {
        if (running) return
        running = true

        println("Checking if DB Exists")
        runBlocking {
            Orientdb.db_action { _  ->
                println("DB Looks Good :) ")
                true
            }
        }

        Orientdb.callback()
        shutDown()
    }

}