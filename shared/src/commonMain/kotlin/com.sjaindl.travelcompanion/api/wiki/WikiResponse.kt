package com.sjaindl.travelcompanion.api.wiki

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WikiResponse(
    @SerialName("batchcomplete")
    val batchComplete: String,
    val query: WikiQuery,
)

@Serializable
data class WikiQuery(
    val pages: Map<String, WikiPage>,
) {
    val pageId: String? = pages.keys.firstOrNull()
}

@Serializable
data class WikiPage(
    @SerialName("pageid")
    val pageId: String,
    val ns: Int?,
    val title: String?,
)
