package com.aohanyao.jelly.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import com.aohanyao.jelly.library.inf.BouncingJellyListener;
import com.aohanyao.jelly.library.util.BouncingInterpolatorType;
import com.aohanyao.jelly.library.util.BouncingType;
import com.nineoldandroids.view.ViewHelper;

/**
 * 针对列表的
 */
public class BouncingJellyToListLayout extends RelativeLayout {
    private int dowX;
    private int dowY;
    private int moveX;
    private int moveY;
    private float bouncingOffset = 2850f;
    private float offsetScale;
    private ValueAnimator animator;
    private boolean isTop = true;
    private String TAG = "BouncingJellyScroolView";
    private BouncingJellyListener onBouncingJellyListener;//果冻弹跳的比例数
    private TimeInterpolator mTimeInterpolator;//差值器
    private int mBouncingDuration = 300;//回弹的时间
    private int mBouncingType;

    public BouncingJellyToListLayout(Context context) {
        this(context, null);
    }

    public BouncingJellyToListLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BouncingJellyToListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }

    /**
     * @param attrs
     */
    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BouncingJellyScrollView);
            mTimeInterpolator = BouncingInterpolatorType.getTimeInterpolator(typedArray.getInteger(
                    R.styleable.BouncingJellyScrollView_BouncingInterpolator
                    , BouncingInterpolatorType.OVERSHOOT_INTERPOLATOR));
            mBouncingDuration = typedArray.getInteger(R.styleable.BouncingJellyScrollView_BouncingDuration, mBouncingDuration);
            mBouncingType = typedArray.getInt(R.styleable.BouncingJellyScrollView_BouncingType, BouncingType.BOTH);
            typedArray.recycle();
        }
    }

    /**
     * 从顶部开始滑动
     */
    public void bouncingTo() {
        ViewHelper.setPivotX(this, getWidth() / 2);
        ViewHelper.setPivotY(this, 0);
        ViewHelper.setScaleY(this, 1.0f + offsetScale);
        if (onBouncingJellyListener != null) {
            onBouncingJellyListener.onBouncingJelly(1.0f + offsetScale);
        }
    }

    /**
     * 从顶部开始滑动
     */
    public void bouncingBottom() {
        ViewHelper.setPivotX(this, getWidth() / 2);
        ViewHelper.setPivotY(this, getHeight());
        ViewHelper.setScaleY(this, 1.0f + offsetScale);
        if (onBouncingJellyListener != null) {
            onBouncingJellyListener.onBouncingJelly(1.0f + offsetScale);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dowX = (int) event.getRawX();
                dowY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mBouncingType != BouncingType.NONE) {
                    moveX = (int) event.getRawX();
                    moveY = (int) event.getRawY();
                    int abs = moveY - dowY;
                    offsetScale = (Math.abs(abs) / bouncingOffset);
                    if (offsetScale > 0.3f) {
                        offsetScale = 0.3f;
                    }
                    if (abs > 20 && getScrollY() == 0) {
                        if (mBouncingType == BouncingType.TOP || mBouncingType == BouncingType.BOTH) {
                            isTop = true;
                            bouncingTo();
                        } else {
                            offsetScale = 0;
                        }
                    } else if (abs < 0 && getScrollY() + getHeight() >= computeVerticalScrollRange()) {//滚动到底部
                        if (mBouncingType == BouncingType.BOTTOM || mBouncingType == BouncingType.BOTH) {
                            isTop = false;
                            bouncingBottom();
                        } else {
                            offsetScale = 0;
                        }
                    } else {
                        offsetScale = 0;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mBouncingType != BouncingType.NONE) {
                    if (offsetScale > 0) {
                        backBouncing(offsetScale, 0);
                        return true;
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.e(TAG, "onTouchEvent: " );
        return super.onTouchEvent(ev);
    }

    /**
     * 进行回弹
     *
     * @param from
     * @param to
     */
    private void backBouncing(final float from, final float to) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
            offsetScale = 0;
            bouncingTo();
        }
        if (mTimeInterpolator == null) {
            mTimeInterpolator = new OvershootInterpolator();
        }
        animator = ValueAnimator.ofFloat(from, to).setDuration(mBouncingDuration);
        animator.setInterpolator(mTimeInterpolator);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetScale = (float) animation.getAnimatedValue();
                if (isTop) {
                    bouncingTo();
                } else {
                    bouncingBottom();
                }
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (from == 0) {
                    backBouncing(to, from);
                }
            }
        });
        animator.start();
    }


    public void setOnBouncingJellyListener(BouncingJellyListener onBouncingJellyListener) {
        this.onBouncingJellyListener = onBouncingJellyListener;
    }

    public void setmTimeInterpolator(TimeInterpolator mTimeInterpolator) {
        this.mTimeInterpolator = mTimeInterpolator;
    }

    public void setmTimeInterpolatorType() {

    }

    public void setmBouncingDuration(int mBouncingDuration) {
        this.mBouncingDuration = mBouncingDuration;
    }
}
