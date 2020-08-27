package com.kamluen.elasticsearch.service.impl;

import com.kamluen.api.vo.ResponseVO;
import com.kamluen.elasticsearch.bean.req.CommonEsInfoReqVo;
import com.kamluen.elasticsearch.cache.CacheManager;
import com.kamluen.elasticsearch.common.CombinedSearch;
import com.kamluen.elasticsearch.bean.resp.CommonInfoRespVo;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.service.CombinedSearchService;
import com.kamluen.elasticsearch.service.ElasticsearchService;
import com.kamluen.elasticsearch.utils.StringUtils;
import com.kamluen.protocol.StaticType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jca.cci.CciOperationNotSupportedException;
import org.springframework.stereotype.Service;
import com.kamluen.elasticsearch.utils.StkUtils;
import sun.awt.geom.AreaOp;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 包: com.kamluen.elasticsearch.service.impl
 * 开发者: LQW
 * 开发时间: 2019/5/16
 * 功能：综合搜索实现类
 */
@Service
public class CombinedSearchServiceImpl implements CombinedSearchService {
    private static final Logger logger = LoggerFactory.getLogger(CombinedSearchServiceImpl.class);

    @Resource
    private ElasticsearchService elasticsearchService;

    @Override
    public List<ResponseVO> combinedSelection(CommonEsInfoReqVo vo) {
        logger.info("开始执行搜索选择方法.....start....");
        ResponseVO responseVO = new ResponseVO();
        List<ResponseVO> respList = new ArrayList<>();
        //region 筛选搜索对象
        switch (vo.getType()) {
            //region step-1 查询股票信息
            case CombinedSearch.StockStatus:
                try {
                    //拿到检索参数
                    String condition = vo.getCondition();
                    String fieldName = "";
                    fieldName = judgeIsInformationOrNews(condition, fieldName, CombinedSearch.StockStatus);
                    selectElasticsearch(respList, responseVO, ElasticConstant.STOCK_INDEX, ElasticConstant.STOCK_TYPE, fieldName, condition, vo.getUserId(), vo.getRowsPerPage(), vo.getCurrentPage(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
                    responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
                    logger.error("查询股票基本信息异常：{}", e.getMessage());
                }
                break;
            //endregion
            //region step-2 查询资讯信息
            case CombinedSearch.StateInformation:
                try {
                    String condition = vo.getCondition();
                    String fieldName = "";
                    // 新闻资讯仅支持中文以及股票代码（即：数字）检索。传入中文时，匹配title属性；传入为数字时，匹配gp属性，非中文以及数字（即特殊符号等）查询全文匹配
                    fieldName = judgeIsInformationOrNews(condition, fieldName, CombinedSearch.StateInformation);
                    selectElasticsearch(respList, responseVO, ElasticConstant.NEWS_INDEX, ElasticConstant.NEWS_TYPE, fieldName, condition, vo.getUserId(), vo.getRowsPerPage(), vo.getCurrentPage(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
                    responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
                    logger.error("查询新闻资讯信息异常：{}", e.getMessage());
                }
                break;
            //endregion
            //region step-3 查询动态信息
            case CombinedSearch.DynamicCondition:
                try {
                    String condition = vo.getCondition();
                    selectElasticsearch(respList, responseVO, ElasticConstant.DYNAMIC_INDEX, ElasticConstant.DYNAMIC_TYPE, "", condition, vo.getUserId(), vo.getRowsPerPage(), vo.getCurrentPage(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
                    responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
                    logger.error("查询动态信息异常：{}", e.getMessage());
                }
                break;
            //endregion
            //region step-4 查询用户信息
            case CombinedSearch.UserStatus:
                try {
                    //拿到检索参数
                    String condition = vo.getCondition();
                    String fieldName = "";
                    //手机号
                    if (StringUtils.isPhoneNumber(condition)) {
                        fieldName = ElasticConstant.USER_PHONE;
                    }
                    selectElasticsearch(respList, responseVO, ElasticConstant.USER_INDEX, ElasticConstant.USER_TYPE, fieldName, condition, vo.getUserId(), vo.getRowsPerPage(), vo.getCurrentPage(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
                    responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
                    logger.error("查询用户信息数据异常：{}", e.getMessage());
                }
                break;
            //endregion
            //region step5 查询策略信息
            case CombinedSearch.Strategy_Type:
                try {
                    String condition = vo.getCondition();
                    selectElasticsearch(respList, responseVO, ElasticConstant.STRATEGY_INDEX, ElasticConstant.STRATEGY_TYPE, "", condition, vo.getUserId(), vo.getRowsPerPage(), vo.getCurrentPage(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
                    responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
                    logger.error("查询策略信息异常：{}", e.getMessage());
                }
                break;
            //endregion
            //region  step-6 查询所有信息
            default:
                Integer status = 0;
                //ES中需要检索的字段名
                String fieldName = "";
                //加入索引  股票->资讯->动态->用户
                String[] strings = {ElasticConstant.STOCK_INDEX, ElasticConstant.NEWS_INDEX, ElasticConstant.DYNAMIC_INDEX, ElasticConstant.USER_INDEX, ElasticConstant.STRATEGY_INDEX};
                //ES中的type名称
                String typeName = "";
                for (String indexName : strings) {
                    // 选择在ES中查询的方向
                    switch (indexName) {
                        case ElasticConstant.NEWS_INDEX:
                            typeName = ElasticConstant.NEWS_TYPE;
                            //资讯
                            status = CombinedSearch.StateInformation;
                            fieldName = judgeIsInformationOrNews(vo.getCondition(), fieldName, status);
                            break;
                        case ElasticConstant.STOCK_INDEX:
                            typeName = ElasticConstant.STOCK_TYPE;
                            // 股票
                            status = CombinedSearch.StockStatus;
                            fieldName = judgeIsInformationOrNews(vo.getCondition(), fieldName, status);
                            break;
                        case ElasticConstant.DYNAMIC_INDEX:
                            //重置检索字段
                            fieldName = "";
                            typeName = ElasticConstant.DYNAMIC_TYPE;
                            break;
                        case ElasticConstant.USER_INDEX:
                            typeName = ElasticConstant.USER_TYPE;
                            if (StringUtils.isPhoneNumber(vo.getCondition())) {
                                fieldName = ElasticConstant.USER_PHONE;
                            }
                            break;
                        case ElasticConstant.STRATEGY_INDEX:
                            typeName = ElasticConstant.STRATEGY_TYPE;
                            break;
                    }
                    selectElasticsearch(respList, responseVO, indexName, typeName, fieldName, vo.getCondition(), vo.getUserId(), vo.getRowsPerPage(), vo.getCurrentPage(), false);
                }
                //endregion
        }
        //endregion
        return respList;
    }

    /**
     * 选择ElasticSearch
     *
     * @param respList     返回结果集
     * @param responseVO   返回结果
     * @param index        ES索引名
     * @param type         ES类型名
     * @param fieldName    ES中要检索的字段名
     * @param condition    查询检索条件
     * @param userIds      当前查询的用户id
     * @param pageNumber   每页显示的数据条数
     * @param currentPage  当前页
     * @param isExactMatch 是否分词查询
     */
    private void selectElasticsearch(List<ResponseVO> respList, ResponseVO responseVO, String index, String type, String fieldName, String condition, Integer userIds, Integer pageNumber, Integer currentPage, boolean isExactMatch) {
        //region 将当前搜索用户id加入到缓存
        CacheManager.putCacheContent("userIds", userIds, System.currentTimeMillis());
        //endregion
        List<Map<String, Object>> list = elasticsearchService.selectElasticsearch(index, type, fieldName, condition, pageNumber, currentPage, isExactMatch);
        responseVO = new ResponseVO();
        if (list != null && list.size() > 0) {
            CommonInfoRespVo respVo = new CommonInfoRespVo();
            if (index.equals(ElasticConstant.NEWS_INDEX)) {
                respVo.setObjectType(CombinedSearch.StateInformation);
            } else if (index.equals(ElasticConstant.STOCK_INDEX)) {
                respVo.setObjectType(CombinedSearch.StockStatus);
            } else if (index.equals(ElasticConstant.DYNAMIC_INDEX)) {
                respVo.setObjectType(CombinedSearch.DynamicCondition);
            } else if (index.equals(ElasticConstant.STRATEGY_INDEX)) {
                respVo.setObjectType(CombinedSearch.Strategy_Type);
            } else {
                respVo.setObjectType(CombinedSearch.UserStatus);
            }
            respVo.setResultInfo(list);
            responseVO.setResult(respVo);
            respList.add(responseVO);
        }
    }


    /**
     * 判断是新闻还是资讯
     *
     * @param condition 字段对应的值(该值为部分数据，支持模糊查询)
     * @param fieldName 要检索的字段名
     * @param status    状态 (1 : 股票, 2 : 资讯)
     */
    private String judgeIsInformationOrNews(String condition, String fieldName, Integer status) {
        String type = StkUtils.judgeCondition(condition);
        //region 选择股票或资讯
        switch (status) {
            //股票
            case CombinedSearch.StockStatus:
                fieldName = judgeTheFieldType(type, fieldName, ElasticConstant.STK_NAME, ElasticConstant.SPELLING, ElasticConstant.STK_CODE);
                break;
            //资讯
            case CombinedSearch.StateInformation:
                fieldName = judgeTheFieldType(type, fieldName, ElasticConstant.TITLE, ElasticConstant.SPELL, ElasticConstant.GP);
                break;
        }
        //endregion
        return fieldName;
    }

    /**
     * 判断ES中要检索的字段的类型
     *
     * @param type      字段的类型
     * @param fieldName ES中要检索的字段
     */
    private String judgeTheFieldType(String type, String fieldName, String stkName, String spelling, String stkCode) {
        //region 选择中文 、字母 、数字
        switch (type) {
            // 表示传入值为中文
            case "0":
                fieldName = stkName;
                break;
            // 表示传入值为字母
            case "1":
                fieldName = spelling;
                break;
            // 表示传入值为数字
            case "2":
                fieldName = stkCode;
                break;
        }
        return fieldName;
//        endregion
    }
}
