package com.kamluen.elasticsearch.entity;

import com.alibaba.fastjson.JSONObject;
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
import java.util.Date;

/**
 * @Package: com.kamluen.elasticsearch.entity
 * @Author: LQW
 * @Date: 2019/10/16
 * @Description:策略结果记录
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("strategy.user_strategy_result")
public class StrategyResult extends Model<StrategyResult> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "result_id", type = IdType.AUTO)
    private Integer resultId;
    /**
     * 关联策略ID
     */
    @TableField("strategy_id")
    private Integer strategyId;
    /**
     * 回测，模拟，实盘(1,2,3)
     */
    @TableField("result_type")
    private Integer resultType;
    /**
     * 股票代码
     */
    @TableField("stock_code")
    private String stockCode;
    /**
     * 总资产
     */
    @TableField("total_funds")
    private String totalFunds;
    /**
     * 可用资金
     */
    @TableField("available_funds")
    private String availableFunds;
    /**
     * 年化收益
     */
    @TableField("annual_income")
    private String annualIncome;
    /**
     * 累计收益
     */
    @TableField("accumulate_income")
    private String accumulateIncome;
    /**
     * 运行天数
     */
    @TableField("days")
    private Integer days;
    /**
     * 最大回撤
     */
    @TableField("max_retracement")
    private String maxRetracement;
    /**
     * 阿尔法
     */
    @TableField("alpha")
    private String alpha;
    /**
     * 贝塔
     */
    @TableField("beta")
    private String beta;
    /**
     * 投资效率
     */
    @TableField("efficiency")
    private String efficiency;
    /**
     * 稳定性
     */
    @TableField("stability")
    private String stability;
    /**
     * 抗风险
     */
    @TableField("anti_risk")
    private String antiRisk;
    /**
     * 收益能力
     */
    @TableField("profitability")
    private String profitability;
    /**
     * 手续费
     */
    @TableField("fee")
    private String fee;
    /**
     * 佣金
     */
    @TableField("commission")
    private String commission;
    /**
     * 印花税
     */
    @TableField("stamp_duty")
    private String stampDuty;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;


    @Override
    protected Serializable pkVal() {
        return this.resultId;
    }
}
