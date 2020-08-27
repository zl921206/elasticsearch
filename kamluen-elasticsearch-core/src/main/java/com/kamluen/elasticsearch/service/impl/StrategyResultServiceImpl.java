package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.elasticsearch.dao.strategy.StrategyResultDao;
import com.kamluen.elasticsearch.entity.StrategyResult;
import com.kamluen.elasticsearch.service.strategy.StrategyResultService;
import org.springframework.stereotype.Service;

/**
 * @Package: com.kamluen.elasticsearch.service.impl
 * @Author: LQW
 * @Date: 2019/10/22
 * @Description:策略结果服务接口实现类
 */
@Service
public class StrategyResultServiceImpl extends ServiceImpl<StrategyResultDao,StrategyResult> implements StrategyResultService {
}
