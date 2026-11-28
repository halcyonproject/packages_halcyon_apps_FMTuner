/*
 * Copyright (C) 2026 RevampedFMRadio Project
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

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Bitmap
import java.util.Locale
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import kotlin.math.roundToInt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TonalToggleButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.abs
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.fmradio.FmPreferences
import com.android.fmradio.FmRecorder
import com.hlcyn.tuner.R
import com.android.fmradio.ui.artwork.StationArtworkGenerator
import com.android.fmradio.ui.favorites.FmFavoritesSheet
import com.android.fmradio.ui.recording.FmRecordingsScreen
import com.android.fmradio.ui.recording.RecordingItem
import com.android.fmradio.ui.settings.FmSettingsScreen
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class FavoriteStationItem(
    val id: Long,
    val frequency: Int,
    val name: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FmPlayerScreen(
    currentFrequency: Float,
    stationName: String,
    radioText: String,
    isPlaying: Boolean,
    isFavorite: Boolean,
    isSpeakerUsed: Boolean,
    isAudioRoutingAvailable: Boolean = false,
    isRecording: Boolean,
    isSeeking: Boolean = false,
    isRecordingSheetVisible: Boolean = false,
    recordingState: Int = FmRecorder.STATE_IDLE,
    recordDurationMs: Long = 0L,
    recordSizeBytes: Long = 0L,
    defaultRecordingName: String = "",
    favoritesList: List<FavoriteStationItem>,
    recordingsList: List<RecordingItem> = emptyList(),
    onPlayPauseClick: () -> Unit,
    onPrevStationClick: () -> Unit,
    onNextStationClick: () -> Unit,
    onTuneDownClick: () -> Unit,
    onTuneUpClick: () -> Unit,
    onFrequencySeek: (Float) -> Unit,
    onToggleFavoriteClick: () -> Unit,
    onToggleSpeakerClick: () -> Unit,
    onRecordClick: () -> Unit,
    onStationSelect: (Int) -> Unit,
    onReloadFavorites: () -> Unit = {},
    onDismissRecordingSheet: () -> Unit = {},
    onOpenRecordings: () -> Unit = onRecordClick,
    onStartRecording: () -> Unit = {},
    onStopRecording: () -> Unit = {},
    onSaveRecording: (String) -> Unit = {},
    onDiscardRecording: () -> Unit = {},
    onDeleteRecording: (RecordingItem) -> Unit = {}
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val isDarkTheme = isSystemInDarkTheme()
    val freqInt = (currentFrequency * 10).toInt()
    val artworkResult by produceState<StationArtworkGenerator.StationArtworkResult?>(
        initialValue = null,
        key1 = freqInt,
        key2 = Triple(colorScheme.primary, colorScheme.tertiary, isDarkTheme)
    ) {
        value = withContext(Dispatchers.Default) {
            StationArtworkGenerator.getStationArtworkResult(
                context,
                freqInt,
                stationName,
                colorScheme.primary.toArgb(),
                colorScheme.secondary.toArgb(),
                colorScheme.tertiary.toArgb(),
                colorScheme.primaryContainer.toArgb(),
                colorScheme.secondaryContainer.toArgb(),
                colorScheme.tertiaryContainer.toArgb(),
                colorScheme.surfaceContainerLow.toArgb(),
                isDarkTheme
            )
        }
    }
    val artworkBitmap = artworkResult?.bitmap

    var isScrubbing by remember { mutableStateOf(false) }
    var scrubbingFrequency by remember { mutableFloatStateOf(currentFrequency) }
    var pendingSeekTarget by remember { mutableStateOf<Float?>(null) }
    var externalTuneRevision by remember { mutableIntStateOf(0) }
    LaunchedEffect(currentFrequency, isScrubbing) {
        if (!isScrubbing) {
            scrubbingFrequency = currentFrequency
        }
    }
    LaunchedEffect(currentFrequency, pendingSeekTarget) {
        val target = pendingSeekTarget
        if (target != null && abs(currentFrequency - target) < 0.05f) {
            pendingSeekTarget = null
        }
    }
    LaunchedEffect(pendingSeekTarget) {
        if (pendingSeekTarget == null) return@LaunchedEffect
        delay(1500L)
        pendingSeekTarget = null
    }
    LaunchedEffect(isPlaying, isSeeking) {
        if (!isPlaying || isSeeking) {
            pendingSeekTarget = null
        }
    }
    val displayedFrequency = when {
        isScrubbing -> scrubbingFrequency
        pendingSeekTarget != null -> pendingSeekTarget!!
        else -> currentFrequency
    }
    var isSettingsVisible by remember { mutableStateOf(false) }
    var reduceMotion by remember { mutableStateOf(FmPreferences.isReduceMotionEnabled(context)) }

    val dominantGlowTarget = artworkResult?.let { Color(it.dominantColor) } ?: colorScheme.primary
    val secondaryGlowTarget = artworkResult?.let { Color(it.secondaryColor) } ?: colorScheme.tertiary
    val glowPrimary by animateColorAsState(
        targetValue = dominantGlowTarget,
        animationSpec = if (reduceMotion) snap() else tween(durationMillis = 550, easing = FastOutSlowInEasing),
        label = "glow_primary"
    )
    val glowSecondary by animateColorAsState(
        targetValue = secondaryGlowTarget,
        animationSpec = if (reduceMotion) snap() else tween(durationMillis = 550, easing = FastOutSlowInEasing),
        label = "glow_secondary"
    )

    // App Entrance Transition on Fresh Open
    var hasEntered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        hasEntered = true
    }
    val entranceAlpha by animateFloatAsState(
        targetValue = if (hasEntered) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "entrance_alpha"
    )
    val entranceOffsetY by animateFloatAsState(
        targetValue = if (hasEntered) 0f else 28f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "entrance_offset"
    )

    // Dynamic Variable Font State Definitions with smooth, granular spring transitions
    val targetWght = when {
        reduceMotion -> 700f
        isRecording -> 700f
        isSeeking -> 800f
        isScrubbing -> 750f
        isPlaying -> 700f
        else -> 400f
    }

    // Breathing width animation: oscillates between 100f and 135f
    val breathingTransition = rememberInfiniteTransition(label = "title_breathing")
    val breathingWdth by breathingTransition.animateFloat(
        initialValue = 100f,
        targetValue = 135f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_wdth"
    )

    // When scrubbing manually via the bar, breathing is paused (held at rest width 117.5f)
    val targetWdth = when {
        reduceMotion -> if (isPlaying || isRecording) 135f else if (isScrubbing || isSeeking) 117.5f else 100f
        isSeeking -> breathingWdth
        isScrubbing -> 117.5f
        isPlaying || isRecording -> 135f
        else -> 100f
    }

    val targetSlnt = if (isRecording && !reduceMotion) -10f else 0f

    // Critically damped spring physics for smooth, non-bouncy font transitions across every state
    val animWght by animateFloatAsState(
        targetValue = targetWght,
        animationSpec = if (reduceMotion) snap() else spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "anim_wght"
    )

    val animWdth by animateFloatAsState(
        targetValue = targetWdth,
        animationSpec = if (reduceMotion) snap() else spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "anim_wdth"
    )

    val animSlnt by animateFloatAsState(
        targetValue = targetSlnt,
        animationSpec = if (reduceMotion) snap() else spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "anim_slnt"
    )

    // Quantize animated axes to fine steps to enable efficient font caching without jitter
    val qWght = (animWght / 10f).roundToInt() * 10f
    val qWdth = (animWdth / 2.5f).roundToInt() * 2.5f
    val qSlnt = (animSlnt / 2.5f).roundToInt() * 2.5f

    val fontCache = remember { mutableMapOf<Triple<Float, Float, Float>, FontFamily>() }
    val currentTitleFont = if (reduceMotion) {
        remember {
            createGoogleSansFlexFamily(
                wght = 700f,
                wdth = 117.5f,
                slnt = 0f,
                rond = 100f,
                opsz = 40f,
                weight = FontWeight.Bold
            )
        }
    } else {
        remember(qWght, qWdth, qSlnt) {
            fontCache.getOrPut(Triple(qWght, qWdth, qSlnt)) {
                createGoogleSansFlexFamily(
                    wght = qWght,
                    wdth = qWdth,
                    slnt = qSlnt,
                    rond = 100f,
                    opsz = 40f,
                    weight = FontWeight.Bold
                )
            }
        }
    }

    // Smooth, non-bouncy tactile scale physics
    val fontScaleX by animateFloatAsState(
        targetValue = if (reduceMotion) 1f else if (isScrubbing || isSeeking) 1.02f else if (isPlaying) 1.0f else 0.98f,
        animationSpec = if (reduceMotion) snap() else spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "font_scale_x"
    )
    val fontScaleY by animateFloatAsState(
        targetValue = if (reduceMotion) 1f else if (isScrubbing || isSeeking) 1.02f else if (isPlaying) 1.0f else 0.98f,
        animationSpec = if (reduceMotion) snap() else spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "font_scale_y"
    )

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Playback and channel change protection guards
    val controlsEnabled = isPlaying && !isSeeking
    var pendingChannelChange by remember { mutableStateOf<(() -> Unit)?>(null) }
    val safeChangeStation: (() -> Unit) -> Unit = { action ->
        if (isRecording) {
            pendingChannelChange = action
        } else {
            action()
        }
    }

    // Orientation Check for Adaptive UI & Fullscreen Immersive Mode
    val configuration = LocalConfiguration.current
    val view = LocalView.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    DisposableEffect(isLandscape) {
        val window = (context as? Activity)?.window ?: return@DisposableEffect onDispose {}
        val controller = WindowCompat.getInsetsController(window, view)
        if (isLandscape) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .alpha(entranceAlpha)
                .graphicsLayer { translationY = entranceOffsetY.dp.toPx() }
        ) {
            AmbientArtworkGlow(
                primaryColor = glowPrimary,
                secondaryColor = glowSecondary,
                isPlaying = isPlaying,
                isLandscape = isLandscape,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.fillMaxSize()
            )
            if (isLandscape) {
                // Adaptive Landscape Layout (Symmetrical 50/50 two-pane presentation in pure fullscreen)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        ArtworkHeroCard(
                            artworkBitmap = artworkBitmap,
                            isSeeking = isSeeking,
                            isFavorite = isFavorite,
                            controlsEnabled = controlsEnabled,
                            onToggleFavoriteClick = onToggleFavoriteClick,
                            hasEntered = hasEntered,
                            modifier = Modifier
                                .fillMaxHeight(0.88f)
                                .aspectRatio(1.0f)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxHeight()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TopStatusPillGroup(
                            isSpeakerUsed = isSpeakerUsed,
                            isAudioRoutingAvailable = isAudioRoutingAvailable,
                            isRecording = isRecording,
                            recordDurationMs = recordDurationMs,
                            onToggleSpeakerClick = onToggleSpeakerClick,
                            onRecordClick = onRecordClick,
                            onOpenSettingsClick = { isSettingsVisible = true }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        StationTitleBlock(
                            currentFrequency = displayedFrequency,
                            stationName = stationName,
                            radioText = radioText,
                            isPlaying = isPlaying,
                            titleFont = currentTitleFont,
                            fontScaleX = fontScaleX,
                            fontScaleY = fontScaleY,
                            reduceMotion = reduceMotion,
                            isScrubbing = isScrubbing
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DottedTunerScrubber(
                            currentFrequency = currentFrequency,
                            isPlaying = isPlaying,
                            enabled = controlsEnabled,
                            onFrequencySeek = { freq ->
                                safeChangeStation {
                                    pendingSeekTarget = freq
                                    onFrequencySeek(freq)
                                }
                            },
                            onScrubbingStateChange = { isScrubbing = it },
                            onPreviewFrequencyChange = { scrubbingFrequency = it },
                            externalTuneRevision = externalTuneRevision,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        PlaybackControlsRow(
                            isPlaying = isPlaying,
                            isSeeking = isSeeking,
                            isCompactHeight = true,
                            onPrevStationClick = {
                                safeChangeStation {
                                    pendingSeekTarget = null
                                    externalTuneRevision++
                                    onPrevStationClick()
                                }
                            },
                            onPlayPauseClick = onPlayPauseClick,
                            onNextStationClick = {
                                safeChangeStation {
                                    pendingSeekTarget = null
                                    externalTuneRevision++
                                    onNextStationClick()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        BottomControlsBar(
                            isRecording = isRecording,
                            controlsEnabled = controlsEnabled,
                            onTuneDownClick = {
                                safeChangeStation {
                                    pendingSeekTarget = null
                                    externalTuneRevision++
                                    onTuneDownClick()
                                }
                            },
                            onTuneUpClick = {
                                safeChangeStation {
                                    pendingSeekTarget = null
                                    externalTuneRevision++
                                    onTuneUpClick()
                                }
                            },
                            onRecordClick = onRecordClick,
                            onStartRecording = onStartRecording,
                            onStopRecording = onStopRecording,
                            onOpenFavoritesClick = {
                                onReloadFavorites()
                                showBottomSheet = true
                            }
                        )
                    }
                }
            } else {
                // Portrait Layout with equal top/bottom spacing around the title & artist block
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    val maxArtworkSize = (maxHeight - 368.dp)
                        .coerceAtLeast(180.dp)
                        .coerceAtMost(maxWidth - 48.dp)

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        TopStatusPillGroup(
                            isSpeakerUsed = isSpeakerUsed,
                            isAudioRoutingAvailable = isAudioRoutingAvailable,
                            isRecording = isRecording,
                            recordDurationMs = recordDurationMs,
                            onToggleSpeakerClick = onToggleSpeakerClick,
                            onRecordClick = onRecordClick,
                            onOpenSettingsClick = { isSettingsVisible = true },
                            modifier = Modifier.padding(top = 6.dp, bottom = 10.dp)
                        )

                        ArtworkHeroCard(
                            artworkBitmap = artworkBitmap,
                            isSeeking = isSeeking,
                            isFavorite = isFavorite,
                            controlsEnabled = controlsEnabled,
                            onToggleFavoriteClick = onToggleFavoriteClick,
                            hasEntered = hasEntered,
                            modifier = Modifier.size(maxArtworkSize)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .heightIn(min = 72.dp)
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            StationTitleBlock(
                                currentFrequency = displayedFrequency,
                                stationName = stationName,
                                radioText = radioText,
                                isPlaying = isPlaying,
                                titleFont = currentTitleFont,
                                fontScaleX = fontScaleX,
                                fontScaleY = fontScaleY,
                                reduceMotion = reduceMotion,
                                isScrubbing = isScrubbing
                            )
                        }

                        DottedTunerScrubber(
                            currentFrequency = currentFrequency,
                            isPlaying = isPlaying,
                            enabled = controlsEnabled,
                            onFrequencySeek = { freq ->
                                safeChangeStation {
                                    pendingSeekTarget = freq
                                    onFrequencySeek(freq)
                                }
                            },
                            onScrubbingStateChange = { isScrubbing = it },
                            onPreviewFrequencyChange = { scrubbingFrequency = it },
                            externalTuneRevision = externalTuneRevision,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PlaybackControlsRow(
                            isPlaying = isPlaying,
                            isSeeking = isSeeking,
                            isCompactHeight = false,
                            onPrevStationClick = {
                                safeChangeStation {
                                    pendingSeekTarget = null
                                    externalTuneRevision++
                                    onPrevStationClick()
                                }
                            },
                            onPlayPauseClick = onPlayPauseClick,
                            onNextStationClick = {
                                safeChangeStation {
                                    pendingSeekTarget = null
                                    externalTuneRevision++
                                    onNextStationClick()
                                }
                            },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        BottomControlsBar(
                            isRecording = isRecording,
                            controlsEnabled = controlsEnabled,
                            onTuneDownClick = {
                                safeChangeStation {
                                    pendingSeekTarget = null
                                    externalTuneRevision++
                                    onTuneDownClick()
                                }
                            },
                            onTuneUpClick = {
                                safeChangeStation {
                                    pendingSeekTarget = null
                                    externalTuneRevision++
                                    onTuneUpClick()
                                }
                            },
                            onRecordClick = onRecordClick,
                            onStartRecording = onStartRecording,
                            onStopRecording = onStopRecording,
                            onOpenFavoritesClick = {
                                onReloadFavorites()
                                showBottomSheet = true
                            },
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }

        // Favorites Modal Bottom Sheet with Reordering, Renaming, Adding, Deleting
        if (showBottomSheet) {
            FmFavoritesSheet(
                favoritesList = favoritesList,
                currentFrequency = currentFrequency,
                sheetState = sheetState,
                onStationSelect = { freq ->
                    safeChangeStation {
                        pendingSeekTarget = null
                        externalTuneRevision++
                        onStationSelect(freq)
                    }
                },
                onDismissRequest = { showBottomSheet = false },
                onReloadFavorites = onReloadFavorites
            )
        }

        // Fullscreen Saved Recordings with In-App Player & Seekbar
        AnimatedVisibility(
            visible = isRecordingSheetVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FmRecordingsScreen(
                recordingsList = recordingsList,
                onDeleteRecording = onDeleteRecording,
                onBackClick = onDismissRecordingSheet,
                onPauseRadio = { if (isPlaying) onPlayPauseClick() }
            )
        }

        // Settings Screen with Grouped Preferences & Reduce Motion Toggle
        AnimatedVisibility(
            visible = isSettingsVisible,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            FmSettingsScreen(
                onBackClick = {
                    reduceMotion = FmPreferences.isReduceMotionEnabled(context)
                    isSettingsVisible = false
                }
            )
        }

        // Confirmation Dialog when attempting to tune away while recording
        if (pendingChannelChange != null) {
            AlertDialog(
                onDismissRequest = {
                    pendingChannelChange = null
                    pendingSeekTarget = null
                    externalTuneRevision++
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.stop_recording_dialog_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.stop_recording_dialog_message),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val target = pendingChannelChange
                            pendingChannelChange = null
                            onStopRecording()
                            target?.invoke()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text(stringResource(R.string.stop_recording_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        pendingChannelChange = null
                        pendingSeekTarget = null
                        externalTuneRevision++
                    }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                },
                shape = RoundedCornerShape(28.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        }
    }
}

/**
 * Soft blurred backdrop glow derived from the active artwork's dominant and secondary colors.
 */
@Composable
private fun AmbientArtworkGlow(
    primaryColor: Color,
    secondaryColor: Color,
    isPlaying: Boolean,
    isLandscape: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    // Cap alpha so foreground text over the surface preserves WCAG AA contrast in both themes
    val primaryAlpha = if (isDarkTheme) {
        if (isPlaying) 0.34f else 0.22f
    } else {
        if (isPlaying) 0.36f else 0.26f
    }
    val secondaryAlpha = if (isDarkTheme) {
        if (isPlaying) 0.22f else 0.14f
    } else {
        if (isPlaying) 0.28f else 0.20f
    }
    val blurRadius = if (isDarkTheme) 64.dp else 48.dp
    val primaryMidStop = if (isDarkTheme) 0.45f else 0.65f
    val primaryMidFraction = if (isDarkTheme) 0.60f else 0.58f
    val secondaryMidStop = if (isDarkTheme) 0.40f else 0.60f
    val secondaryMidFraction = if (isDarkTheme) 0.65f else 0.62f

    Canvas(
        modifier = modifier.blur(radius = blurRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
    ) {
        val centerX = if (isLandscape) size.width * 0.27f else size.width * 0.5f
        val centerY = if (isLandscape) size.height * 0.50f else size.height * 0.34f
        val primaryRadius = size.minDimension * (if (isDarkTheme) 0.56f else 0.60f)
        val secondaryRadius = size.minDimension * (if (isDarkTheme) 0.44f else 0.48f)

        drawCircle(
            brush = Brush.radialGradient(
                0.0f to primaryColor.copy(alpha = primaryAlpha),
                primaryMidFraction to primaryColor.copy(alpha = primaryAlpha * primaryMidStop),
                1.0f to Color.Transparent,
                center = Offset(centerX - size.minDimension * 0.06f, centerY - size.minDimension * 0.04f),
                radius = primaryRadius
            ),
            radius = primaryRadius,
            center = Offset(centerX - size.minDimension * 0.06f, centerY - size.minDimension * 0.04f)
        )

        drawCircle(
            brush = Brush.radialGradient(
                0.0f to secondaryColor.copy(alpha = secondaryAlpha),
                secondaryMidFraction to secondaryColor.copy(alpha = secondaryAlpha * secondaryMidStop),
                1.0f to Color.Transparent,
                center = Offset(centerX + size.minDimension * 0.10f, centerY + size.minDimension * 0.08f),
                radius = secondaryRadius
            ),
            radius = secondaryRadius,
            center = Offset(centerX + size.minDimension * 0.10f, centerY + size.minDimension * 0.08f)
        )
    }
}

/**
 * Top status pill group containing the Audio Output selector and dynamic live Recording indicator.
 */
@Composable
private fun TopStatusPillGroup(
    isSpeakerUsed: Boolean,
    isAudioRoutingAvailable: Boolean,
    isRecording: Boolean,
    recordDurationMs: Long,
    onToggleSpeakerClick: () -> Unit,
    onRecordClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Audio Routing Pill (Disabled if no headset / bluetooth device is available)
        Surface(
            onClick = onToggleSpeakerClick,
            enabled = isAudioRoutingAvailable,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                alpha = if (isAudioRoutingAvailable) 1f else 0.38f
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .alpha(if (isAudioRoutingAvailable) 1f else 0.38f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isSpeakerUsed) Icons.Filled.Speaker else Icons.Filled.Headphones,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isSpeakerUsed) stringResource(R.string.playing_on_speaker) else stringResource(R.string.playing_on_headphone),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // 2. Dynamic Live Recording Indicator Pill (Shown when recording)
        AnimatedVisibility(
            visible = isRecording,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Surface(
                onClick = onRecordClick,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "rec_dot_pulse")
                    val recDotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.35f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "rec_dot_alpha"
                    )
                    Icon(
                        imageVector = Icons.Filled.FiberManualRecord,
                        contentDescription = null,
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(recDotAlpha),
                        tint = MaterialTheme.colorScheme.error
                    )
                    val durationSec = recordDurationMs / 1000L
                    val timeStr = String.format(Locale.US, "%02d:%02d", durationSec / 60, durationSec % 60)
                    Text(
                        text = stringResource(R.string.recording_badge_format, timeStr),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // 3. Settings Action Pill
        Surface(
            onClick = onOpenSettingsClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Box(
                modifier = Modifier.padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings),
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Hero artwork card with animated entrance scale, M3 seeking loading indicator, and favorite button.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ArtworkHeroCard(
    artworkBitmap: Bitmap?,
    isSeeking: Boolean,
    isFavorite: Boolean,
    controlsEnabled: Boolean,
    onToggleFavoriteClick: () -> Unit,
    hasEntered: Boolean,
    modifier: Modifier = Modifier
) {
    val targetCardScale = if (isSeeking) {
        0.92f
    } else if (artworkBitmap != null && hasEntered) {
        1.0f
    } else {
        0.88f
    }
    val artworkEntranceScale by animateFloatAsState(
        targetValue = targetCardScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "artwork_entrance_scale"
    )

    Card(
        modifier = modifier.scale(artworkEntranceScale),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(
                targetState = artworkBitmap,
                animationSpec = tween(durationMillis = 350),
                label = "artwork_crossfade"
            ) { bitmap ->
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Expressive Material 3 LoadingIndicator overlay while searching stations
            val loadingAlpha by animateFloatAsState(
                targetValue = if (isSeeking) 1.0f else 0.0f,
                animationSpec = tween(durationMillis = 250),
                label = "seeking_loading_alpha"
            )
            if (loadingAlpha > 0.01f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.40f * loadingAlpha))
                        .alpha(loadingAlpha),
                    contentAlignment = Alignment.Center
                ) {
                    ContainedLoadingIndicator(
                        modifier = Modifier.size(64.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Floating Favorite FAB in bottom-right corner of artwork
            val favScale by animateFloatAsState(
                targetValue = if (isFavorite) 1.0f else 0.95f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "fav_scale"
            )
            FilledIconButton(
                onClick = onToggleFavoriteClick,
                enabled = controlsEnabled,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(18.dp)
                    .size(58.dp)
                    .scale(favScale),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isFavorite) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
                    contentColor = if (isFavorite) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.38f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(R.string.talk_add_favorite),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

/**
 * Centered Station Title & Subtitle with smooth slide + fade animated transition.
 * Uses hardware variable font variations for seamless weight/width breathing.
 */
@Composable
private fun StationTitleBlock(
    currentFrequency: Float,
    stationName: String,
    radioText: String,
    isPlaying: Boolean,
    titleFont: FontFamily,
    fontScaleX: Float = 1.0f,
    fontScaleY: Float = 1.0f,
    reduceMotion: Boolean = false,
    isScrubbing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val formattedFreq = String.format(Locale.US, "%.1fFM", currentFrequency)
    val appName = stringResource(R.string.app_name)
    val hasRds = stationName.isNotBlank() && stationName != appName
    val displayTitle = if (hasRds) stationName else formattedFreq
    val displaySubtitle = if (hasRds) {
        if (radioText.isNotBlank()) radioText else formattedFreq
    } else {
        if (radioText.isNotBlank()) radioText else formattedFreq
    }

    // Deduplicate subtitle if identical to station title
    val showSubtitle = displaySubtitle.isNotBlank() && !displaySubtitle.equals(displayTitle, ignoreCase = true)

    var lastFrequency by remember { mutableFloatStateOf(currentFrequency) }
    val direction = when {
        currentFrequency > lastFrequency + 0.01f -> 1
        currentFrequency < lastFrequency - 0.01f -> -1
        else -> 0
    }
    SideEffect {
        lastFrequency = currentFrequency
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title with contextual directional slide + fade transition
        AnimatedContent(
            targetState = displayTitle,
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
            transitionSpec = {
                if (reduceMotion || isScrubbing) {
                    fadeIn(animationSpec = tween(100)) togetherWith fadeOut(animationSpec = tween(100))
                } else if (direction < 0) {
                    // Previous scrubbing / lower freq: moves to left
                    (slideInHorizontally(animationSpec = tween(300)) { width -> width / 2 } + fadeIn(tween(300)))
                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> -width / 2 } + fadeOut(tween(300)))
                } else if (direction > 0) {
                    // Next scrubbing / higher freq: moves to right
                    (slideInHorizontally(animationSpec = tween(300)) { width -> -width / 2 } + fadeIn(tween(300)))
                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> width / 2 } + fadeOut(tween(300)))
                } else {
                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                }
            },
            label = "title_directional_slide"
        ) { targetTitle ->
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val density = LocalDensity.current
                val layoutDirection = LocalLayoutDirection.current
                val textMeasurer = rememberTextMeasurer()
                val titleStyle = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = titleFont,
                    fontSize = 40.sp,
                    lineHeight = 46.sp,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center
                )
                val measuredWidthPx = remember(targetTitle, titleStyle) {
                    textMeasurer.measure(
                        text = targetTitle,
                        style = titleStyle,
                        maxLines = 1,
                        softWrap = false,
                        constraints = Constraints()
                    ).size.width
                }
                val overflowTolerancePx = with(density) { 4.dp.toPx() }
                val needsMarquee = measuredWidthPx * fontScaleX > constraints.maxWidth + overflowTolerancePx

                val titleModifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .then(
                        if (needsMarquee) {
                            Modifier
                                .graphicsLayer {
                                    scaleX = fontScaleX
                                    scaleY = fontScaleY
                                    compositingStrategy = CompositingStrategy.Offscreen
                                }
                                .drawWithContent {
                                    drawContent()
                                    val fadeWidth = 24.dp.toPx()
                                    if (size.width > fadeWidth * 2) {
                                        val startStop = fadeWidth / size.width
                                        val endStop = 1f - startStop
                                        val brush = if (layoutDirection == LayoutDirection.Rtl) {
                                            Brush.horizontalGradient(
                                                0f to Color.Transparent,
                                                startStop to Color.Black,
                                                endStop to Color.Black,
                                                1f to Color.Transparent,
                                                startX = size.width,
                                                endX = 0f
                                            )
                                        } else {
                                            Brush.horizontalGradient(
                                                0f to Color.Transparent,
                                                startStop to Color.Black,
                                                endStop to Color.Black,
                                                1f to Color.Transparent
                                            )
                                        }
                                        drawRect(brush = brush, blendMode = BlendMode.DstIn)
                                    }
                                }
                                .then(if (isPlaying) Modifier.basicMarquee() else Modifier)
                        } else {
                            Modifier.graphicsLayer {
                                scaleX = fontScaleX
                                scaleY = fontScaleY
                            }
                        }
                    )

                Text(
                    text = targetTitle,
                    style = titleStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = titleModifier
                )
            }
        }

        this@Column.AnimatedVisibility(
            visible = showSubtitle,
            enter = if (reduceMotion) fadeIn(tween(200)) else fadeIn(tween(250)) + slideInVertically(animationSpec = tween(250)) { it / 2 },
            exit = if (reduceMotion) fadeOut(tween(200)) else fadeOut(tween(200)) + slideOutVertically(animationSpec = tween(200)) { -it / 2 }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Subtitle with smooth directional slide + fade transition when frequency or RDS updates
                AnimatedContent(
                    targetState = displaySubtitle,
                    transitionSpec = {
                        if (reduceMotion) {
                            fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                        } else if (direction < 0) {
                            // Previous scrubbing / lower freq: moves to left
                            (slideInHorizontally(animationSpec = tween(300)) { width -> width / 2 } + fadeIn(tween(300)))
                                .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> -width / 2 } + fadeOut(tween(300)))
                        } else if (direction > 0) {
                            // Next scrubbing / higher freq: moves to right
                            (slideInHorizontally(animationSpec = tween(300)) { width -> -width / 2 } + fadeIn(tween(300)))
                                .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> width / 2 } + fadeOut(tween(300)))
                        } else {
                            (slideInVertically(animationSpec = tween(250)) { height -> height / 2 } + fadeIn(tween(250)))
                                .togetherWith(slideOutVertically(animationSpec = tween(250)) { height -> -height / 2 } + fadeOut(tween(250)))
                        }
                    },
                    label = "subtitle_directional_slide"
                ) { targetSubtitle ->
                    Text(
                        text = targetSubtitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Expressive Playback Controls Row (Disabled on pause, morphing center squircle to stadium pill).
 */
@Composable
private fun PlaybackControlsRow(
    isPlaying: Boolean,
    isSeeking: Boolean,
    isCompactHeight: Boolean = false,
    onPrevStationClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextStationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val seekPulseScale by if (isSeeking) {
        val infiniteTransition = rememberInfiniteTransition(label = "seek_btn_pulse")
        infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "seek_btn_scale"
        )
    } else {
        remember { mutableFloatStateOf(1.0f) }
    }

    val rowHeight = if (isCompactHeight) 88.dp else 104.dp
    val centerWeight by animateFloatAsState(
        targetValue = if (isPlaying) 1.85f else 1.15f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "center_btn_weight"
    )
    val centerBtnCorner by animateDpAsState(
        targetValue = if (isPlaying) 52.dp else 32.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "center_btn_corner"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous Station (Disabled when paused)
        FilledIconButton(
            onClick = onPrevStationClick,
            enabled = isPlaying && !isSeeking,
            modifier = Modifier
                .weight(1f)
                .height(rowHeight)
                .scale(if (isSeeking) seekPulseScale else 1.0f),
            shape = RoundedCornerShape(32.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.38f),
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        ) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = stringResource(R.string.talk_prev_station),
                modifier = Modifier.size(if (isCompactHeight) 36.dp else 40.dp)
            )
        }

        // Central Primary Play / Pause (Morphs between squircle and stadium)
        FilledIconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .weight(centerWeight)
                .height(rowHeight),
            shape = RoundedCornerShape(centerBtnCorner),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) {
                    stringResource(R.string.accessibility_pause)
                } else {
                    stringResource(R.string.accessibility_play)
                },
                modifier = Modifier.size(if (isCompactHeight) 44.dp else 50.dp)
            )
        }

        // Next Station (Disabled when paused)
        FilledIconButton(
            onClick = onNextStationClick,
            enabled = isPlaying && !isSeeking,
            modifier = Modifier
                .weight(1f)
                .height(rowHeight)
                .scale(if (isSeeking) seekPulseScale else 1.0f),
            shape = RoundedCornerShape(32.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.38f),
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = stringResource(R.string.talk_next_station),
                modifier = Modifier.size(if (isCompactHeight) 36.dp else 40.dp)
            )
        }
    }
}

/**
 * Bottom Auxiliary Row: Granular Stepping & Connected Material 3 Expressive Button Group.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BottomControlsBar(
    isRecording: Boolean,
    controlsEnabled: Boolean,
    onTuneDownClick: () -> Unit,
    onTuneUpClick: () -> Unit,
    onRecordClick: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onOpenFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recordBtnEnabled = isRecording || controlsEnabled

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Granular -0.1 Step Button
        Surface(
            onClick = onTuneDownClick,
            enabled = controlsEnabled,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                alpha = if (controlsEnabled) 1f else 0.38f
            ),
            modifier = Modifier.height(48.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .alpha(if (controlsEnabled) 1f else 0.38f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "0.1",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Connected Center Button Group (Recording List, Record/Stop, Favorites)
        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TonalToggleButton(
                checked = false,
                onCheckedChange = { onRecordClick() },
                shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                colors = ToggleButtonDefaults.tonalToggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                modifier = Modifier.size(width = 50.dp, height = 48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                    contentDescription = stringResource(R.string.fm_over_record_list),
                    modifier = Modifier.size(20.dp)
                )
            }

            ToggleButton(
                checked = isRecording,
                onCheckedChange = {
                    if (isRecording) {
                        onStopRecording()
                    } else {
                        onStartRecording()
                    }
                },
                enabled = recordBtnEnabled,
                shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    checkedContainerColor = MaterialTheme.colorScheme.error,
                    checkedContentColor = MaterialTheme.colorScheme.onError,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.38f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                modifier = Modifier.size(width = 56.dp, height = 48.dp)
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.FiberManualRecord,
                    contentDescription = if (isRecording) stringResource(R.string.stop_record) else stringResource(R.string.record_title),
                    modifier = Modifier.size(if (isRecording) 20.dp else 14.dp)
                )
            }

            TonalToggleButton(
                checked = false,
                onCheckedChange = { onOpenFavoritesClick() },
                enabled = controlsEnabled,
                shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                colors = ToggleButtonDefaults.tonalToggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.38f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                modifier = Modifier.size(width = 50.dp, height = 48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ListAlt,
                    contentDescription = stringResource(R.string.favorites),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Granular +0.1 Step Button
        Surface(
            onClick = onTuneUpClick,
            enabled = controlsEnabled,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                alpha = if (controlsEnabled) 1f else 0.38f
            ),
            modifier = Modifier.height(48.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .alpha(if (controlsEnabled) 1f else 0.38f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "0.1",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
