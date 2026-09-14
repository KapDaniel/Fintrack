package com.fintrack.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Радиусы макета: 10 / 16 / 24 / 32 / pill.
 */
val FinShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

val PillShape = RoundedCornerShape(percent = 50)

/** Верхние углы белого «листа» контента. */
val SheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
