package com.example.test

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val plane_contrails: ImageVector
  get() {
    if (_plane_contrails != null) {
      return _plane_contrails!!
    }
    _plane_contrails =
      ImageVector.Builder(
          name = "plane_contrails",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(9.43f, 22f)
            lineTo(8f, 20.58f)
            lineTo(12.6f, 16f)
            lineTo(14f, 17.4f)
            lineTo(9.43f, 22f)
            close()
            moveToRelative(4.97f, 0f)
            lineTo(13f, 20.58f)
            lineTo(17.08f, 16.5f)
            lineToRelative(1.43f, 1.4f)
            lineTo(14.4f, 22f)
            close()
            moveTo(3.43f, 11f)
            lineTo(2f, 9.6f)
            lineTo(6.1f, 5.5f)
            lineTo(7.5f, 6.93f)
            lineTo(3.43f, 11f)
            close()
            moveToRelative(0f, 5f)
            lineTo(2f, 14.58f)
            lineTo(6.6f, 10f)
            lineTo(8f, 11.4f)
            lineTo(3.43f, 16f)
            close()
            moveTo(19f, 15.48f)
            lineTo(16.6f, 9.5f)
            lineToRelative(-1.95f, 1.95f)
            lineToRelative(0.47f, 2.35f)
            lineTo(13.95f, 15f)
            lineTo(12.18f, 11.8f)
            lineTo(9f, 10.05f)
            lineToRelative(1.18f, -1.2f)
            lineToRelative(2.35f, 0.47f)
            lineTo(14.48f, 7.38f)
            lineTo(8.5f, 5f)
            lineTo(10f, 3.57f)
            lineTo(17.18f, 4.7f)
            lineTo(19.45f, 2.45f)
            quadToRelative(0.22f, -0.23f, 0.5f, -0.34f)
            reflectiveQuadTo(20.5f, 2f)
            reflectiveQuadToRelative(0.55f, 0.11f)
            reflectiveQuadToRelative(0.5f, 0.34f)
            quadToRelative(0.23f, 0.2f, 0.34f, 0.47f)
            reflectiveQuadTo(22f, 3.47f)
            reflectiveQuadTo(21.89f, 4.04f)
            reflectiveQuadTo(21.55f, 4.55f)
            lineTo(19.28f, 6.8f)
            lineToRelative(1.13f, 7.17f)
            lineTo(19f, 15.48f)
            close()
          }
        }
        .build()
    return _plane_contrails!!
  }

private var _plane_contrails: ImageVector? = null
