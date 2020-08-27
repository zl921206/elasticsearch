package com.kamluen.elasticsearch.common;

import com.kamluen.elasticsearch.entity.StockInfoVO;
import com.kamluen.elasticsearch.entity.StrategyIncome;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Package: com.kamluen.elasticsearch.bean.resp
 * @Author: LQW
 * @Date: 2019/10/24
 * @Description:策略信息返回结果集
 */
@Data
public class UserStrategyListRespVO implements Serializable {

    private Integer strategyId;

    private Integer fromStrategyId;
    /**
     * 策略类型：1日内交易（1天）2短期（1-15天）3中期（15-90天）4长期（90天以上）
     */
    private Integer strategyType;
    /**
     * 投资周期
     * -1不限周期、
     * 1：一个周期、
     * 2：两个周期
     * ...
     */
    private Integer strategyCycle;
    /**
     * 策略名称
     */
    private String strategyName;
    /**
     * 策略说明
     */
    private String strategyExplain;

    /**
     * 策略权限：0私有；1公开
     */
    private Integer strategyRole;
    private Integer userId;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 策略状态：未启用、启用、结束（-1,1,0）
     */
    private Integer status;
    /**
     * 发布状态：未发布、已发布、审核中（0,1,2）
     */
    private Integer publishStatus;
    /**
     * 发布时间
     */
    private Long publishTime;
    private Long createTime;

    /**
     * 策略概要收益曲线
     */
    private List<StrategyIncome> userStrategyIncomes;

    /**
     * 股票信息
     */
    private StockInfoVO stockInfo;

    /**
     * 累计收益 -16.70%
     */
    private String accumulateIncome;

    /**
     * 年化收益
     */
    private String annualIncome;

    /**
     * 运行天数
     */
    private Integer days;

    /**
     * 是否跟单
     */
    private Boolean followed;

    /**
     * 回测状态：未回测、回测中、回测完成（-1,0,1）
     */
    private Integer testStatus;

}
