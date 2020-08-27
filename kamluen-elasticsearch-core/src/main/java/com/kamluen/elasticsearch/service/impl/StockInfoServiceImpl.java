package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.api.vo.ResponseVO;
import com.kamluen.elasticsearch.bean.resp.EsStockInfoRespVo;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.dao.mktinfo.StockInfoDao;
import com.kamluen.elasticsearch.entity.StockInfo;
import com.kamluen.elasticsearch.service.ElasticsearchService;
import com.kamluen.elasticsearch.service.mktinfo.StockInfoService;
import com.kamluen.elasticsearch.utils.StkUtils;
import com.kamluen.protocol.StaticType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhanglei
 * 股票基本信息服务接口实现类
 * @date 2018-11-01
 */
@Service
public class StockInfoServiceImpl extends ServiceImpl<StockInfoDao, StockInfo> implements StockInfoService {

    private static final Logger logger = LoggerFactory.getLogger(NewsInfoServiceImpl.class);

    @Resource
    private ElasticsearchService elasticsearchService;

    @Override
    public ResponseVO selectStockInfoByEs(String value, Integer rowsPerPage, Integer currentPage) {
        ResponseVO responseVO = new ResponseVO();
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String fieldName = "";
            String type = StkUtils.judgeCondition(value);
            if(type.equals("0")){   // 表示传入值为中文
                fieldName = ElasticConstant.STK_NAME;
            } else if (type.equals("1")){   // 表示传入值为字母
                fieldName = ElasticConstant.SPELLING;
            } else if (type.equals("2")){   // 表示传入值为数字
                fieldName = ElasticConstant.STK_CODE;
            } else {   // 表示传入值为组合字符，例如: 00700.HK

            }
            list = elasticsearchService.selectElasticsearch(ElasticConstant.STOCK_INDEX, ElasticConstant.STOCK_TYPE, fieldName, value, rowsPerPage, currentPage, false);
            if (list != null && list.size() > 0) {
                EsStockInfoRespVo respVo = new EsStockInfoRespVo();
                respVo.setResultInfo(list);
                responseVO.setResult(respVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
            responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
            logger.error("查询股票基本信息异常：{}", e.getMessage());
        }
        return responseVO;
    }
}
