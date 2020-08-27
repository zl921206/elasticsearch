package com.kamluen.elasticsearch.entity.ElasticsearchVO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @Package: com.kamluen.elasticsearch.entity.ElasticsearchVO
 * @Author: LQW
 * @Date: 2019/10/16
 * @Description:策略信息ES检索类
 */
@Data
public class EsStrategyInfo {

    private Integer fromStrategyId;

    /**
     * 策略id
     */
    private Integer strategyId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 投资周期(-1不限周期、1：一个周期、2：两个周期...)
     */
    private Integer strategyCycle;

    /**
     * 策略权限：0私有；1公开
     */
    private Integer strategyRole;

    /**
     * 发布状态：未发布、已发布、审核中（0,1,2）
     */
    private Integer publishStatus;

    /**
     * 策略名称
     */
    private String strategyName;

    /**
     * 策略简介
     */
    private String strategyExplain;

    /**
     * 用户图标/头像
     */
    private String userIcon;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 是否跟单 0 : 未跟单 1 : 跟单
     */
    private Integer followed;

    /**
     * 策略类型：1日内交易（1天）2短期（1-15天）3中期（15-90天）4长期（90天以上）
     */
    private Integer strategyType;

    /**
     * 回测状态：未回测、回测中、回测完成（-1,0,1）
     */
    private Integer testStatus;

    /**
     * 策略状态：未启用、启用、结束、删除（-2, -1 , 1 , 0）
     */
    private Integer status;

    /**
     * 策略发布时间
     */
    private Date publishTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
