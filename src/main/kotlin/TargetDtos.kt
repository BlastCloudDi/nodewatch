package com.blastclouddi

import kotlinx.serialization.Serializable

@Serializable
data class CreateTargetRequest(
    val name: String,
    val url: String,
    val checkIntervalSeconds: Int = 60
)

@Serializable
data class ErrorResponse(
    val message: String
)