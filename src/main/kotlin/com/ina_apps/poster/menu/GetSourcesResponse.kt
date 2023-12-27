package com.ina_apps.poster.menu

import kotlinx.serialization.Serializable

@Serializable
data class GetSourcesResponse(
    val response: List<SourcesResponse>
)

@Serializable
data class SourcesResponse(
    val id: Int,
    val name: String,
    val visible: Int,
    val type: Int
)
