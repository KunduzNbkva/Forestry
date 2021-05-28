package kg.core

sealed class Event {
    class Message(val message: String) : Event()
    class RefreshData() : Event()

}
