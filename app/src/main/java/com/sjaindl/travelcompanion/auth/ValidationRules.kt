package com.sjaindl.travelcompanion.auth

object ValidationRules {
    const val emailRegex = "\\A[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
            "@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\z"
}

fun String.isValidMail(): Boolean {
    return matches(regex = Regex(pattern = ValidationRules.emailRegex))
}

fun String.isValidName(): Boolean {
    return isNotEmpty()
}

fun String.isValidPassword(): Boolean {
    return length >= 6
}
