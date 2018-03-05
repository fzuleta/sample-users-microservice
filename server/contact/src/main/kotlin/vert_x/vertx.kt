package vert_x
import scheduler.EmailQueue
import vertxl.VertX

object vertx {
    fun start() {
        VertX.initCallback = {
            val v = VertX.vertx!!
            v.deployVerticle(vert_x.email.send_email())


            EmailQueue.start()
        }

        VertX.start()
    }
}