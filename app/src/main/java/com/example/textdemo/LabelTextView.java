package com.example.textdemo;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
/**
 * @author yongzhen_zou@163.com
 * @date 2023/7/27 13:49
 */
public class LabelTextView extends AppCompatTextView {
    private static final int WRAP  = 1;//换行,一行文本剩余空间能够展示一个标签但是不够展示标签加间距的特殊情况处理
    private static final int SPLICING  = 2;//拼接,一行文本剩余空间能够展示一个标签但是不够展示标签加间距的特殊情况处理
    private int splicingMethod = WRAP;//拼接方法,一行文本剩余空间能够展示一个标签但是不够展示标签加间距的特殊情况处理
    private int maxLines = 1;
    private int drawablePadding = 0;
    private Drawable drawable;
    private CharSequence contentText;

    public LabelTextView(Context context) {
        this(context,null);
    }

    public LabelTextView( Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LabelTextView( Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelTextView);
        maxLines = typedArray.getInteger(R.styleable.LabelTextView_maxLines,1);
        drawablePadding = (int) typedArray.getDimension(R.styleable.LabelTextView_drawablePadding,0);
        splicingMethod =  typedArray.getInteger(R.styleable.LabelTextView_splicingMethod,WRAP);
        typedArray.recycle();
    }

    public void setContentText(CharSequence text, Drawable labelDrawable){
        //这里先赋值考虑在列表中复用和屏幕翻转
        this.drawable = labelDrawable;
        contentText = text;
        if (drawable == null){
            setText(text);
            return;
        }
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        if (TextUtils.isEmpty(contentText)){
            setText("");
        }else {
            post(new Runnable() {
                @Override
                public void run() {
                    StaticLayout staticLayout = new StaticLayout(contentText,getPaint(),getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL,1f,getLineSpacingExtra(),getIncludeFontPadding());
                    int lineCount = staticLayout.getLineCount();
                    SpannableStringBuilder resultText = new SpannableStringBuilder();
                    CenterSpaceImageSpan imageSpan;
                    if (lineCount >= maxLines){
                        int maxLineTextStartIndex = staticLayout.getLineStart(maxLines - 1);
                        int maxLineTextEndIndex = staticLayout.getLineEnd(maxLines - 1);
                        CharSequence maxLineText = contentText.subSequence(maxLineTextStartIndex, maxLineTextEndIndex);
                        CharSequence maxLineTextWithEllipsize = TextUtils.ellipsize(maxLineText, getPaint(), getMeasuredWidth() - drawablePadding - drawable.getIntrinsicWidth(), TextUtils.TruncateAt.END);
                        CharSequence textWithOutMaxLine = contentText.subSequence(0, maxLineTextStartIndex);
                        resultText.append(textWithOutMaxLine).append(maxLineTextWithEllipsize);
                        imageSpan = new CenterSpaceImageSpan(drawable,drawablePadding,0);
                    }else {
                        resultText.append(text);
                        float textWidth = staticLayout.getLineWidth(staticLayout.getLineCount() - 1);
                        float remainingSpace = getMeasuredWidth() - textWidth;
                        /**
                         * 一行文本剩余空间能够展示一个标签但是不够展示标签加间距的特殊情况处理
                         */
                        if (splicingMethod == WRAP){
                            //splicingMethod == WRAP 换行,标签换行左对齐（可能造成文本剩余空间能够展示下标签但是展示空白的问题）
                            if (remainingSpace > drawablePadding + drawable.getIntrinsicWidth()){
                                //不会换行
                                imageSpan = new CenterSpaceImageSpan(drawable,drawablePadding,0);
                            }else if (remainingSpace > drawable.getIntrinsicWidth() && remainingSpace < drawablePadding + drawable.getIntrinsicWidth()){
                                //这里因为能展示下标签textview会把标签追加在文本后面但是没有间距所以这里选择手动换行
                                resultText.append("\n");
                                imageSpan = new CenterSpaceImageSpan(drawable,0,0);
                            }else {
                                imageSpan = new CenterSpaceImageSpan(drawable,0,0);
                            }
                        }else {
//                            splicingMethod == SPLICING 拼接，标签追加在文本后面展示忽略间距（可能造成标签与文本间距为0的问题）
                            if (remainingSpace < drawablePadding + drawable.getIntrinsicWidth()){
                                //换行显示，但是如果这个时候remainingSpace大于标签间宽度，textview会把标签会追加在文本后面展示忽略间距
                                imageSpan = new CenterSpaceImageSpan(drawable,0,0);
                            }else {
                                imageSpan = new CenterSpaceImageSpan(drawable,drawablePadding,0);
                            }
                        }
                    }
                    resultText.append(" ");//这里拼接一个空字符串后续追加图片时用于替换
                    addDrawable(resultText, imageSpan);
                }
            });
        }
    }

    private void addDrawable(SpannableStringBuilder resultText, CenterSpaceImageSpan imageSpan) {
        resultText.setSpan(imageSpan,resultText.length()-1,resultText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        setText(resultText);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentText(contentText,drawable);
    }


}
