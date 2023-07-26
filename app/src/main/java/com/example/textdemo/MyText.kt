package com.example.textdemo

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.*
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView

/**
 * @author yongzhen_zou@163.com
 * @date 2023/7/26 09:05
 */
class MyText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
    val drawablePadding = dip2px(5f)
    var drawable: Drawable? = null
    var contentText: CharSequence = ""
    var maxLine  = 2
    init {
        setLineSpacing(dip2px(2f).toFloat(), 1f);
    }
    fun setString(text: CharSequence, drawable: Drawable?) {
        //这里先赋值考虑在列表中复用和屏幕翻转
        this.drawable = drawable
        this.contentText = text
        if (drawable == null) {
            setText(text)
            return
        }
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        var imageSpan = MyImageSpan(drawable)
        if (TextUtils.isEmpty(text)) {
            setText("")
        } else {
            post {
                val staticLayout = StaticLayout(
                    text,
                    paint,
                    measuredWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    1f,
                    dip2px(2f).toFloat(),
                    true
                )
                var lineCount = staticLayout.lineCount
                var resultText = SpannableStringBuilder()
                if (lineCount >= maxLine) {
                    var maxLineTextStartIndex = staticLayout.getLineStart(maxLine -1)
                    var maxLineTextEndIndex = staticLayout.getLineEnd(maxLine -1)
                    var maxLineText = text.subSequence(maxLineTextStartIndex, maxLineTextEndIndex)
                    var maxLineTextWithEllipsize = TextUtils.ellipsize(
                        maxLineText,
                        paint,
                        (measuredWidth - drawablePadding - drawable.intrinsicWidth).toFloat(),
                        TextUtils.TruncateAt.END
                    )
                    var textWithOutMaxLine = text.subSequence(0, maxLineTextStartIndex)
                    resultText.append(textWithOutMaxLine).append(maxLineTextWithEllipsize)
                } else {
                    resultText.append(text)
                }
                resultText.append(" ")//这里拼接一个空字符串后续追加图片时用于替换
                addDrawable(resultText, imageSpan)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        setString(contentText, drawable)
    }

    private fun addDrawable(text: CharSequence, imageSpan: MyImageSpan) {
        var spannableString = SpannableStringBuilder(text)
        spannableString.setSpan(
            imageSpan,
            text.length - 1,
            text.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        setText(spannableString)
    }

    fun dip2px(dpValue: Float): Int {
        val scale: Float = getResources().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun log(msg: String) {
        Log.e("MyText", "log: " + msg)

    }

    class MyImageSpan : ImageSpan {
        constructor(drawable: Drawable) : super(drawable, ALIGN_CENTER)

        override fun draw(
            canvas: Canvas,
            text: CharSequence?,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
            var b = getDrawable();
            var fm = paint.getFontMetricsInt();
            var x = 5.dp + x;
            var transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;
            canvas.save();
            canvas.translate(x, transY.toFloat());
            b.draw(canvas);
            canvas.restore();
        }
    }
}