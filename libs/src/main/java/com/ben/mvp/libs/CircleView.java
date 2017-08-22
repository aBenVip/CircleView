package com.ben.mvp.libs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.text.DecimalFormat;

/**
 * 类描述:
 * 创建人:aBen
 * 创建时间:2017/8/21
 * 备注:
 */

public class CircleView extends View {
    //输入利率
    private double middleText;
    //圆环底色
    private int defaultColor;
    //圆环底色宽度
    private float defaultWidth;
    //圆环当前色
    private int currentColor;
    //圆环当前宽度
    private float currentWidth;
    private Paint textPaint;
    //文本颜色
    private int textColor;
    private Paint defaultPaint;
    //圆环半径
    private int roundWidth;
    private Paint currentPaint;
    private BarAnimation anim;
    //动画时间
    private long duration = 1000;
    //当前进度比例Float
    private float mSweepAngle;
    private float mSweepAnglePer;
    private double mCount;
    private RectF rectF = new RectF();
    //第二个文本
    private CharSequence rateText;
    //文本间距
    private int textPadding;

    public CircleView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        if (typedArray != null) {
            defaultColor = typedArray.getColor(R.styleable.CircleView_defaultColor, Color.GRAY);
            defaultWidth = typedArray.getDimensionPixelSize(R.styleable.CircleView_defaultWith, dip2px(context, 18));
            currentColor = typedArray.getColor(R.styleable.CircleView_currentColor, Color.RED);
            currentWidth = typedArray.getDimensionPixelSize(R.styleable.CircleView_currentWith, dip2px(context, 10));
            roundWidth = typedArray.getDimensionPixelSize(R.styleable.CircleView_roundWidth, dip2px(context, 90));
            textColor = typedArray.getColor(R.styleable.CircleView_textColor, Color.GRAY);
            rateText = typedArray.getText(R.styleable.CircleView_rateText);
            textPadding = typedArray.getDimensionPixelSize(R.styleable.CircleView_textPadding, dip2px(context, 20));
            typedArray.recycle();
        }
        //底部进度画笔设置
        defaultPaint = new Paint();
        defaultPaint.setColor(defaultColor);
        defaultPaint.setStyle(Paint.Style.STROKE);
        defaultPaint.setStrokeWidth(defaultWidth);
        defaultPaint.setStrokeCap(Paint.Cap.ROUND);
        defaultPaint.setAntiAlias(true);
        //当前进度画笔设置
        currentPaint = new Paint();
        currentPaint.setColor(currentColor);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(currentWidth);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setAntiAlias(true);
        //文本画笔颜色
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);

        anim = new BarAnimation();
        anim.setDuration(duration);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rectF, 180, 180, false, defaultPaint);
        canvas.drawArc(rectF, 180, mSweepAnglePer, false, currentPaint);
        //Y为基线位置
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        canvas.drawText(new DecimalFormat(".0").format(mCount * 100) + "%", getWidth() / 2, (rectF.top + roundWidth + defaultWidth - fontMetrics.top + fontMetrics.bottom) / 2, textPaint);
        canvas.drawText(String.valueOf(rateText), getWidth() / 2, (rectF.top + roundWidth + defaultWidth - fontMetrics.top * 2 + fontMetrics.bottom * 2 + textPadding) / 2, textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 400;
        int desiredHeight = 200;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(desiredWidth, widthSize);
                break;
            default:
                width = desiredWidth;
                break;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(desiredHeight, heightSize);
                break;
            default:
                height = desiredHeight;
                break;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height + 20);

       /* int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);*/
//        setMeasuredDimension(width, height+20);// 强制改View为以最短边为长度的正方形
        float pressExtraStrokeWidth = 2;
        rectF.set(defaultWidth + pressExtraStrokeWidth,
                defaultWidth + pressExtraStrokeWidth, width
                        - defaultWidth - pressExtraStrokeWidth, width
                        - defaultWidth - pressExtraStrokeWidth);// 设置矩形
        defaultPaint.setStrokeWidth(defaultWidth);
        currentPaint.setStrokeWidth(defaultWidth);
    }

    private class BarAnimation extends Animation {
        //        * 动画类利用了applyTransformation参数中的interpolatedTime参数(从0到1)的变化特点，
        //        * 实现了该View的某个属性随时间改变而改变。原理是在每次系统调用animation的applyTransformation()方法时，
        //        * 改变mSweepAnglePer，mCount的值，
        //        * 然后调用postInvalidate()不停的绘制view。
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            //mSweepAnglePer，mCount这两个属性只是动画过程中要用到的临时属性，
            //mText和mSweepAngle才是动画结束之后表示扇形弧度和中间数值的真实值。
            if (interpolatedTime < 1.0f) {
                mSweepAnglePer = interpolatedTime * mSweepAngle * 180;
                mCount = (interpolatedTime * middleText);
            } else {
                mSweepAnglePer = mSweepAngle * 180;
                mCount = middleText;
            }
            postInvalidate();
        }
    }

    public double getMiddleText() {
        return middleText;
    }

    public void setMiddleText(double middleText) {
        this.middleText = middleText;
        this.startAnimation(anim);
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public float getDefaultWidth() {
        return defaultWidth;
    }

    public void setDefaultWidth(float defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    public float getCurrentWidth() {
        return currentWidth;
    }

    public void setCurrentWidth(float currentWidth) {
        this.currentWidth = currentWidth;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(int roundWidth) {
        this.roundWidth = roundWidth;
    }

    public float getmSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(float mSweepAngle) {
        this.mSweepAngle = mSweepAngle;
        this.startAnimation(anim);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public float getSweepAnglePer() {
        return mSweepAnglePer;
    }

    public void setmSweepAnglePer(float mSweepAnglePer) {
        this.mSweepAnglePer = mSweepAnglePer;
    }

    public CharSequence getRateText() {
        return rateText;
    }

    public void setRateText(CharSequence rateText) {
        this.rateText = rateText;
    }

    public int getTextPadding() {
        return textPadding;
    }

    public void setTextPadding(int textPadding) {
        this.textPadding = textPadding;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
