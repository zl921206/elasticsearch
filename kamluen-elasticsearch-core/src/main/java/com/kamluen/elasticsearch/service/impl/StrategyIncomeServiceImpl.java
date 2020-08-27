package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.elasticsearch.dao.strategy.StrategyIncomeDao;
import com.kamluen.elasticsearch.entity.StrategyIncome;
import com.kamluen.elasticsearch.service.strategy.StrategyIncomeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Package: com.kamluen.elasticsearch.service.impl
 * @Author: LQW
 * @Date: 2019/10/21
 * @Description:策略收益服务接口实现类
 */
@Service
public class StrategyIncomeServiceImpl extends ServiceImpl<StrategyIncomeDao, StrategyIncome> implements StrategyIncomeService {

    @Resource
    private StrategyIncomeDao strategyIncomeDao;

    @Override
    public List<StrategyIncome> queryStrategyOverviewLine(Integer strategyId, Integer strategyStatus, Integer limitNumber) {
        return strategyIncomeDao.queryStrategyOverviewLine(strategyId, strategyStatus, limitNumber);
    }
}
