package com.aohanyao.jelly.library;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ScrollView;

import com.aohanyao.jelly.library.inf.BouncingJellyListener;
import com.aohanyao.jelly.library.util.BouncingInterpolatorType;
import com.aohanyao.jelly.library.util.BouncingType;
import com.aohanyao.jelly.library.util.ScreenUtils;
import com.nineoldandroids.view.ViewHelper;

/**
 * <p>作者：江俊超 on 2016/8/30 14:42</p>
 * <p>邮箱：928692385@qq.com</p>
 * <p>ScrollView</p>
 */
public class BouncingJellyScrollView extends ScrollView {
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
    private int dowY2;

    public BouncingJellyScrollView(Context context) {
        this(context, null);
    }

    public BouncingJellyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BouncingJellyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);

    }

    /**
     * @param attrs
     */
    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BouncingJellyScrollView);
            //差值器
            mTimeInterpolator = BouncingInterpolatorType.getTimeInterpolator(typedArray.getInteger(
                    R.styleable.BouncingJellyScrollView_BouncingInterpolator
                    , BouncingInterpolatorType.OVERSHOOT_INTERPOLATOR));
            //回弹速度
            mBouncingDuration = typedArray.getInteger(R.styleable.BouncingJellyScrollView_BouncingDuration, mBouncingDuration);
            //果冻类型
            mBouncingType = typedArray.getInt(R.styleable.BouncingJellyScrollView_BouncingType, BouncingType.BOTH);
            typedArray.recycle();
            //获取是差值  整个屏幕的三倍大小
            bouncingOffset = ScreenUtils.getScreenHeight(getContext()) * 3;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //判断可滚动的内容是不是小于整个屏幕的高度，以防底部进行所动
        int contentHeight = getChildAt(0).getHeight();
        if (contentHeight > 0 && contentHeight <= ScreenUtils.getScreenHeight(getContext())) {
            mBouncingType = BouncingType.TOP;
        }
    }

    /**
     * 从顶部开始滑动
     */
    public void bouncingTo() {
        //设置X坐标点
        ViewHelper.setPivotX(this, getWidth() / 2);
        //设置Y坐标点
        ViewHelper.setPivotY(this, 0);
        //进行缩放
        ViewHelper.setScaleY(this, 1.0f + offsetScale);
        if (onBouncingJellyListener != null) {
            onBouncingJellyListener.onBouncingJelly(1.0f + offsetScale);
        }
    }

    /**
     * 从顶部开始滑动
     */
    public void bouncingBottom() {
        //设置X坐标点
        ViewHelper.setPivotX(this, getWidth() / 2);
        //设置Y坐标点
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
                //移动坐标
                dowY = (int) event.getRawY();
                //按下坐标 用于计算缩放值
                dowY2 = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mBouncingType != BouncingType.NONE) {
                    moveX = (int) event.getRawX();
                    moveY = (int) event.getRawY();
                    //dy值 判断方向
                    int dy = moveY - dowY;
                    dowY = moveY;
                    //顶部
                    if (dy > 0 && getScrollY() == 0) {
                        //判断果冻的类型
                        if (mBouncingType == BouncingType.TOP || mBouncingType == BouncingType.BOTH) {
                            //获取现在坐标与按下坐标的差值
                            int abs = moveY - dowY2;
                            //计算缩放值
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            isTop = true;
                            bouncingTo();
                            return true;
                        }
                    } else if (getScrollY() == 0 && dy < 0 && offsetScale > 0) {//为顶部 并且dy为下拉 并且缩放值大于0
                        if (mBouncingType == BouncingType.TOP || mBouncingType == BouncingType.BOTH) {
                            //获取现在坐标与按下坐标的差值
                            int abs = moveY - dowY2;
                            //计算缩放值
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            if (abs <= 0) {
                                offsetScale = 0;
                                dowY2 = moveY;
                            }
                            isTop = true;
                            bouncingTo();
                            return true;
                        }
                    }

                    //底部
                    if (dy < 0 && getScrollY() + getHeight() >= computeVerticalScrollRange()) {//滚动到底部
                        if (mBouncingType == BouncingType.BOTTOM || mBouncingType == BouncingType.BOTH) {
                            int abs = moveY - dowY2;
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            isTop = false;
                            bouncingBottom();
                        }
                    } else if (dy > 0 && getScrollY() + getHeight() >= computeVerticalScrollRange() && offsetScale > 0) {
                        if (mBouncingType == BouncingType.BOTTOM || mBouncingType == BouncingType.BOTH) {
                            int abs = moveY - dowY2;
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            if (abs >= 0) {
                                offsetScale = 0;
                                dowY2 = moveY;
                            }
                            isTop = false;
                            bouncingBottom();
                            return true;
                        }
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
        //初始化
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
            offsetScale = 0;
            bouncingTo();
        }
        if (mTimeInterpolator == null) {
            mTimeInterpolator = new OvershootInterpolator();
        }
        //散发值
        animator = ValueAnimator.ofFloat(from, to).setDuration(mBouncingDuration);
        animator.setInterpolator(mTimeInterpolator);//差值器
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取动画阶段的值
                offsetScale = (float) animation.getAnimatedValue();
                if (isTop) {//回弹到顶部
                    bouncingTo();
                } else {//回弹到底部
                    bouncingBottom();
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
