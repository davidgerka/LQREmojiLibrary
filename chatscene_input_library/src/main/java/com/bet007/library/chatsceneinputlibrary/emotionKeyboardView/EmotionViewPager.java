package com.bet007.library.chatsceneinputlibrary.emotionKeyboardView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.bet007.library.chatsceneinputlibrary.utils.LQREmotionKit;


/**
 * 装载表情的ViewPager
 */
public class EmotionViewPager extends ViewPager {

    private EnsureSizeCallback mEnsureSizeCallback;

    public EmotionViewPager(Context context) {
        super(context);
    }

    public EmotionViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        if (mEnsureSizeCallback != null && (w != oldw || h != oldh)) {
//            mEnsureSizeCallback.ensureSize(w, h);
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int measuredWidth = measureWidth(widthMeasureSpec);
//        int measuredHeight = measureHeight(heightMeasureSpec);
//        setMeasuredDimension(measuredWidth, measuredHeight);
//        if (mEnsureSizeCallback != null) {
//            mEnsureSizeCallback.ensureSize(measuredWidth, measuredHeight);
//        }
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = LQREmotionKit.dip2px(200);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = LQREmotionKit.dip2px(200);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public void setEnsureSizeCallback(EnsureSizeCallback ensureSizeCallback) {
        mEnsureSizeCallback = ensureSizeCallback;
    }

    public interface EnsureSizeCallback {
        void ensureSize(int width, int height);
    }

}
