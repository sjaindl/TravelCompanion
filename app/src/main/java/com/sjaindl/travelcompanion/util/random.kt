package com.sjaindl.travelcompanion.util

import kotlin.random.Random

private val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun randomStringByKotlinRandom(size: Int) =
    (1..size).map { Random.nextInt(0, charPool.size).let { charPool[it] } }.joinToString("")
