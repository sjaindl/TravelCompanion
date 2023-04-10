package com.sjaindl.travelcompanion.api.wiki

interface WikiClient {
    suspend fun fetchWikiLink(name: String, domain: String): Result<WikiResponse>
}
