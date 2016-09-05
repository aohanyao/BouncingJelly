package com.aohanyao.jelly.library.util;

import android.animation.TimeInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * <p>作者：江俊超 on 2016/8/31 09:36</p>
 * <p>邮箱：928692385@qq.com</p>
 * <p>回弹差值器</p>
 */
public class BouncingInterpolatorType {

    public static final int OVERSHOOT_INTERPOLATOR = 1;
    public static final int BOUNCE_INTERPOLATOR = 2;
    public static final int LINEAR_INTERPOLATOR = 3;
    public static final int ACCELERATE_DECELERATE_INTERPOLATOR = 4;

    /**
     * 获取弹跳类型
     *
     * @return
     */
    public static TimeInterpolator getTimeInterpolator(int type) {
        TimeInterpolator mTimeInterpolator = null;
        switch (type) {
            case OVERSHOOT_INTERPOLATOR:
                mTimeInterpolator = new OvershootInterpolator();
                break;
            case BOUNCE_INTERPOLATOR:
                mTimeInterpolator = new BounceInterpolator();
                break;
            case LINEAR_INTERPOLATOR:
                mTimeInterpolator = new LinearInterpolator();
                break;
            case ACCELERATE_DECELERATE_INTERPOLATOR:
                mTimeInterpolator = new AccelerateDecelerateInterpolator();
                break;
        }
        return mTimeInterpolator;
    }
}
