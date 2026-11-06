import kotlinx.serialization.Serializable

@Serializable
data class Target(
    val id: String,
    val name: String,
    val url: String,
    val checkIntervalSeconds: Int = 60
)