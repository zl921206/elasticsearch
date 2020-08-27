package com.kamluen.elasticsearch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Package: com.kamluen.elasticsearch.entity
 * @Author: LQW
 * @Date: 2019/10/16
 * @Description:策略收益记录
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("strategy.user_strategy_income")
public class StrategyIncome extends Model<StrategyIncome> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "income_id", type = IdType.AUTO)
    private Integer incomeId;
    /**
     * 关联策略ID
     */
    @TableField("strategy_id")
    private Integer strategyId;
    /**
     * 回测，模拟，实盘(1,2,3)
     */
    @TableField("income_type")
    private Integer incomeType;
    /**
     * 累计收益
     */
    @TableField("accumulate_income")
    private BigDecimal accumulateIncome;

    //收益率就是 今天的收盘-昨天的收盘价/昨天的收盘价

    /**
     * 当日基准数据（恒指）
     */
    @TableField("day_base")
    private BigDecimal dayBase;
    /**
     * 当日标的走势（本身）
     */
    @TableField("day_tendency")
    private BigDecimal dayTendency;
    /**
     * 当日收益
     */
    @TableField("day_income")
    private BigDecimal dayIncome;
    /**
     * 当前持仓
     */
    @TableField("hold_posi")
    private BigDecimal holdPosi;
    /**
     * 剩余资金
     */
    @TableField("surplus_funds")
    private BigDecimal surplusFunds;
    /**
     * 推送时间
     */
    @TableField("date")
    private String date;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;


    @Override
    protected Serializable pkVal() {
        return this.incomeId;
    }
}
