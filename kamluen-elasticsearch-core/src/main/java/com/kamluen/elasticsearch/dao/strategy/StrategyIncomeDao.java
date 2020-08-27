package com.kamluen.elasticsearch.dao.strategy;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kamluen.elasticsearch.entity.StrategyIncome;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Package: com.kamluen.elasticsearch.dao.strategy
 * @Author: LQW
 * @Date: 2019/10/21
 * @Description:策略收益数据交互接口
 */
public interface StrategyIncomeDao extends BaseMapper<StrategyIncome> {
    @Select("SELECT * FROM (SELECT * FROM strategy.user_strategy_income WHERE strategy_id = #{strategyId}  AND income_type = #{strategyStatus}  GROUP BY date ORDER BY date,create_time DESC LIMIT #{limitNumber} ) t ORDER BY date ASC")
    List<StrategyIncome> queryStrategyOverviewLine(@Param("strategyId") Integer strategyId, @Param("strategyStatus") Integer strategyStatus, @Param("limitNumber") Integer limitNumber);
}
