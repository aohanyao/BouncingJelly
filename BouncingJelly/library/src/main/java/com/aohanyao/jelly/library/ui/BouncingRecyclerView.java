package com.aohanyao.jelly.library.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;

import com.aohanyao.jelly.library.R;
import com.aohanyao.jelly.library.inf.BouncingJellyListener;
import com.aohanyao.jelly.library.util.BouncingInterpolatorType;
import com.aohanyao.jelly.library.util.BouncingType;
import com.aohanyao.jelly.library.util.ScreenUtils;
import com.nineoldandroids.view.ViewHelper;

/**
 * <p>作者：江俊超 on 2016/8/31 15:13</p>
 * <p>邮箱：928692385@qq.com</p>
 * <p></p>
 */
public class BouncingRecyclerView extends RecyclerView {
    private int dowX;
    private int dowY;
    private int moveX;
    private int moveY;
    private float bouncingOffset = 2850f;
    private float bouncingOffsetScale;
    private ValueAnimator animator;
    private boolean isTop = true;
    private boolean isTopBack;
    private boolean isBottom;
    private boolean isBottomBack;
    private String TAG = "BouncingJellyScroolView";
    private BouncingJellyListener onBouncingJellyListener;//果冻弹跳的比例数
    private TimeInterpolator mTimeInterpolator;//差值器
    private int mBouncingDuration = 300;//回弹的时间
    private int mBouncingType;

    private float offsetScale;
    private int dowX2;
    private int dowY2;
    private int scrollState;

    public BouncingRecyclerView(Context context) {
        this(context, null);
    }

    public BouncingRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BouncingRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }

    /**
     * @param attrs
     */
    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BouncingRecyclerView);
            mTimeInterpolator = BouncingInterpolatorType.getTimeInterpolator(typedArray.getInteger(
                    R.styleable.BouncingRecyclerView_BouncingInterpolator
                    , BouncingInterpolatorType.OVERSHOOT_INTERPOLATOR));
            mBouncingDuration = typedArray.getInteger(R.styleable.BouncingRecyclerView_BouncingDuration, mBouncingDuration);
            mBouncingType = typedArray.getInt(R.styleable.BouncingRecyclerView_BouncingType, BouncingType.BOTH);
            typedArray.recycle();
            if (computeVerticalScrollRange() <= ScreenUtils.getScreenHeight(getContext())) {
                mBouncingType = BouncingType.TOP;
            }
        }
    }

    /**
     * 从顶部开始滑动
     */
    public void bouncingTo() {
        if (bouncingOffsetScale < -1) {
            return;
        }
        ViewHelper.setPivotX(this, getWidth() / 2);
        ViewHelper.setPivotY(this, 0);
        ViewHelper.setScaleY(this, 1.0f + bouncingOffsetScale);
        //Log.e(TAG, "bouncingTo: " + bouncingOffsetScale);
        if (onBouncingJellyListener != null) {
            onBouncingJellyListener.onBouncingJelly(1.0f + bouncingOffsetScale);
        }
    }

    /**
     * 从顶部开始滑动
     */
    public void bouncingBottom() {
        ViewHelper.setPivotX(this, getWidth() / 2);
        ViewHelper.setPivotY(this, getHeight());
        ViewHelper.setScaleY(this, 1.0f + bouncingOffsetScale);
        if (onBouncingJellyListener != null) {
            onBouncingJellyListener.onBouncingJelly(1.0f + bouncingOffsetScale);
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        isTop = false;
        isBottom = false;
        if (!canScrollVertically(-1)) {
            isTop = true;
            //到了顶部
            //Log.e(TAG, "onScrolled: 底部");
            if (scrollState == 2) {
                backBouncing(0, 0.2f);
            }
        } else if (!canScrollVertically(1)) {
            isBottom = true;
            //  Log.e(TAG, "onScrolled: 底部");
            if (scrollState == 2) {
                backBouncing(0, 0.2f);
            }
            //滚动到底部
        } else if (dy < 0) {
            //  onScrolledUp();
        } else if (dy > 0) {
            //  onScrolledDown();
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        scrollState = state;
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "dispatchTouchEvent: ACTION_DOWN");
                dowX = (int) event.getRawX();
                dowX2 = (int) event.getRawX();
                dowY = (int) event.getRawY();
                dowY2 = (int) event.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:
                if (mBouncingType != BouncingType.NONE) {
                    moveX = (int) event.getRawX();
                    moveY = (int) event.getRawY();
                    int dy = moveY - dowY;
                    if (dy > 0 && isTop) {
                        if (mBouncingType == BouncingType.TOP || mBouncingType == BouncingType.BOTH) {
                            int abs = moveY - dowY2;
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            bouncingOffsetScale = offsetScale;
                            bouncingTo();
                            return true;
                        }
//                        Log.e(TAG, "dispatchTouchEvent: 顶部并且向上" + bouncingOffsetScale);
                    } else if (dy < 0 && bouncingOffsetScale > 0 && !isBottom && !isTop) {
                        if (mBouncingType == BouncingType.TOP || mBouncingType == BouncingType.BOTH) {
//                        Log.e(TAG, "dispatchTouchEvent: ACTION_MOVE 回移:" + bouncingOffsetScale);
                            int abs = moveY - dowY2;
                            isTopBack = true;
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            bouncingOffsetScale = offsetScale;
                            if (abs <= 0) {
                                bouncingOffsetScale = 0;
                            }
                            bouncingTo();
                            return true;
                        }
                    }
                    isTopBack = false;
                    if (dy < 0 && isBottom) {
                        if (mBouncingType == BouncingType.BOTTOM || mBouncingType == BouncingType.BOTH) {
                            int abs = moveY - dowY2;
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            bouncingOffsetScale = offsetScale;
                            bouncingBottom();
                            return true;
//                        Log.e(TAG, "dispatchTouchEvent: 底部并且向下" + bouncingOffsetScale);
                        }
                    } else if (dy > 0 && bouncingOffsetScale > 0 && !isBottom && !isTop) {
                        if (mBouncingType == BouncingType.BOTTOM || mBouncingType == BouncingType.BOTH) {
                            // Log.e(TAG, "dispatchTouchEvent: ACTION_MOVE 回移:" + bouncingOffsetScale);
                            int abs = moveY - dowY2;
                            offsetScale = (Math.abs(abs) / bouncingOffset);
                            if (offsetScale > 0.3f) {
                                offsetScale = 0.3f;
                            }
                            isBottomBack = true;
                            bouncingOffsetScale = offsetScale;
                            if (abs >= 0) {
                                bouncingOffsetScale = 0;
                            }
                            bouncingBottom();
                            return true;
                        }
                    }
                    isBottomBack = false;
                    dowY = moveY;
                }
                break;
            case MotionEvent.ACTION_UP:

                if (bouncingOffsetScale != BouncingType.NONE) {
                    if (offsetScale > 0) {
                        Log.e(TAG, "dispatchTouchEvent: ACTION_UP:" + offsetScale + "  " + getScrollY());
                        backBouncing(bouncingOffsetScale, 0);
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
            bouncingOffsetScale = 0;
            offsetScale = 0;
            if (isTop || isTopBack)
                bouncingTo();
            if (isBottom || isBottomBack)
                bouncingBottom();
        }
        if (mTimeInterpolator == null) {
            mTimeInterpolator = new OvershootInterpolator();
        }

        animator = ValueAnimator.ofFloat(from, to).setDuration(mBouncingDuration);
        animator.setInterpolator(mTimeInterpolator);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bouncingOffsetScale = (float) animation.getAnimatedValue();
                Log.e(TAG, "backBouncing: " + bouncingOffsetScale);
                if (isTop || isTopBack) {
                    bouncingTo();
                }
                if (isBottom || isBottomBack) {
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
