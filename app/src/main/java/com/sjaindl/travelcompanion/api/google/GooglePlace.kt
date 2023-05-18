package com.sjaindl.travelcompanion.api.google

import android.net.Uri
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import androidx.compose.ui.text.buildAnnotatedString
import com.sjaindl.travelcompanion.SecretConstants

fun GooglePlace.asPlannable() = object : Plannable {

    fun getLinkAttributedText(link: String): Spanned? {
        return Html.fromHtml(link, Html.FROM_HTML_MODE_LEGACY)
    }

    fun getLink(text: String): String? {
        val linkBeginIndex = text.indexOf("<a href=\"")
        val linkEndIndex = text.indexOf(">") - 1

        if (linkBeginIndex != -1 && linkEndIndex != -1) {
            return text.substring(linkBeginIndex + 9, linkEndIndex)
        }

        return null
    }

    override fun description(): String = name

    override fun details() = buildAnnotatedString {
        append(vicinity)
        val rating = rating
        if (rating != null) {
            append(". $rating/5*")
        }
        val photos = photos ?: emptyList()
        val photoAttribution = photos.firstOrNull()?.htmlAttributions?.firstOrNull()?.apply {
            TextUtils.htmlEncode(this)
        }
        val linkText = photoAttribution?.let { getLinkAttributedText(it) }
        if (linkText != null) {
            append(linkText)
        }
    }

    override fun imageUrl(): Uri? {
        val photoReference = photos?.firstOrNull()?.photoReference ?: return null
        val uriString =
            "${GoogleConstants.UrlComponents.pathPhotos}?${GoogleConstants.ParameterKeys.maxWidth}=${GoogleConstants.ParameterValues.maxWidth}&${GoogleConstants.ParameterKeys.photoReference}=${photoReference}&${GoogleConstants.ParameterKeys.key}=${SecretConstants.apiKeyGooglePlaces}"
        return Uri.parse(uriString)
    }

    override fun getId(): String = placeId

    override fun getLink(): String? {
        val attributionLink = photos?.firstOrNull()?.htmlAttributions?.firstOrNull() ?: return null

        return getLink(text = attributionLink)
    }

    override fun getLinkText(): Spanned? {
        val attributed = photos?.firstOrNull()?.htmlAttributions?.firstOrNull()?.let {
            Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
        }

        return attributed
    }


    override fun getNotes(): String {
        TODO("Not yet implemented")
    }

    override fun setNotes(notes: String) {
        TODO("Not yet implemented")
    }
}
