package com.blastclouddi

import kotlinx.serialization.Serializable

@Serializable
data class CheckResult(
    val id: Long = 0,
    val targetId: String,
    val checkedAt: String,
    val isUp: Boolean,
    val responseTimeMs: Long?,
    val statusCode: Int?,
    val errorMessage: String? = null
)