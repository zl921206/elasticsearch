package com.kamluen.elasticsearch.service.strategy;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kamluen.api.mktinfo.vo.QuotationVO;
import com.kamluen.elasticsearch.entity.StrategyInfo;

import java.util.List;
import java.util.Map;

/**
 * @Package: com.kamluen.elasticsearch.service.strategy
 * @Author: LQW
 * @Date: 2019/10/16
 * @Description:策略信息服务接口
 */
public interface StrategyInfoService extends IService<StrategyInfo> {
    /**
     * 查询当前策略的互动信息
     *
     * @param strategyId 策略id
     * @param userId     用户id
     * @return HashMap
     */
    Map<String, Object> queryInteractiveInfoAboutTheCurrentStrategy(Integer strategyId, Integer userId);

    /**
     *  获取单个对象
     * @param strategyId    策略id
     * @return  StrategyInfo
     */
    StrategyInfo getOne(Integer strategyId);

    /**
     *  是否跟单
     * @param userId        用户id
     * @param strategyId    策略id
     * @return  1 : true 0 : false
     */
    Integer whetherTrackOrder(Integer userId,Integer strategyId);

    /**
     *  获取股票信息
     * @param stockCode 股票代码
     * @return  QuotationVO
     */
    QuotationVO queryStockDataFromRedis(String stockCode);
}
