package eu.alsk.transmissionremote.networkclient

/** Holds the user name and the password for the HTTP Basic authentication of the server. */
public data class Credentials(
    val username: String,
    val password: String,
) {
    // Do not show the password in logs or in error messages.
    override fun toString(): String = "Credentials(username=$username, password=***)"
}
