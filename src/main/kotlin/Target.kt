package com.blastclouddi

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Target(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val url: String,
    val checkIntervalSeconds: Int = 60
)