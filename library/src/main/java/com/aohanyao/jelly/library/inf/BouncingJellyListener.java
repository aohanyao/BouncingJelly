package com.aohanyao.jelly.library.inf;

/**
     * 弹跳的结果
     */
    public interface BouncingJellyListener {
    /**
     * 顶部弹跳
     *
     * @param bouncingJelly 弹跳系数
     */
    void onBouncingJellyTop(float bouncingJelly);

    /**
     * 底部弹跳
     *
     * @param bouncingJelly 弹跳系数
     */
    void onBouncingJellyBottom(float bouncingJelly);
    }