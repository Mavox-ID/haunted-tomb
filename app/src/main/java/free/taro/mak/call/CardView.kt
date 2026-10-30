package free.taro.mak.call

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.view.View

class CardView(context: Context) : View(context) {
    var card: Card? = null
        set(value) {
            field = value
            invalidate()
        }
    var faceUp: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private val backFill = Color.parseColor("#23242B")
    private val backLine = Color.parseColor("#5A5D6B")
    private val cream = Color.parseColor("#F4EFE6")
    private val faceLine = Color.parseColor("#BDB7A9")
    private val red = Color.parseColor("#C62828")
    private val ink = Color.parseColor("#1B1B2F")
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val txt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }
    private val rect = RectF()
    private val inner = RectF()

    private val heartP = Path().apply {
        moveTo(0.5f, 0.95f)
        cubicTo(-0.05f, 0.55f, 0.05f, 0.02f, 0.5f, 0.3f)
        cubicTo(0.95f, 0.02f, 1.05f, 0.55f, 0.5f, 0.95f)
        close()
    }
    private val diamondP = Path().apply {
        moveTo(0.5f, 0f)
        lineTo(0.88f, 0.5f)
        lineTo(0.5f, 1f)
        lineTo(0.12f, 0.5f)
        close()
    }
    private val stemP = Path().apply {
        moveTo(0.5f, 0.55f)
        lineTo(0.37f, 1f)
        lineTo(0.63f, 1f)
        close()
    }

    init {
        cameraDistance = 12000f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(w, (w * 1.5f).toInt())
    }

    fun flip(done: () -> Unit) {
        animate().rotationY(90f).setDuration(170).withEndAction {
            faceUp = true
            rotationY = -90f
            animate().rotationY(0f).setDuration(170).withEndAction { done() }.start()
        }.start()
    }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        rect.set(w * 0.03f, h * 0.02f, w * 0.97f, h * 0.98f)
        val rad = w * 0.06f
        val c = card
        if (c == null) drawEmpty(canvas, w, h, rad)
        else if (!faceUp) drawBack(canvas, w, h, rad)
        else drawFace(canvas, c, w, h, rad)
    }

    private fun drawEmpty(canvas: Canvas, w: Float, h: Float, rad: Float) {
        line.color = backLine
        line.alpha = 255
        line.strokeWidth = w * 0.02f
        line.pathEffect = DashPathEffect(floatArrayOf(w * 0.06f, w * 0.05f), 0f)
        canvas.drawRoundRect(rect, rad, rad, line)
        line.pathEffect = null
        line.strokeWidth = w * 0.025f
        canvas.drawLine(w / 2f, h / 2f - w * 0.1f, w / 2f, h / 2f + w * 0.1f, line)
        canvas.drawLine(w / 2f - w * 0.1f, h / 2f, w / 2f + w * 0.1f, h / 2f, line)
    }

    private fun drawBack(canvas: Canvas, w: Float, h: Float, rad: Float) {
        fill.style = Paint.Style.FILL
        fill.color = backFill
        canvas.drawRoundRect(rect, rad, rad, fill)
        line.color = backLine
        line.alpha = 255
        line.strokeWidth = w * 0.02f
        canvas.drawRoundRect(rect, rad, rad, line)
        inner.set(w * 0.1f, h * 0.065f, w * 0.9f, h * 0.935f)
        canvas.save()
        canvas.clipRect(inner)
        line.alpha = 60
        line.strokeWidth = w * 0.01f
        val step = w * 0.16f
        var x = -h
        while (x < w + h) {
            canvas.drawLine(x, 0f, x + h, h, line)
            canvas.drawLine(x + h, 0f, x, h, line)
            x += step
        }
        canvas.restore()
        line.alpha = 255
    }

    private fun drawFace(canvas: Canvas, c: Card, w: Float, h: Float, rad: Float) {
        fill.style = Paint.Style.FILL
        fill.color = cream
        canvas.drawRoundRect(rect, rad, rad, fill)
        line.color = faceLine
        line.alpha = 255
        line.strokeWidth = w * 0.015f
        canvas.drawRoundRect(rect, rad, rad, line)
        if (c.joker) {
            val col = Color.parseColor("#8E2A3A")
            star(canvas, w / 2f, h * 0.4f, w * 0.3f, col)
            text(canvas, "JOKER", w / 2f, h * 0.74f, w * 0.17f, col)
            return
        }
        val col = if (c.suit < 2) red else ink
        val label = when (c.rank) {
            1 -> "A"
            11 -> "J"
            12 -> "Q"
            13 -> "K"
            else -> c.rank.toString()
        }
        corner(canvas, label, c.suit, col, w, h)
        canvas.save()
        canvas.rotate(180f, w / 2f, h / 2f)
        corner(canvas, label, c.suit, col, w, h)
        canvas.restore()
        if (c.rank >= 11) {
            text(canvas, label, w / 2f, h * 0.44f, w * 0.52f, col)
            suit(canvas, c.suit, w / 2f, h * 0.68f, w * 0.26f, col)
        } else {
            suit(canvas, c.suit, w / 2f, h / 2f, w * 0.5f, col)
        }
    }

    private fun corner(canvas: Canvas, label: String, s: Int, col: Int, w: Float, h: Float) {
        text(canvas, label, w * 0.2f, h * 0.12f, w * 0.21f, col)
        suit(canvas, s, w * 0.2f, h * 0.12f + w * 0.2f, w * 0.17f, col)
    }

    private fun text(canvas: Canvas, s: String, cx: Float, cy: Float, size: Float, color: Int) {
        txt.textSize = size
        txt.color = color
        val fm = txt.fontMetrics
        canvas.drawText(s, cx, cy - (fm.ascent + fm.descent) / 2f, txt)
    }

    private fun star(canvas: Canvas, cx: Float, cy: Float, r: Float, color: Int) {
        val p = Path()
        for (i in 0 until 10) {
            val ang = -Math.PI / 2 + i * Math.PI / 5
            val rr = if (i % 2 == 0) r else r * 0.42f
            val x = cx + (rr * Math.cos(ang)).toFloat()
            val y = cy + (rr * Math.sin(ang)).toFloat()
            if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
        }
        p.close()
        fill.style = Paint.Style.FILL
        fill.color = color
        canvas.drawPath(p, fill)
    }

    private fun suit(canvas: Canvas, s: Int, cx: Float, cy: Float, size: Float, color: Int) {
        fill.style = Paint.Style.FILL
        fill.color = color
        canvas.save()
        canvas.translate(cx - size / 2f, cy - size / 2f)
        canvas.scale(size, size)
        when (s) {
            0 -> canvas.drawPath(heartP, fill)
            1 -> canvas.drawPath(diamondP, fill)
            2 -> {
                canvas.drawCircle(0.5f, 0.27f, 0.2f, fill)
                canvas.drawCircle(0.27f, 0.56f, 0.2f, fill)
                canvas.drawCircle(0.73f, 0.56f, 0.2f, fill)
                canvas.drawCircle(0.5f, 0.5f, 0.13f, fill)
                canvas.drawPath(stemP, fill)
            }
            else -> {
                canvas.drawPath(stemP, fill)
                canvas.save()
                canvas.translate(0f, 0.85f)
                canvas.scale(1f, -0.85f)
                canvas.drawPath(heartP, fill)
                canvas.restore()
            }
        }
        canvas.restore()
    }
}
