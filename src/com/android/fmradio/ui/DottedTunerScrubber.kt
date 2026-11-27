/*
 * Copyright (C) 2026 RevampedFMRadio Project
 * Copyright (C) 2026 Halcyon Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.fmradio.ui

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.round

/**
 * Larger segmented Material 3 slider for smooth FM tuning without drag jitter.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DottedTunerScrubber(
    currentFrequency: Float,
    minFrequency: Float = 87.5f,
    maxFrequency: Float = 108.0f,
    isPlaying: Boolean = false,
    enabled: Boolean = true,
    onFrequencySeek: (Float) -> Unit,
    onScrubbingStateChange: (Boolean) -> Unit = {},
    onPreviewFrequencyChange: (Float) -> Unit = {},
    externalTuneRevision: Int = 0,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }

    var isDragging by remember { mutableStateOf(false) }
    var dragValue by remember { mutableFloatStateOf(currentFrequency) }
    var pendingSeekFreq by remember { mutableFloatStateOf(currentFrequency) }
    var lastHapticFreq by remember { mutableFloatStateOf(currentFrequency) }
    var lastDispatchedFreq by remember { mutableFloatStateOf(currentFrequency) }
    var settlingTargetFreq by remember { mutableStateOf<Float?>(null) }

    // Keep released thumb locked at its target until hardware tune finishes or timeout expires.
    LaunchedEffect(currentFrequency, isDragging, settlingTargetFreq) {
        if (isDragging) return@LaunchedEffect
        val target = settlingTargetFreq
        if (target != null) {
            if (abs(currentFrequency - target) < 0.05f) {
                settlingTargetFreq = null
                dragValue = currentFrequency
                pendingSeekFreq = currentFrequency
                lastHapticFreq = currentFrequency
                lastDispatchedFreq = currentFrequency
            }
            return@LaunchedEffect
        }
        dragValue = currentFrequency
        pendingSeekFreq = currentFrequency
        lastHapticFreq = currentFrequency
        lastDispatchedFreq = currentFrequency
    }

    LaunchedEffect(settlingTargetFreq) {
        if (settlingTargetFreq == null) return@LaunchedEffect
        delay(1500L)
        settlingTargetFreq = null
    }

    LaunchedEffect(externalTuneRevision) {
        if (externalTuneRevision > 0 && !isDragging) {
            settlingTargetFreq = null
        }
    }

    LaunchedEffect(enabled) {
        if (!enabled && !isDragging) {
            settlingTargetFreq = null
        }
    }

    // Coalesce slow-drag frequency steps so native tune ioctls do not block the UI frame rate.
    LaunchedEffect(isDragging, pendingSeekFreq) {
        if (!isDragging) return@LaunchedEffect
        delay(160L)
        if (pendingSeekFreq != lastDispatchedFreq) {
            lastDispatchedFreq = pendingSeekFreq
            onFrequencySeek(pendingSeekFreq)
        }
    }

    val displayedValue = (if (isDragging || settlingTargetFreq != null) dragValue else currentFrequency)
        .coerceIn(minFrequency, maxFrequency)
    val disabledScale = if (enabled) 1f else 0.38f

    val sliderColors = SliderDefaults.colors(
        thumbColor = MaterialTheme.colorScheme.primary,
        activeTrackColor = MaterialTheme.colorScheme.primary,
        inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        activeTickColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.65f),
        inactiveTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
    )
    val activeDotColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.65f * disabledScale)
    val inactiveDotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f * disabledScale)

    Slider(
        value = displayedValue,
        onValueChange = { rawValue ->
            if (!isDragging) {
                isDragging = true
                settlingTargetFreq = null
                onScrubbingStateChange(true)
            }
            dragValue = rawValue
            val stepped = (round(rawValue * 10f) / 10f).coerceIn(minFrequency, maxFrequency)
            pendingSeekFreq = stepped
            onPreviewFrequencyChange(stepped)
            if (stepped != lastHapticFreq) {
                lastHapticFreq = stepped
                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }
        },
        onValueChangeFinished = {
            val target = pendingSeekFreq
            dragValue = target
            settlingTargetFreq = if (abs(currentFrequency - target) < 0.05f) null else target
            isDragging = false
            onScrubbingStateChange(false)
            onPreviewFrequencyChange(target)
            if (target != lastDispatchedFreq) {
                lastDispatchedFreq = target
                onFrequencySeek(target)
            }
        },
        valueRange = minFrequency..maxFrequency,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = sliderColors,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                colors = sliderColors,
                enabled = enabled,
                thumbSize = DpSize(6.dp, 46.dp)
            )
        },
        track = { sliderState ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            ) {
                SliderDefaults.Track(
                    sliderState = sliderState,
                    modifier = Modifier.fillMaxSize(),
                    enabled = enabled,
                    colors = sliderColors,
                    drawStopIndicator = null,
                    thumbTrackGapSize = 6.dp,
                    trackInsideCornerSize = 2.dp
                )
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val centerY = size.height / 2f
                    val dotRadius = 2.5.dp.toPx()
                    val inset = 10.dp.toPx()
                    val thumbGapHalf = 11.dp.toPx()
                    val fraction = ((sliderState.value - minFrequency) / (maxFrequency - minFrequency))
                        .coerceIn(0f, 1f)
                    val thumbX = width * fraction
                    val stopCount = 8

                    for (i in 0 until stopCount) {
                        val stopFraction = i.toFloat() / (stopCount - 1)
                        val stopX = inset + (width - inset * 2f) * stopFraction
                        if (abs(stopX - thumbX) > thumbGapHalf) {
                            drawCircle(
                                color = if (stopX < thumbX) activeDotColor else inactiveDotColor,
                                radius = dotRadius,
                                center = Offset(stopX, centerY)
                            )
                        }
                    }
                }
            }
        }
    )
}
