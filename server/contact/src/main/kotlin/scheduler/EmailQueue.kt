package scheduler

import com.google.common.util.concurrent.AbstractScheduledService
import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit


object EmailQueue {
    val scheduler = EmailScheduler()
    fun start() {
        println("Email scheduler started")
        scheduler.startAsync()
    }
    fun add(subject: String, to: String, template: String, hTemplateVariables: MutableMap<String, Any>) {
        scheduler.queue.add(Email(subject, to, template, hTemplateVariables))
    }
}

class Email (val subject: String, val to: String, val template: String, val hTemplateVariables: MutableMap<String, Any>)
class EmailScheduler: AbstractScheduledService() {
    var running = false
    val queue = mutableListOf<Email>()

    override fun startUp() { Thread.sleep(100)}
    override fun scheduler(): Scheduler {
        return Scheduler.newFixedRateSchedule(0, 1, TimeUnit.SECONDS)
    }

    override fun runOneIteration() {
        if (running) return
        running = true

        val e = queue.firstOrNull()

        if (e != null) {
            launch {
                val sent = email.Email.sendHTMLEmail(e.subject, e.to, e.template, e.hTemplateVariables)
                if (sent) { queue.remove(e) }
                println("Email sent: $sent")
                running = false
            }
        } else {
            running = false
        }
    }
}