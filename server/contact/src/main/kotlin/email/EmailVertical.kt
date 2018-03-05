package email

import io.vertx.kotlin.coroutines.CoroutineVerticle
import scheduler.EmailQueue

class EmailVertical(val subject: String, val to: String, val template: String, val hTemplateVariables: MutableMap<String, Any>): CoroutineVerticle() {
    override suspend fun start() {
        EmailQueue.add(subject, to, template, hTemplateVariables)
    }
}