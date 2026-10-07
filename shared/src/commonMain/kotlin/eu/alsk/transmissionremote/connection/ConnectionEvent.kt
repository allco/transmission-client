package eu.alsk.transmissionremote.connection

/** A one-time signal from [ConnectionViewModel] to the connection screen. */
internal sealed interface ConnectionEvent {
    /** The view model saved the connection. */
    data object Saved : ConnectionEvent
}
