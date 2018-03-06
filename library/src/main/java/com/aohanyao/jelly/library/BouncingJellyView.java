package com.aohanyao.jelly.library;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.OvershootInterpolator;

import com.aohanyao.jelly.library.inf.BouncingJellyListener;
import com.aohanyao.jelly.library.util.BouncingInterpolatorType;
import com.aohanyao.jelly.library.util.BouncingType;
import com.aohanyao.jelly.library.util.BouncingScreenUtils;

/**
 * <p>作者：江俊超 on 2016/8/30 14:42</p>
 * <p>ScrollView</p>
 * ver
 * ---------------------------------------
 * 2018年3月6日11:43:01
 * 修复：https://github.com/aohanyao/BouncingJelly/issues/1#
 * 产生原因是：
 * ①没有设置setFillViewport，导致子ViewGroup不是全部铺满的
 * ②在onSizeChang中错误的赋值
 * ---------------------------------------
 */
public class BouncingJellyView extends NestedScrollView {
    private int onInterceptTouchDownY;
    private int dispatchTouchDowY;
    private float bouncingOffset = 2850f;
    private float offsetScale;
    private ValueAnimator animator;
    private boolean isTop = true;
    private String TAG = "BouncingJellyScroolView";
    /**
     * 回调
     */
    private BouncingJellyListener onBouncingJellyListener;
    private TimeInterpolator mTimeInterpolator;//差值器
    private int mBouncingDuration = 300;//回弹的时间
    private int mBouncingType;
    private View childView;

    private int mTouchSlop;

    public BouncingJellyView(Context context) {
        this(context, null);
    }

    public BouncingJellyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BouncingJellyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        childView = getChildAt(0);
    }

    /**
     * @param attrs
     */
    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BouncingJellyView);
            //差值器
            mTimeInterpolator = BouncingInterpolatorType.getTimeInterpolator(typedArray.getInteger(
                    R.styleable.BouncingJellyView_BouncingInterpolator
                    , BouncingInterpolatorType.OVERSHOOT_INTERPOLATOR));
            //回弹速度
            mBouncingDuration = typedArray.getInteger(R.styleable.BouncingJellyView_BouncingDuration, mBouncingDuration);
            //果冻类型
            mBouncingType = typedArray.getInt(R.styleable.BouncingJellyView_BouncingType, BouncingType.BOTH);
            typedArray.recycle();
            //获取是差值  整个屏幕的三倍大小
            bouncingOffset = BouncingScreenUtils.getScreenHeight(getContext()) * 3;
            //2018年3月6日11:31:59 修复bug，设置子view铺满
            setFillViewport(true);
        }
    }


    /**
     * 从顶部开始滑动
     */
    public void startBouncingTo() {
        //设置X坐标点
        childView.setPivotX(getWidth() / 2);
        //设置Y坐标点
        childView.setPivotY(0);
        //进行缩放
        childView.setScaleY(1.0f + offsetScale);
        if (onBouncingJellyListener != null) {
            onBouncingJellyListener.onBouncingJellyTop(1.0f + offsetScale);
        }
    }

    /**
     * 从底部开始滑动
     */
    public void startBouncingBottom() {
        //设置X坐标点
        childView.setPivotX(getWidth() / 2);
        //设置Y坐标点
        childView.setPivotY(childView.getHeight());
        //开始缩放
        childView.setScaleY(1.0f + offsetScale);
        if (onBouncingJellyListener != null) {
            onBouncingJellyListener.onBouncingJellyBottom(1.0f + offsetScale);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下坐标 用于计算缩放值
                dispatchTouchDowY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mBouncingType != BouncingType.NONE) {
                    //移动的y轴
                    int moveY = (int) event.getRawY();
                    //dy值 判断方向
                    int dy = moveY - dispatchTouchDowY;

                    //y轴滚动
                    int scrollY = getScrollY();
                    //获得当前高度
                    int getHeight = getHeight();
                    //滚动范围高度
                    int computeVerticalScrollRange = computeVerticalScrollRange();

                    //顶部
                    if (dy > 0 && scrollY == 0) {
                        //判断果冻的类型
                        if (mBouncingType == BouncingType.TOP || mBouncingType == BouncingType.BOTH) {
                            //获取现在坐标与按下坐标的差值
                            int abs = moveY - dispatchTouchDowY;
                            //计算缩放值
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            isTop = true;
                            startBouncingTo();
                            return true;
                        }
                    } else if (scrollY == 0 && dy > 0 && offsetScale > 0) {//为顶部 并且dy为下拉 并且缩放值大于0
                        //这一段主要是为了防止卡顿 闪屏
                        if (mBouncingType == BouncingType.TOP || mBouncingType == BouncingType.BOTH) {
                            //获取现在坐标与按下坐标的差值
                            int abs = moveY - dispatchTouchDowY;
                            //计算缩放值
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            if (abs <= 0) {
                                offsetScale = 0;
                                dispatchTouchDowY = moveY;
                            }
                            isTop = true;
                            startBouncingTo();
                            return true;
                        }
                    }

                    //底部
                    if (dy < 0 && scrollY + getHeight >= computeVerticalScrollRange) {//滚动到底部
                        if (mBouncingType == BouncingType.BOTTOM || mBouncingType == BouncingType.BOTH) {
                            int abs = moveY - dispatchTouchDowY;
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            isTop = false;
                            startBouncingBottom();
                        }
                    } else if (dy > 0 && scrollY + getHeight >= computeVerticalScrollRange && offsetScale > 0) {
                        // 防止卡顿闪屏
                        if (mBouncingType == BouncingType.BOTTOM || mBouncingType == BouncingType.BOTH) {
                            int abs = moveY - dispatchTouchDowY;
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            if (abs >= 0) {
                                offsetScale = 0;
                                dispatchTouchDowY = moveY;
                            }
                            isTop = false;
                            startBouncingBottom();
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //回滚到初始点
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
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onInterceptTouchDownY = (int) e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) e.getRawY();
                //判断是否达到最小滚动值
                if (Math.abs(moveY - onInterceptTouchDownY) > mTouchSlop) {
                    return true;
                }
        }
        return super.onInterceptTouchEvent(e);
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
            startBouncingTo();
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
                    startBouncingTo();
                } else {//回弹到底部
                    startBouncingBottom();
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
