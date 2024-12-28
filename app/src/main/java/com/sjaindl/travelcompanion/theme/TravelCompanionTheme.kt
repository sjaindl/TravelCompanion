/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sjaindl.travelcompanion.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Cyan700,
    primaryContainer = Cyan900,
    onPrimary = Color.White,
    secondary = Cyan700,
    secondaryContainer = Cyan900,
    onSecondary = Color.White,
    error = Color.Red,
    background = Color(0xFF555555),
    onBackground = Color(0xFFDDD7D7)
)

private val DarkColors = darkColorScheme(
    primary = Cyan300,
    primaryContainer = Cyan700,
    onPrimary = Color.Black,
    secondary = Cyan300,
    onSecondary = Color.Black,
    error = Color.Red,
    background = Color(0xFF555555),
    onBackground = Color(0xFFDDD7D7),
)

@Composable
fun TravelCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = TCTypography,
        shapes = TCShapes,
        content = content
    )
}
