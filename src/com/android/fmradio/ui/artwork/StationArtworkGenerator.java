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

package com.android.fmradio.ui.artwork;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.LruCache;

import java.util.Objects;
import java.util.Random;

/**
 * Generates frequency-seeded Material 3 artwork using the active device
 * Material You color scheme and exposes extracted roles for UI ambient lighting.
 */
public final class StationArtworkGenerator {
    private static final int ARTWORK_SIZE = 1024;
    private static final LruCache<String, StationArtworkResult> sArtworkCache = new LruCache<>(16);

    /**
     * Generated artwork bitmap paired with its dominant palette colors.
     */
    public static final class StationArtworkResult {
        public final Bitmap bitmap;
        public final int dominantColor;
        public final int secondaryColor;
        public final int accentColor;

        public StationArtworkResult(Bitmap bitmap, int dominantColor, int secondaryColor, int accentColor) {
            this.bitmap = bitmap;
            this.dominantColor = dominantColor;
            this.secondaryColor = secondaryColor;
            this.accentColor = accentColor;
        }
    }

    private StationArtworkGenerator() {
    }

    /**
     * Retrieve or generate Material 3 artwork using the live Compose ColorScheme roles.
     */
    public static synchronized StationArtworkResult getStationArtworkResult(
            Context context,
            int frequency,
            String stationName,
            int primary,
            int secondary,
            int tertiary,
            int primaryContainer,
            int secondaryContainer,
            int tertiaryContainer,
            int surfaceBase,
            boolean isDarkTheme) {
        int themeHash = Objects.hash(
                primary, secondary, tertiary,
                primaryContainer, secondaryContainer, tertiaryContainer,
                surfaceBase, isDarkTheme
        );
        String cacheKey = frequency + "_" + themeHash;
        StationArtworkResult cached = sArtworkCache.get(cacheKey);
        if (cached != null && cached.bitmap != null && !cached.bitmap.isRecycled()) {
            return cached;
        }

        Palette palette = buildM3Palette(
                frequency,
                primary, secondary, tertiary,
                primaryContainer, secondaryContainer, tertiaryContainer,
                surfaceBase, isDarkTheme
        );
        StationArtworkResult result = generateArtworkResult(frequency, palette);
        sArtworkCache.put(cacheKey, result);
        return result;
    }

    /**
     * Retrieve or generate artwork using the system Material You dynamic palette tokens.
     *
     * @param context Application or UI context
     * @param frequency Station frequency in tenths (e.g. 875 for 87.5 MHz)
     * @param stationName Optional station name/callsign
     * @return Cached or newly generated StationArtworkResult
     */
    public static synchronized StationArtworkResult getStationArtworkResult(
            Context context, int frequency, String stationName) {
        boolean isDarkTheme = (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        int primary = getSystemColor(context,
                isDarkTheme ? android.R.color.system_accent1_200 : android.R.color.system_accent1_600,
                isDarkTheme ? 0xFFD0BCFF : 0xFF6750A4);
        int secondary = getSystemColor(context,
                isDarkTheme ? android.R.color.system_accent2_200 : android.R.color.system_accent2_600,
                isDarkTheme ? 0xFFCCC2DC : 0xFF625B71);
        int tertiary = getSystemColor(context,
                isDarkTheme ? android.R.color.system_accent3_200 : android.R.color.system_accent3_600,
                isDarkTheme ? 0xFFEFB8C8 : 0xFF7D5260);
        int primaryContainer = getSystemColor(context,
                isDarkTheme ? android.R.color.system_accent1_700 : android.R.color.system_accent1_100,
                isDarkTheme ? 0xFF4F378B : 0xFFEADDFF);
        int secondaryContainer = getSystemColor(context,
                isDarkTheme ? android.R.color.system_accent2_700 : android.R.color.system_accent2_100,
                isDarkTheme ? 0xFF4A4458 : 0xFFE8DEF8);
        int tertiaryContainer = getSystemColor(context,
                isDarkTheme ? android.R.color.system_accent3_700 : android.R.color.system_accent3_100,
                isDarkTheme ? 0xFF633B48 : 0xFFFFD8E4);
        int surfaceBase = getSystemColor(context,
                isDarkTheme ? android.R.color.system_neutral1_900 : android.R.color.system_neutral1_50,
                isDarkTheme ? 0xFF1C1B1F : 0xFFF7F2FA);

        return getStationArtworkResult(
                context, frequency, stationName,
                primary, secondary, tertiary,
                primaryContainer, secondaryContainer, tertiaryContainer,
                surfaceBase, isDarkTheme
        );
    }

    /**
     * Retrieve or generate an HD artwork bitmap for a specific radio frequency.
     *
     * @param context Application or UI context
     * @param frequency Station frequency in tenths (e.g. 875 for 87.5 MHz)
     * @param stationName Optional station name/callsign
     * @return 1024x1024 high-definition Bitmap
     */
    public static synchronized Bitmap getStationArtwork(Context context, int frequency, String stationName) {
        return getStationArtworkResult(context, frequency, stationName).bitmap;
    }

    private static int getSystemColor(Context context, int resId, int fallback) {
        try {
            return context.getColor(resId);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static StationArtworkResult generateArtworkResult(int frequency, Palette palette) {
        Bitmap bitmap = Bitmap.createBitmap(ARTWORK_SIZE, ARTWORK_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        long seed = (long) frequency * 31337L + 17L;
        Random rng = new Random(seed);

        int archetype = Math.abs((frequency * 7) / 3) % 8;

        switch (archetype) {
            case 0:
                renderMaterial3Shapes(canvas, palette, frequency, rng);
                break;
            case 1:
                renderAtmosphericHorizon(canvas, palette, frequency, rng);
                break;
            case 2:
                renderGeometricWaveforms(canvas, palette, frequency, rng);
                break;
            case 3:
                renderRetroRadioDial(canvas, palette, frequency, rng);
                break;
            case 4:
                renderModernistAbstract(canvas, palette, frequency, rng);
                break;
            case 5:
                renderStarMedallion(canvas, palette, frequency, rng);
                break;
            case 6:
                renderPrismBloom(canvas, palette, frequency, rng);
                break;
            default:
                renderDuotoneRipples(canvas, palette, frequency, rng);
                break;
        }

        return new StationArtworkResult(
                bitmap,
                palette.accentPrimary,
                palette.accentSecondary,
                palette.accentTertiary
        );
    }

    private static void renderAtmosphericHorizon(Canvas canvas, Palette palette, int freq, Random rng) {
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Shader skyShader = new LinearGradient(
                0, 0, ARTWORK_SIZE * 0.25f, ARTWORK_SIZE,
                new int[]{palette.accentPrimary, palette.accentSecondary, palette.surfaceBase},
                new float[]{0.0f, 0.58f, 1.0f},
                Shader.TileMode.CLAMP
        );
        bgPaint.setShader(skyShader);
        canvas.drawRect(0, 0, ARTWORK_SIZE, ARTWORK_SIZE, bgPaint);

        Paint sunPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float sunCx = ARTWORK_SIZE * (0.44f + rng.nextFloat() * 0.12f);
        float sunCy = ARTWORK_SIZE * 0.42f;
        float sunRadius = ARTWORK_SIZE * 0.21f;
        sunPaint.setShader(new RadialGradient(
                sunCx, sunCy, sunRadius * 1.8f,
                new int[]{adjustAlpha(palette.accentTertiary, 0.85f), palette.glowColor, Color.TRANSPARENT},
                new float[]{0.15f, 0.55f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(sunCx, sunCy, sunRadius * 1.8f, sunPaint);

        sunPaint.setShader(new LinearGradient(
                sunCx, sunCy - sunRadius, sunCx, sunCy + sunRadius,
                palette.accentTertiary, palette.accentPrimary,
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(sunCx, sunCy, sunRadius, sunPaint);

        Paint ridgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ridgePaint.setStyle(Paint.Style.FILL);

        int[] ridgeColors = new int[]{
                adjustAlpha(palette.accentSecondary, 0.48f),
                adjustAlpha(lerpColor(palette.accentPrimary, palette.surfaceBase, 0.55f), 0.86f),
                palette.surfaceBase
        };

        for (int r = 0; r < ridgeColors.length; r++) {
            float baseY = ARTWORK_SIZE * (0.58f + r * 0.13f);
            Path path = new Path();
            path.moveTo(0, ARTWORK_SIZE);
            path.lineTo(0, baseY);

            float waveFreq = 0.0045f + (r * 0.0022f);
            float amplitude = 42f - (r * 7f);
            for (float x = 0; x <= ARTWORK_SIZE; x += 16) {
                float y = baseY + (float) Math.sin((x + freq * 12f + r * 90f) * waveFreq) * amplitude;
                path.lineTo(x, y);
            }
            path.lineTo(ARTWORK_SIZE, ARTWORK_SIZE);
            path.close();

            ridgePaint.setColor(ridgeColors[r]);
            canvas.drawPath(path, ridgePaint);
        }
    }

    private static void renderGeometricWaveforms(Canvas canvas, Palette palette, int freq, Random rng) {
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setShader(new LinearGradient(
                0, 0, ARTWORK_SIZE, ARTWORK_SIZE,
                palette.surfaceBase, darkenColor(palette.accentPrimary, 0.28f),
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, ARTWORK_SIZE, ARTWORK_SIZE, bgPaint);

        Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setShader(new RadialGradient(
                ARTWORK_SIZE * 0.5f, ARTWORK_SIZE * 0.5f, ARTWORK_SIZE * 0.55f,
                new int[]{adjustAlpha(palette.accentSecondary, 0.35f), Color.TRANSPARENT},
                new float[]{0.0f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(ARTWORK_SIZE * 0.5f, ARTWORK_SIZE * 0.5f, ARTWORK_SIZE * 0.55f, glowPaint);

        Paint wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setStyle(Paint.Style.STROKE);
        wavePaint.setStrokeCap(Paint.Cap.ROUND);

        int waveCount = 16;
        float centerY = ARTWORK_SIZE * 0.5f;
        for (int i = 0; i < waveCount; i++) {
            float progress = (float) i / (waveCount - 1);
            wavePaint.setStrokeWidth(8f + (float) Math.sin(progress * Math.PI) * 18f);
            int strokeColor = progress < 0.5f
                    ? lerpColor(palette.accentPrimary, palette.accentSecondary, progress * 2f)
                    : lerpColor(palette.accentSecondary, palette.accentTertiary, (progress - 0.5f) * 2f);
            wavePaint.setColor(strokeColor);
            wavePaint.setAlpha((int) (135 + Math.sin(progress * Math.PI) * 120));

            Path wave = new Path();
            float startX = -24f;
            float endX = ARTWORK_SIZE + 24f;
            wave.moveTo(startX, centerY);

            float amp = (float) Math.sin(progress * Math.PI) * (ARTWORK_SIZE * 0.28f);
            float freqFactor = 2.0f + (freq % 4);
            for (float x = startX; x <= endX; x += 16) {
                float normX = (x - startX) / (endX - startX);
                float y = centerY + (float) Math.sin(normX * Math.PI * freqFactor + (i * 0.38f)) * amp;
                wave.lineTo(x, y);
            }
            canvas.drawPath(wave, wavePaint);
        }
    }

    private static void renderRetroRadioDial(Canvas canvas, Palette palette, int freq, Random rng) {
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setShader(new RadialGradient(
                ARTWORK_SIZE * 0.5f, ARTWORK_SIZE * 0.5f, ARTWORK_SIZE * 0.72f,
                lerpColor(palette.surfaceBase, palette.accentSecondary, 0.22f),
                darkenColor(palette.surfaceBase, 0.45f),
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, ARTWORK_SIZE, ARTWORK_SIZE, bgPaint);

        float cx = ARTWORK_SIZE * 0.5f;
        float cy = ARTWORK_SIZE * 0.5f;

        Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setShader(new RadialGradient(
                cx, cy, 440f,
                new int[]{adjustAlpha(palette.accentPrimary, 0.36f), Color.TRANSPARENT},
                new float[]{0.2f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(cx, cy, 440f, glowPaint);

        Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);

        for (float r = 136; r <= 440; r += 34) {
            ringPaint.setStrokeWidth(4f);
            ringPaint.setColor(adjustAlpha(palette.accentSecondary, 0.28f));
            canvas.drawCircle(cx, cy, r, ringPaint);
        }

        ringPaint.setStrokeWidth(26f);
        ringPaint.setColor(palette.accentPrimary);
        RectF arcBounds = new RectF(cx - 320, cy - 320, cx + 320, cy + 320);
        float startAngle = 135f;
        float sweepAngle = 60f + 210f * ((freq % 205) / 205.0f);
        canvas.drawArc(arcBounds, startAngle, sweepAngle, false, ringPaint);

        ringPaint.setStrokeWidth(14f);
        ringPaint.setColor(palette.accentTertiary);
        RectF innerArcBounds = new RectF(cx - 220, cy - 220, cx + 220, cy + 220);
        canvas.drawArc(innerArcBounds, -45f, 180f, false, ringPaint);

        Paint hubPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hubPaint.setStyle(Paint.Style.FILL);
        hubPaint.setColor(palette.accentSecondary);
        Path hub = createHarmonicShapePath(cx, cy, 118f, 0.16f, 8);
        canvas.drawPath(hub, hubPaint);

        hubPaint.setColor(palette.accentTertiary);
        canvas.drawCircle(cx, cy, 48f, hubPaint);
    }

    private static void renderModernistAbstract(Canvas canvas, Palette palette, int freq, Random rng) {
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setShader(new LinearGradient(
                0, 0, ARTWORK_SIZE, ARTWORK_SIZE,
                palette.surfaceBase, lerpColor(palette.surfaceBase, palette.accentSecondary, 0.26f),
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, ARTWORK_SIZE, ARTWORK_SIZE, bgPaint);

        Paint shapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shapePaint.setStyle(Paint.Style.FILL);

        shapePaint.setShader(new LinearGradient(
                120f, 140f, 680f, 760f,
                palette.accentPrimary, palette.accentSecondary,
                Shader.TileMode.CLAMP
        ));
        Path diamond = createPuffyDiamondPath(ARTWORK_SIZE * 0.38f, ARTWORK_SIZE * 0.45f, 330f);
        canvas.drawPath(diamond, shapePaint);

        shapePaint.setShader(null);
        shapePaint.setColor(adjustAlpha(palette.accentSecondary, 0.85f));
        Path arch = createArchPath(ARTWORK_SIZE * 0.63f, ARTWORK_SIZE * 0.60f, 340f, 480f);
        canvas.drawPath(arch, shapePaint);

        shapePaint.setColor(palette.accentTertiary);
        Path flower = createHarmonicShapePath(ARTWORK_SIZE * 0.72f, ARTWORK_SIZE * 0.27f, 118f, 0.24f, 6);
        canvas.drawPath(flower, shapePaint);

        shapePaint.setColor(adjustAlpha(palette.surfaceBase, 0.85f));
        canvas.drawCircle(ARTWORK_SIZE * 0.72f, ARTWORK_SIZE * 0.27f, 34f, shapePaint);
    }

    private static void renderMaterial3Shapes(Canvas canvas, Palette palette, int freq, Random rng) {
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setShader(new RadialGradient(
                ARTWORK_SIZE * 0.5f, ARTWORK_SIZE * 0.5f, ARTWORK_SIZE * 0.75f,
                lerpColor(palette.surfaceBase, palette.accentPrimary, 0.22f),
                darkenColor(palette.surfaceBase, 0.55f),
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, ARTWORK_SIZE, ARTWORK_SIZE, bgPaint);

        Paint shapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shapePaint.setStyle(Paint.Style.FILL);

        float cx = ARTWORK_SIZE * 0.5f;
        float cy = ARTWORK_SIZE * 0.5f;
        int shapeStyle = Math.abs(freq % 6);

        Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setShader(new RadialGradient(
                cx, cy, 450f,
                new int[]{adjustAlpha(palette.accentPrimary, 0.45f), Color.TRANSPARENT},
                new float[]{0.2f, 1.0f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(cx, cy, 450f, glowPaint);

        shapePaint.setColor(adjustAlpha(palette.accentSecondary, 0.55f));
        Path backingShape = (shapeStyle % 2 == 0)
                ? createHarmonicShapePath(cx, cy, 345f, 0.16f, 8)
                : createBurstPath(cx, cy, 375f, 270f, 12);
        canvas.drawPath(backingShape, shapePaint);

        shapePaint.setShader(new LinearGradient(
                cx - 280f, cy - 280f, cx + 280f, cy + 280f,
                palette.accentPrimary, palette.accentSecondary,
                Shader.TileMode.CLAMP
        ));
        Path heroShape;
        switch (shapeStyle) {
            case 0:
                heroShape = createHarmonicShapePath(cx, cy, 275f, 0.32f, 4);
                break;
            case 1:
                heroShape = createHarmonicShapePath(cx, cy, 285f, 0.18f, 6);
                break;
            case 2:
                heroShape = createPuffyDiamondPath(cx, cy, 285f);
                break;
            case 3:
                heroShape = createHarmonicShapePath(cx, cy, 275f, 0.28f, 8);
                break;
            case 4:
                heroShape = createGemPath(cx, cy, 275f);
                break;
            default:
                heroShape = createHeartPath(cx, cy - 30f, 385f);
                break;
        }
        canvas.drawPath(heroShape, shapePaint);

        shapePaint.setShader(null);
        shapePaint.setColor(palette.accentTertiary);
        float coreCy = (shapeStyle == 5) ? cy - 30f : cy;
        Path coreShape = createHarmonicShapePath(cx, coreCy, 114f, shapeStyle == 5 ? 0.16f : 0.25f, shapeStyle == 5 ? 6 : 4);
        canvas.drawPath(coreShape, shapePaint);

        shapePaint.setColor(Color.WHITE);
        shapePaint.setAlpha(215);
        canvas.drawCircle(cx, coreCy, 24f, shapePaint);
    }

    private static void renderStarMedallion(Canvas canvas, Palette palette, int freq, Random rng) {
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setShader(new LinearGradient(
                0, 0, 0, ARTWORK_SIZE,
                lerpColor(palette.surfaceBase, palette.accentSecondary, 0.24f),
                palette.surfaceBase,
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, ARTWORK_SIZE, ARTWORK_SIZE, bgPaint);

        float cx = ARTWORK_SIZE * 0.5f;
        float cy = ARTWORK_SIZE * 0.5f;

        Paint shapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shapePaint.setStyle(Paint.Style.FILL);
        shapePaint.setShader(new LinearGradient(
                cx - 360f, cy - 360f, cx + 360f, cy + 360f,
                palette.accentPrimary, palette.accentTertiary,
                Shader.TileMode.CLAMP
        ));
        Path outerBurst = createBurstPath(cx, cy, 380f, 295f, 10);
        canvas.drawPath(outerBurst, shapePaint);

        shapePaint.setShader(null);
        shapePaint.setColor(palette.accentSecondary);
        Path midCookie = createHarmonicShapePath(cx, cy, 215f, 0.14f, 5);
        canvas.drawPath(midCookie, shapePaint);

        shapePaint.setColor(palette.surfaceBase);
        canvas.drawCircle(cx, cy, 92f, shapePaint);

        shapePaint.setColor(palette.accentTertiary);
        canvas.drawCircle(cx, cy, 42f, shapePaint);

        int orbitDots = 6;
        for (int i = 0; i < orbitDots; i++) {
            double angle = (2.0 * Math.PI * i) / orbitDots + (freq % 10) * 0.2;
            float dx = cx + 415f * (float) Math.cos(angle);
            float dy = cy + 415f * (float) Math.sin(angle);
            shapePaint.setColor(adjustAlpha(i % 2 == 0 ? palette.accentTertiary : palette.accentSecondary, 0.78f));
            canvas.drawCircle(dx, dy, 18f, shapePaint);
        }
    }

    private static void renderPrismBloom(Canvas canvas, Palette palette, int freq, Random rng) {
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setShader(new RadialGradient(
                ARTWORK_SIZE * 0.3f, ARTWORK_SIZE * 0.25f, ARTWORK_SIZE * 0.9f,
                lerpColor(palette.surfaceBase, palette.accentPrimary, 0.32f),
                darkenColor(palette.surfaceBase, 0.5f),
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, ARTWORK_SIZE, ARTWORK_SIZE, bgPaint);

        float cx = ARTWORK_SIZE * 0.5f;
        float cy = ARTWORK_SIZE * 0.5f;
        Paint shardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shardPaint.setStyle(Paint.Style.FILL);

        int petals = 6;
        for (int i = 0; i < petals; i++) {
            canvas.save();
            canvas.rotate((360f / petals) * i + (freq % 30), cx, cy);
            shardPaint.setColor(adjustAlpha(
                    i % 2 == 0 ? palette.accentPrimary : palette.accentSecondary,
                    0.58f
            ));
            RectF oval = new RectF(cx - 120f, cy - 390f, cx + 120f, cy + 40f);
            canvas.drawRoundRect(oval, 120f, 120f, shardPaint);
            canvas.restore();
        }

        shardPaint.setColor(palette.accentTertiary);
        Path centerGem = createGemPath(cx, cy, 130f);
        canvas.drawPath(centerGem, shardPaint);

        shardPaint.setColor(palette.surfaceBase);
        canvas.drawCircle(cx, cy, 38f, shardPaint);
    }

    private static void renderDuotoneRipples(Canvas canvas, Palette palette, int freq, Random rng) {
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(palette.surfaceBase);
        canvas.drawRect(0, 0, ARTWORK_SIZE, ARTWORK_SIZE, bgPaint);

        Path diagonalSplit = new Path();
        diagonalSplit.moveTo(0, ARTWORK_SIZE * 0.62f);
        diagonalSplit.lineTo(ARTWORK_SIZE, ARTWORK_SIZE * 0.28f);
        diagonalSplit.lineTo(ARTWORK_SIZE, ARTWORK_SIZE);
        diagonalSplit.lineTo(0, ARTWORK_SIZE);
        diagonalSplit.close();

        Paint splitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        splitPaint.setShader(new LinearGradient(
                0, ARTWORK_SIZE * 0.28f, ARTWORK_SIZE, ARTWORK_SIZE,
                adjustAlpha(palette.accentPrimary, 0.85f),
                adjustAlpha(palette.accentSecondary, 0.72f),
                Shader.TileMode.CLAMP
        ));
        canvas.drawPath(diagonalSplit, splitPaint);

        float cx = ARTWORK_SIZE * 0.5f;
        float cy = ARTWORK_SIZE * 0.48f;
        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < 4; i++) {
            float r = 175f + i * 74f;
            strokePaint.setStrokeWidth(16f - i * 2f);
            strokePaint.setColor(adjustAlpha(i % 2 == 0 ? palette.accentTertiary : palette.accentSecondary, 0.82f - i * 0.14f));
            Path ring = createHarmonicShapePath(cx, cy, r, 0.10f, 6);
            canvas.drawPath(ring, strokePaint);
        }

        Paint corePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        corePaint.setStyle(Paint.Style.FILL);
        corePaint.setColor(palette.accentTertiary);
        canvas.drawPath(createPuffyDiamondPath(cx, cy, 118f), corePaint);
    }

    private static Path createHarmonicShapePath(float cx, float cy, float baseR, float modAmp, int lobes) {
        Path path = new Path();
        int steps = 180;
        for (int i = 0; i <= steps; i++) {
            double angle = (2.0 * Math.PI * i) / steps;
            float r = baseR * (1.0f + modAmp * (float) Math.cos(lobes * angle));
            float x = cx + r * (float) Math.cos(angle);
            float y = cy + r * (float) Math.sin(angle);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.close();
        return path;
    }

    private static Path createBurstPath(float cx, float cy, float outerR, float innerR, int points) {
        Path path = new Path();
        int totalVertices = points * 2;
        for (int i = 0; i <= totalVertices; i++) {
            double angle = (2.0 * Math.PI * i) / totalVertices - Math.PI / 2.0;
            float r = (i % 2 == 0) ? outerR : innerR;
            float x = cx + r * (float) Math.cos(angle);
            float y = cy + r * (float) Math.sin(angle);
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        path.close();
        return path;
    }

    private static Path createPuffyDiamondPath(float cx, float cy, float r) {
        Path path = new Path();
        float topX = cx, topY = cy - r;
        float rightX = cx + r, rightY = cy;
        float bottomX = cx, bottomY = cy + r;
        float leftX = cx - r, leftY = cy;
        float ctrl = r * 0.45f;
        path.moveTo(topX, topY);
        path.quadTo(cx + ctrl, cy - ctrl, rightX, rightY);
        path.quadTo(cx + ctrl, cy + ctrl, bottomX, bottomY);
        path.quadTo(cx - ctrl, cy + ctrl, leftX, leftY);
        path.quadTo(cx - ctrl, cy - ctrl, topX, topY);
        path.close();
        return path;
    }

    private static Path createArchPath(float cx, float cy, float width, float height) {
        Path path = new Path();
        float left = cx - width / 2f;
        float right = cx + width / 2f;
        float bottom = cy + height / 2f;
        float top = cy - height / 2f;
        RectF dome = new RectF(left, top, right, top + width);
        path.moveTo(left, bottom);
        path.lineTo(left, top + width / 2f);
        path.arcTo(dome, 180f, 180f);
        path.lineTo(right, bottom);
        path.close();
        return path;
    }

    private static Path createGemPath(float cx, float cy, float r) {
        Path path = new Path();
        int sides = 6;
        for (int i = 0; i <= sides; i++) {
            double angle = (2.0 * Math.PI * i) / sides - Math.PI / 2.0;
            float x = cx + r * (float) Math.cos(angle);
            float y = cy + r * (float) Math.sin(angle);
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        path.close();
        return path;
    }

    private static Path createHeartPath(float cx, float cy, float size) {
        Path path = new Path();
        float s = size * 0.5f;
        path.moveTo(cx, cy + s * 0.75f);
        path.cubicTo(cx - s * 1.25f, cy, cx - s * 1.25f, cy - s * 0.8f, cx, cy - s * 0.25f);
        path.cubicTo(cx + s * 1.25f, cy - s * 0.8f, cx + s * 1.25f, cy, cx, cy + s * 0.75f);
        path.close();
        return path;
    }

    private static Palette buildM3Palette(
            int frequency,
            int primary,
            int secondary,
            int tertiary,
            int primaryContainer,
            int secondaryContainer,
            int tertiaryContainer,
            int surfaceBase,
            boolean isDarkTheme) {
        // Select deterministically among curated permutations of the current device Material 3 roles
        int variant = Math.abs(frequency * 7) % 6;
        int accent1;
        int accent2;
        int accent3;
        int containerRole;

        switch (variant) {
            case 0:
                accent1 = primary;
                accent2 = tertiary;
                accent3 = secondary;
                containerRole = primaryContainer;
                break;
            case 1:
                accent1 = tertiary;
                accent2 = primary;
                accent3 = secondaryContainer;
                containerRole = tertiaryContainer;
                break;
            case 2:
                accent1 = primary;
                accent2 = secondary;
                accent3 = tertiaryContainer;
                containerRole = secondaryContainer;
                break;
            case 3:
                accent1 = secondary;
                accent2 = primary;
                accent3 = tertiary;
                containerRole = primaryContainer;
                break;
            case 4:
                accent1 = tertiary;
                accent2 = secondary;
                accent3 = primary;
                containerRole = tertiaryContainer;
                break;
            default:
                accent1 = primary;
                accent2 = secondaryContainer;
                accent3 = tertiary;
                containerRole = primaryContainer;
                break;
        }

        int artworkSurface = isDarkTheme
                ? lerpColor(surfaceBase, containerRole, 0.45f)
                : lerpColor(containerRole, surfaceBase, 0.22f);
        int glow = adjustAlpha(accent1, isDarkTheme ? 0.45f : 0.55f);
        return new Palette(accent1, accent2, accent3, artworkSurface, glow);
    }

    private static int darkenColor(int color, float factor) {
        return Color.argb(
                Color.alpha(color),
                (int) (Color.red(color) * factor),
                (int) (Color.green(color) * factor),
                (int) (Color.blue(color) * factor)
        );
    }

    private static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        return Color.argb(Math.min(255, Math.max(0, alpha)), Color.red(color), Color.green(color), Color.blue(color));
    }

    private static int lerpColor(int from, int to, float t) {
        float inv = 1.0f - t;
        int a = (int) (Color.alpha(from) * inv + Color.alpha(to) * t);
        int r = (int) (Color.red(from) * inv + Color.red(to) * t);
        int g = (int) (Color.green(from) * inv + Color.green(to) * t);
        int b = (int) (Color.blue(from) * inv + Color.blue(to) * t);
        return Color.argb(a, r, g, b);
    }

    private static class Palette {
        final int accentPrimary;
        final int accentSecondary;
        final int accentTertiary;
        final int surfaceBase;
        final int glowColor;

        Palette(int p, int s, int t, int surf, int glow) {
            this.accentPrimary = p;
            this.accentSecondary = s;
            this.accentTertiary = t;
            this.surfaceBase = surf;
            this.glowColor = glow;
        }
    }
}
