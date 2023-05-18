package com.sjaindl.travelcompanion.api.google

import android.net.Uri
import android.text.Spanned
import androidx.compose.ui.text.AnnotatedString

interface Plannable {
    fun description(): String

    fun details(): AnnotatedString
    fun imageUrl(): Uri?
    fun getId(): String
    fun getLink(): String?

    fun getLinkText(): Spanned?
    fun getNotes(): String
    fun setNotes(notes: String)
}
