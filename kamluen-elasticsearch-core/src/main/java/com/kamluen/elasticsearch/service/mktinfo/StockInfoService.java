package com.kamluen.elasticsearch.service.mktinfo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kamluen.api.vo.ResponseVO;
import com.kamluen.elasticsearch.entity.StockInfo;

/**
 * @author zhanglei
 * 股票基本信息服务接口
 * @date 2018-11-01
 */
public interface StockInfoService extends IService<StockInfo> {

    /**
     * 从ES中查询股票基本信息数据
     * @return
     */
    ResponseVO selectStockInfoByEs(String stockCode, Integer rowsPerPage, Integer currentPage);
}
