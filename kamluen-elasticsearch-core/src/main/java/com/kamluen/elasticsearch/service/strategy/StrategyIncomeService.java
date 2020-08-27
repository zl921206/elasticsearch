package com.kamluen.elasticsearch.service.strategy;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kamluen.elasticsearch.entity.StrategyIncome;

import java.util.List;

/**
 * @Package: com.kamluen.elasticsearch.service.strategy
 * @Author: LQW
 * @Date: 2019/10/21
 * @Description:策略收益服务接口
 */
public interface StrategyIncomeService extends IService<StrategyIncome> {

    List<StrategyIncome> queryStrategyOverviewLine(Integer strategyId, Integer strategyStatus,Integer limitNumber);
}
