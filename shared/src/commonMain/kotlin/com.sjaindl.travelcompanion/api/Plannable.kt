package com.sjaindl.travelcompanion.api

interface Plannable {
    fun description(): String

    // fun details(): NSMutableAttributedString
    // fun imageUrl(): String?
    fun getId(): String
    // fun getLink(): String?

    // fun getLinkText(): NSMutableAttributedString?
    // fun getNotes(): String
    // fun setNotes(notes: String)
    // fun encode(): Map<String, Any>
}

// TODO:
/*
internal fun Plannable.getLink(): String? =
    null

internal fun Plannable.getLinkText(): NSMutableAttributedString? =
    null
 */
