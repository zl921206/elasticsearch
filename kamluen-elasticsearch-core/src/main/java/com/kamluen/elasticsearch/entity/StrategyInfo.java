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
 * @Description:策略信息
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("strategy.user_strategy")
public class StrategyInfo extends Model<StrategyInfo> {

    @TableId(value = "strategy_id", type = IdType.AUTO)
    private Integer strategyId;
    /**
     * 策略类型：1日内交易（1天）2短期（1-15天）3中期（15-90天）4长期（90天以上）
     */
    @TableField("strategy_type")
    private Integer strategyType;
    /**
     * 投资周期(-1不限周期、1：一个周期、2：两个周期...)
     */
    @TableField("strategy_cycle")
    private Integer strategyCycle;
    /**
     * 策略名称
     */
    @TableField("strategy_name")
    private String strategyName;
    /**
     * 策略说明
     */
    @TableField("strategy_explain")
    private String strategyExplain = "";
    /**
     * 佣金费率
     */
    @TableField("strategy_rate")
    private BigDecimal strategyRate;
    /**
     * 策略权限：0私有；1公开
     */
    @TableField("strategy_role")
    private Integer strategyRole;
    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;
    /**
     * 股票代码
     */
    @TableField("stock_code")
    private String stockCode;
    /**
     * 选股策略id
     */
    @TableField("selection_id")
    private Integer selectionId;
    /**
     * 交易策略id
     */
    @TableField("trade_id")
    private Integer tradeId;
    /**
     * 风控策略id
     */
    @TableField("risk_id")
    private Integer riskId;
    /**
     * 是否自动使用羊毛支付
     */
    @TableField("auto_use_fleece")
    private Integer autoUseFleece;
    /**
     * 策略启动时间
     */
    @TableField("start_time")
    private String startTime;
    /**
     * 策略状态：未启用、启用、结束、删除（-2, -1 , 1 , 0）
     */
    @TableField("status")
    private Integer status;
    /**
     * 回测状态：未回测、回测中、回测完成（-1,0,1）
     */
    @TableField("test_status")
    private Integer testStatus;
    /**
     * 发布状态：未发布、已发布、审核中（0,1,2）
     */
    @TableField("publish_status")
    private Integer publishStatus;
    /**
     * 发布时间
     */
    @TableField("publish_time")
    private Date publishTime = new Date();
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 模拟状态：未模拟、模拟中、模拟关闭（-1,0,1）
     */
    @TableField("simulation_status")
    private Integer simulationStatus;
    /**
     * 跟单的策略id
     */
    @TableField("from_strategy_id")
    private Integer fromStrategyId;

    @Override
    protected Serializable pkVal() {
        return strategyId;
    }
}
