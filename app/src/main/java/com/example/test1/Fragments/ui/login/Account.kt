import java.util.UUID

data class Account(
    val appName: String,
    val username: String,
    val accountId: String,
    val password: String,
    val id: String = UUID.randomUUID().toString()
)