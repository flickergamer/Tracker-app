package com.example.ui.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.data.model.MediaItem
import com.example.data.model.formatWatchTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

data class YearReviewSlideData(
    val eyebrow: String,
    val headline: String,
    val valueText: String,
    val detailText: String,
    val accentHex: Int
)

object ShareCardRenderer {

    suspend fun loadPosterBitmap(context: Context, url: String): Bitmap? = withContext(Dispatchers.IO) {
        if (url.isBlank()) return@withContext null
        runCatching {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .size(480, 720)
                .build()
            val result = loader.execute(request)
            val drawable = (result as? SuccessResult)?.drawable
            (drawable as? BitmapDrawable)?.bitmap
        }.getOrNull()
    }

    suspend fun renderMediaShareBitmap(context: Context, item: MediaItem): Bitmap {
        val poster = loadPosterBitmap(context, item.posterUrl)
        return withContext(Dispatchers.Default) {
            val w = 1080
            val h = 1440
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)

            drawNeonBackdrop(canvas, w, h, 0xFF9333EA.toInt(), 0xFF06B6D4.toInt())

            // Outer glass frame
            val cardRect = RectF(56f, 68f, w - 56f, h - 68f)
            drawGlassPanel(canvas, cardRect, 44f, 0xCC110B24.toInt(), 0x55FFFFFF)

            // ZENIX Brand Pill
            val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(96f, 110f, 300f, 170f, 0xFFB26BFF.toInt(), 0xFFFF4FA0.toInt(), Shader.TileMode.CLAMP)
            }
            canvas.drawRoundRect(RectF(96f, 110f, 290f, 168f), 29f, 29f, brandPaint)
            val brandText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 28f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                letterSpacing = 0.14f
            }
            canvas.drawText("ZENIX", 142f, 149f, brandText)

            val catText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFF22E5FF.toInt()
                textSize = 26f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText(item.categoryEnum.label.uppercase(Locale.US), 314f, 148f, catText)

            // Poster box
            val posterRect = RectF(96f, 210f, 436f, 700f)
            if (poster != null) {
                val save = canvas.save()
                val clipPath = android.graphics.Path().apply {
                    addRoundRect(posterRect, 28f, 28f, android.graphics.Path.Direction.CW)
                }
                canvas.clipPath(clipPath)
                drawCenterCropBitmap(canvas, poster, posterRect)
                canvas.restoreToCount(save)
            } else {
                drawGlassPanel(canvas, posterRect, 28f, 0xFF1D1238.toInt(), 0x44FFFFFF)
                val initPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFFB26BFF.toInt()
                    textSize = 120f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(
                    item.title.take(1).uppercase(Locale.US),
                    posterRect.centerX(),
                    posterRect.centerY() + 40f,
                    initPaint
                )
            }
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = 0x55FFFFFF
            }
            canvas.drawRoundRect(posterRect, 28f, 28f, borderPaint)

            // Title + Metadata right of poster
            val textX = 472f
            val availW = (w - 96f - textX).toInt()
            val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFFF4EFFF.toInt()
                textSize = 52f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val titleLayout = StaticLayout.Builder.obtain(item.title, 0, item.title.length, titlePaint, availW)
                .setMaxLines(3)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .build()
            canvas.save()
            canvas.translate(textX, 216f)
            titleLayout.draw(canvas)
            canvas.restore()

            var cursorY = 216f + titleLayout.height + 24f

            val metaParts = listOfNotNull(
                item.formattedYearOrBlank().takeIf { it.isNotBlank() },
                item.normalizedPlatform.takeIf { it.isNotBlank() },
                if (item.categoryEnum.isEpisodic) "${item.episodesWatched}/${item.totalEpisodes} eps"
                else if (item.runtimeMinutes > 0L) formatWatchTime(item.runtimeMinutes) else null
            ).joinToString(" · ")

            if (metaParts.isNotBlank()) {
                val metaPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFFCDBDF5.toInt()
                    textSize = 28f
                }
                val metaLayout = StaticLayout.Builder.obtain(metaParts, 0, metaParts.length, metaPaint, availW)
                    .setMaxLines(2)
                    .build()
                canvas.save()
                canvas.translate(textX, cursorY)
                metaLayout.draw(canvas)
                canvas.restore()
                cursorY += metaLayout.height + 24f
            }

            if (item.personalRating > 0.0) {
                val pillRect = RectF(textX, cursorY, textX + 260f, cursorY + 70f)
                drawGlassPanel(canvas, pillRect, 35f, 0x33FFC857, 0xAAFFC857.toInt())
                val ratePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFFFFC857.toInt()
                    textSize = 34f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText(
                    "★ ${String.format(Locale.US, "%.1f", item.personalRating)} / 10",
                    textX + 28f,
                    cursorY + 47f,
                    ratePaint
                )
                cursorY += 92f
            }

            val statusRect = RectF(textX, cursorY, textX + 310f, cursorY + 56f)
            drawGlassPanel(canvas, statusRect, 28f, 0x3322E5FF, 0x8822E5FF.toInt())
            val statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFF22E5FF.toInt()
                textSize = 26f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText(item.statusEnum.label, textX + 24f, cursorY + 37f, statusPaint)

            // Bottom synopsis / review block
            var bottomY = 746f
            if (item.normalizedGenre.isNotBlank()) {
                val genrePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFFE879F9.toInt()
                    textSize = 28f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText(item.normalizedGenre, 96f, bottomY, genrePaint)
                bottomY += 46f
            }

            val quoteBody = item.personalReview.ifBlank { item.synopsis }.ifBlank { item.notes }
            if (quoteBody.isNotBlank()) {
                val bodyCard = RectF(96f, bottomY, w - 96f, h - 180f)
                drawGlassPanel(canvas, bodyCard, 28f, 0x66191030, 0x33FFFFFF)
                val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFF22E5FF.toInt()
                    textSize = 24f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText(
                    if (item.personalReview.isNotBlank()) "MY TAKE" else "SYNOPSIS",
                    132f,
                    bottomY + 52f,
                    labelPaint
                )
                val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFFF4EFFF.toInt()
                    textSize = 31f
                }
                val bodyLayout = StaticLayout.Builder.obtain(
                    quoteBody,
                    0,
                    quoteBody.length,
                    bodyPaint,
                    (w - 264)
                ).setMaxLines(8).setEllipsize(android.text.TextUtils.TruncateAt.END).build()
                canvas.save()
                canvas.translate(132f, bottomY + 76f)
                bodyLayout.draw(canvas)
                canvas.restore()
            }

            val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFFCDBDF5.toInt()
                textSize = 24f
            }
            canvas.drawText("Tracked with ZENIX · Personal Cinema Sanctuary", 96f, h - 110f, footerPaint)

            bmp
        }
    }

    suspend fun renderYearInReviewBitmap(
        context: Context,
        yearLabel: String,
        displayName: String,
        slide: YearReviewSlideData
    ): Bitmap = withContext(Dispatchers.Default) {
        val w = 1080
        val h = 1600
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        drawNeonBackdrop(canvas, w, h, slide.accentHex, 0xFF22E5FF.toInt())

        val cardRect = RectF(56f, 72f, w - 56f, h - 72f)
        drawGlassPanel(canvas, cardRect, 48f, 0xCC110B24.toInt(), 0x55FFFFFF)

        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFF4EFFF.toInt()
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            letterSpacing = 0.16f
        }
        canvas.drawText("ZENIX  ·  $yearLabel IN REVIEW", 104f, 152f, headerPaint)

        val subHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFCDBDF5.toInt()
            textSize = 28f
        }
        canvas.drawText(displayName, 104f, 198f, subHeaderPaint)

        // Accent glow pill
        val eyebrowRect = RectF(104f, 340f, 540f, 404f)
        drawGlassPanel(canvas, eyebrowRect, 32f, 0x33FFFFFF, slide.accentHex)
        val eyebrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = slide.accentHex
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            letterSpacing = 0.10f
        }
        canvas.drawText(slide.eyebrow.uppercase(Locale.US), 136f, 381f, eyebrowPaint)

        val headlinePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFCDBDF5.toInt()
            textSize = 44f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val headLayout = StaticLayout.Builder.obtain(slide.headline, 0, slide.headline.length, headlinePaint, w - 208)
            .setMaxLines(2)
            .build()
        canvas.save()
        canvas.translate(104f, 460f)
        headLayout.draw(canvas)
        canvas.restore()

        val valuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFF4EFFF.toInt()
            textSize = 112f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val valLayout = StaticLayout.Builder.obtain(slide.valueText, 0, slide.valueText.length, valuePaint, w - 208)
            .setMaxLines(3)
            .build()
        canvas.save()
        canvas.translate(104f, 620f)
        valLayout.draw(canvas)
        canvas.restore()

        val detailBox = RectF(104f, 1060f, w - 104f, 1340f)
        drawGlassPanel(canvas, detailBox, 32f, 0x661A1032, 0x33FFFFFF)
        val detailPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFF4EFFF.toInt()
            textSize = 36f
        }
        val detailLayout = StaticLayout.Builder.obtain(slide.detailText, 0, slide.detailText.length, detailPaint, w - 280)
            .setMaxLines(5)
            .build()
        canvas.save()
        canvas.translate(140f, 1110f)
        detailLayout.draw(canvas)
        canvas.restore()

        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFCDBDF5.toInt()
            textSize = 26f
        }
        canvas.drawText("Created in ZENIX · Personal Cinema & Series Vault", 104f, h - 122f, footerPaint)

        bmp
    }

    suspend fun shareBitmap(context: Context, bitmap: Bitmap, fileName: String, shareSubject: String): Result<Unit> {
        return runCatching {
            val uri = withContext(Dispatchers.IO) {
                val dir = File(context.cacheDir, "shared").apply { mkdirs() }
                val outFile = File(dir, "$fileName.png")
                FileOutputStream(outFile).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outFile)
            }
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, shareSubject)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(sendIntent, "Share via")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        }
    }

    suspend fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val safeName = "${fileName.replace(Regex("[^a-zA-Z0-9_-]"), "_")}_${System.currentTimeMillis()}.png"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, safeName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ZENIX")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri: Uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: error("Unable to create MediaStore image entry")
                resolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                } ?: error("Unable to open image output stream")
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                "Saved image to Pictures/ZENIX/$safeName"
            } else {
                val dir = File(context.filesDir, "saved_cards").apply { mkdirs() }
                val file = File(dir, safeName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                "Saved image to ${file.absolutePath}"
            }
        }
    }

    private fun drawNeonBackdrop(canvas: Canvas, w: Int, h: Int, primaryColor: Int, secondaryColor: Int) {
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint.color = 0xFF07050F.toInt()
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        val topRightGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = android.graphics.RadialGradient(
                w * 0.88f, h * 0.12f, w * 0.72f,
                intArrayOf((primaryColor and 0x00FFFFFF) or 0x66000000, Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(w * 0.88f, h * 0.12f, w * 0.72f, topRightGlow)

        val leftGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = android.graphics.RadialGradient(
                w * 0.10f, h * 0.56f, w * 0.65f,
                intArrayOf((secondaryColor and 0x00FFFFFF) or 0x44000000, Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(w * 0.10f, h * 0.56f, w * 0.65f, leftGlow)
    }

    private fun drawGlassPanel(canvas: Canvas, rect: RectF, radius: Float, fillColor: Int, borderColor: Int) {
        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fillColor }
        canvas.drawRoundRect(rect, radius, radius, fill)
        val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            color = borderColor
        }
        canvas.drawRoundRect(rect, radius, radius, stroke)
    }

    private fun drawCenterCropBitmap(canvas: Canvas, bitmap: Bitmap, dest: RectF) {
        val srcRatio = bitmap.width.toFloat() / bitmap.height.coerceAtLeast(1).toFloat()
        val dstRatio = dest.width() / dest.height().coerceAtLeast(1f)
        val srcRect = if (srcRatio > dstRatio) {
            val cropW = (bitmap.height * dstRatio).toInt()
            val left = ((bitmap.width - cropW) / 2).coerceAtLeast(0)
            android.graphics.Rect(left, 0, left + cropW, bitmap.height)
        } else {
            val cropH = (bitmap.width / dstRatio).toInt()
            val top = ((bitmap.height - cropH) / 2).coerceAtLeast(0)
            android.graphics.Rect(0, top, bitmap.width, top + cropH)
        }
        canvas.drawBitmap(bitmap, srcRect, dest, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
    }
}
