package common

object fcn {
    //Rabbit
    var queues = mutableListOf<String>()

    fun validateQueue(queue:String):String? = queues.firstOrNull({ queue.contains(it)})
    fun addQueue(queue:String) { queues.add(queue) }

}