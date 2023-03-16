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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColors(
    primary = Cyan700,
    primaryVariant = Cyan900,
    onPrimary = Color.White,
    secondary = Cyan700,
    secondaryVariant = Cyan900,
    onSecondary = Color.White,
    error = Cyan800
)

private val DarkColors = darkColors(
    primary = Cyan300,
    primaryVariant = Cyan700,
    onPrimary = Color.Black,
    secondary = Cyan300,
    onSecondary = Color.Black,
    error = Cyan200
)

@Composable
fun TravelCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        typography = TCTypography,
        shapes = TCShapes,
        content = content
    )
}
