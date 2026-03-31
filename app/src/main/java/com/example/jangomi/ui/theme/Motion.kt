package com.example.jangomi.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween

object Motion {
    val EmphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val StandardEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val DurationShort = 150
    val DurationMedium = 250
    val DurationLong = 350

    fun <T> emphasizedTween() = tween<T>(DurationMedium, easing = EmphasizedEasing)
    fun <T> standardTween() = tween<T>(DurationMedium, easing = StandardEasing)
}
