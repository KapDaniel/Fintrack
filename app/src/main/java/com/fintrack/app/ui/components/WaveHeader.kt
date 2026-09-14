package com.fintrack.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.fintrack.app.ui.theme.FinTheme
import com.fintrack.app.ui.theme.Spacing

/**
 * Оранжевая шапка с волнами.
 *
 * Волны нарисованы на [Canvas] теми же кривыми Безье, что и в макете
 * (viewBox 400×220, три наложенных полупрозрачных слоя). Shape здесь не
 * подошёл бы: он задаёт один силуэт, а нужно три слоя поверх градиента.
 */
@Composable
fun WaveHeader(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = FinTheme.colors
    Box(modifier.fillMaxWidth()) {
        Canvas(Modifier.matchParentSize()) {
            // linear-gradient(160deg, orange500 0%, orange600 70%)
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(colors.headerStart, colors.headerEnd),
                    start = Offset(size.width, 0f),
                    end = Offset(size.width * 0.3f, size.height),
                )
            )
            drawWaves()
        }
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    start = Spacing.screenPadding,
                    end = Spacing.screenPadding,
                    top = Spacing.x4,
                    bottom = Spacing.x5,
                ),
            content = content,
        )
    }
}

/** Координаты взяты из SVG макета и масштабируются под фактический размер. */
private fun DrawScope.drawWaves() {
    val sx = size.width / VIEWBOX_WIDTH
    val sy = size.height / VIEWBOX_HEIGHT

    drawPath(wavePath(WAVE_TOP, sx, sy), Color.White.copy(alpha = 0.10f))
    drawPath(wavePath(WAVE_MIDDLE, sx, sy), Color.White.copy(alpha = 0.14f))
    drawPath(wavePath(WAVE_BOTTOM, sx, sy), Color.Black.copy(alpha = 0.07f))
}

private const val VIEWBOX_WIDTH = 400f
private const val VIEWBOX_HEIGHT = 220f

/**
 * Одна волна: стартовая высота слева и две кубические кривые.
 * Порядок чисел совпадает с атрибутом `d` исходного SVG.
 */
private data class Wave(
    val startY: Float,
    val c1: FloatArray,
    val c2: FloatArray,
)

private val WAVE_TOP = Wave(
    startY = 120f,
    c1 = floatArrayOf(60f, 160f, 140f, 80f, 220f, 110f),
    c2 = floatArrayOf(300f, 140f, 340f, 90f, 400f, 115f),
)

private val WAVE_MIDDLE = Wave(
    startY = 150f,
    c1 = floatArrayOf(80f, 112f, 160f, 190f, 260f, 150f),
    c2 = floatArrayOf(330f, 122f, 360f, 160f, 400f, 140f),
)

private val WAVE_BOTTOM = Wave(
    startY = 172f,
    c1 = floatArrayOf(90f, 200f, 200f, 140f, 300f, 175f),
    c2 = floatArrayOf(340f, 188f, 370f, 165f, 400f, 178f),
)

private fun wavePath(wave: Wave, sx: Float, sy: Float): Path = Path().apply {
    moveTo(0f, wave.startY * sy)
    cubicTo(
        wave.c1[0] * sx, wave.c1[1] * sy,
        wave.c1[2] * sx, wave.c1[3] * sy,
        wave.c1[4] * sx, wave.c1[5] * sy,
    )
    cubicTo(
        wave.c2[0] * sx, wave.c2[1] * sy,
        wave.c2[2] * sx, wave.c2[3] * sy,
        wave.c2[4] * sx, wave.c2[5] * sy,
    )
    lineTo(VIEWBOX_WIDTH * sx, VIEWBOX_HEIGHT * sy)
    lineTo(0f, VIEWBOX_HEIGHT * sy)
    close()
}
