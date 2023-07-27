package com.example.textdemo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

//https://blog.csdn.net/h4x0r_007/article/details/105271347
//https://cloud.tencent.com/developer/article/1820593
public class CenterSpaceImageSpan extends ImageSpan {
    private final int mMarginLeft;
    private final int mMarginRight;

    public CenterSpaceImageSpan(Drawable drawable) {
        this(drawable, 0, 0);
    }

    public CenterSpaceImageSpan(Drawable drawable,
                                int marginLeft, int marginRight) {
        super(drawable);
        mMarginLeft = marginLeft;
        mMarginRight = marginRight;
    }

//    @Override
//    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x,
//                     int top, int y, int bottom,
//                     @NonNull Paint paint) {
//        Drawable b = getDrawable();
//        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
//        x = mMarginLeft + x;
//        int transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;
//        canvas.save();
//        canvas.translate(x, transY);
//        b.draw(canvas);
//        canvas.restore();
//    }
//
//    @Override
//    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable
//            Paint.FontMetricsInt fm) {
////        Drawable d = getCachedDrawable();
////        Rect rect = d.getBounds();
////
////        if (fm != null) {
////            fm.ascent = -rect.bottom;
////            fm.descent = 0;
////
////            fm.top = fm.ascent;
////            fm.bottom = 0;
////        }
////
////        return mMarginLeft + rect.right + mMarginRight;
//        return mMarginLeft + super.getSize(paint, text, start, end, fm) + mMarginRight;
//    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fontMetricsInt) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (null != fontMetricsInt) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.descent - fmPaint.ascent;
            int drHeight = rect.bottom - rect.top;
            int centerY = fmPaint.ascent + fontHeight / 2;
            fontMetricsInt.ascent = centerY - drHeight / 2;
            fontMetricsInt.top = fontMetricsInt.ascent;
            fontMetricsInt.bottom = centerY + drHeight / 2;
            fontMetricsInt.descent = fontMetricsInt.bottom;
        }
        return mMarginLeft + rect.right + mMarginRight;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
        x = mMarginLeft + x;
        int fontHeight = fmPaint.descent - fmPaint.ascent;
        int centerY = y + fmPaint.descent - fontHeight / 2;
        int transY = centerY - (drawable.getBounds().bottom - drawable.getBounds().top) / 2;
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }

}