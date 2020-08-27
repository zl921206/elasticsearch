package com.kamluen.elasticsearch.common;

import java.io.Serializable;

/**
 * 包: com.kamluen.elasticsearch.common
 * 开发者: LQW
 * 开发时间: 2019/5/16
 * 功能：
 */
public abstract class CombinedSearch implements Serializable {

    /**
     *  股票
     */
    public static final int StockStatus = 1;
    /**
     *  资讯
     */
    public static final int StateInformation = 2;
    /**
     * 动态
     */
    public static final int DynamicCondition = 3;
    /**
     *  用户
     */
    public static final int UserStatus = 4;
    /**
     * 策略类型
     */
    public static final int Strategy_Type = 5;

}
