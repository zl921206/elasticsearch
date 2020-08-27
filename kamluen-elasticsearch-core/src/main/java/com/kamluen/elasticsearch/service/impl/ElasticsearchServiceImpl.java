package com.kamluen.elasticsearch.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import com.kamluen.api.mktinfo.vo.QuotationVO;
import com.kamluen.api.mktserver.HkexTsData;
import com.kamluen.api.mktserver.ShTsData;
import com.kamluen.api.mktserver.SzTsData;
import com.kamluen.api.mktserver.UsTsData;
import com.kamluen.api.ptf.vo.enums.ENoteType;
import com.kamluen.api.ptf.vo.resp.PtfNoteIndexRespVo;
import com.kamluen.common.bean.ResultMessage;
import com.kamluen.common.utils.JSONUtil;
import com.kamluen.elasticsearch.bean.req.EsSearchInfoReqVo;
import com.kamluen.elasticsearch.bean.resp.HotSearchVO;
import com.kamluen.elasticsearch.cache.Cache;
import com.kamluen.elasticsearch.cache.CacheManager;
import com.kamluen.elasticsearch.common.UserStrategyListRespVO;
import com.kamluen.elasticsearch.entity.*;
import com.kamluen.elasticsearch.entity.ElasticsearchVO.EsNewsInfo;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.entity.ElasticsearchVO.EsPtfNoteInfo;
import com.kamluen.elasticsearch.enums.MktTypeEnums;
import com.kamluen.elasticsearch.enums.NewsInteractionEnum;
import com.kamluen.elasticsearch.enums.PtfNoteInteractionEnum;
import com.kamluen.elasticsearch.enums.StrategyEnum;
import com.kamluen.elasticsearch.service.*;
import com.kamluen.elasticsearch.service.kamluen.*;
import com.kamluen.elasticsearch.service.strategy.StrategyIncomeService;
import com.kamluen.elasticsearch.service.strategy.StrategyInfoService;
import com.kamluen.elasticsearch.service.strategy.StrategyResultService;
import com.kamluen.elasticsearch.utils.DateUtils;
import com.kamluen.elasticsearch.utils.JsonUtils;
import com.kamluen.elasticsearch.utils.PinYinUtils;
import com.kamluen.elasticsearch.utils.StringUtils;
import com.kamluen.odps.service.RedisMapService;
import com.kamluen.security.SecurityKey;
import com.kamluen.security.util.IDTransUtil;
import com.kamluen2.common.message.MessageProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Elasticsearch 相关操作，公共服务实现类
 */
@Service(application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}",
        version = "${dubbo.version}")
@Component
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${ip.path}")
    private String ipPath;

    @Resource
    MessageProducer producer;

    @Resource
    private RedisMapService redisMapService;

    @Resource
    private PtfNoteInteractionService ptfNoteInteractionService;
    @Resource
    private PtfFavService ptfFavService;

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private UserInfoBOService userInfoBOService;
    @Resource
    private NewsInteractionService newsInteractionService;
    @Resource
    private NewsRecordNumService newsRecordNumService;
    @Resource
    private StrategyIncomeService strategyIncomeService;
    @Resource
    private StrategyResultService strategyResultService;
    @Resource
    private StrategyInfoService strategyInfoService;

    /**
     * 生产者标题
     */
    @Value("${apache.kafka.producer.producerTopic}")
    private String producerTopic;


    @Override
    public Integer getCount(String indexName, String indexType, String fieldName, String value, boolean isExactMatch) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.from(0);
//      searchSourceBuilder.size(rowsPerPage);
        boolean conditionQuery = false;
        if (StringUtils.isNEmpty(value)) {
            conditionQuery = true;
        }
        // 给传入值匹配通配符
        value = ElasticConstant.WILDCARD + value + ElasticConstant.WILDCARD;
        // 新闻资讯信息检索
        if (indexName.equals(ElasticConstant.NEWS_INDEX)) {
            // 传入参数为数字，则匹配字段名gp；传入参数为中文，则匹配字段名title；传入参数为英文，则匹配字段名spell；匹配方式：通配符匹配
            if (fieldName.equals(ElasticConstant.GP)) {
                if (isExactMatch) {
                    searchSourceBuilder.query(QueryBuilders.queryStringQuery(value));
                } else {
                    searchSourceBuilder.query(QueryBuilders.wildcardQuery(fieldName, value));
                }
            } else if (fieldName.equals(ElasticConstant.TITLE)) {
                searchSourceBuilder.query(QueryBuilders.matchQuery(fieldName, value));
            } else if (fieldName.equals(ElasticConstant.LABEL_NEWS_ID)) {
                searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            } else {
                // 传入参数为特殊符号时，则对所有字段进行分词查询
                searchSourceBuilder.query(QueryBuilders.queryStringQuery(value));
            }
            // 股票基本信息检索
        } else if (indexName.equals(ElasticConstant.STOCK_INDEX)) {
            // 传入参数为数字，则匹配字段名stkCode；传入参数为中文，则匹配字段名stkName；匹配方式：通配符匹配
            if (fieldName.equals(ElasticConstant.STK_CODE)) {
                searchSourceBuilder.query(QueryBuilders.wildcardQuery(fieldName, value));
            } else {
                // 传入参数为非数字时（也就是字母和特殊符号等），则对所有字段进行分词查询
                searchSourceBuilder.query(QueryBuilders.queryStringQuery(value));
            }
            // 热搜榜信息检索
        } else if (indexName.equals(ElasticConstant.SEARCH_INDEX)) {
            // 根据更新时间范围查询
//            searchSourceBuilder.query(QueryBuilders.rangeQuery(ElasticConstant.UPDATE_TIME).gt(DateUtils.getTwoHoursBeforeTimeStamp())).sort(ElasticConstant.COUNT, SortOrder.DESC);
            if (conditionQuery) {
                if (isExactMatch) {
                    searchSourceBuilder.query(QueryBuilders.queryStringQuery(value)).sort(ElasticConstant.COUNT, SortOrder.DESC);
                } else {
                    searchSourceBuilder.query(QueryBuilders.wildcardQuery(fieldName, value)).sort(ElasticConstant.COUNT, SortOrder.DESC);
                }
            } else {
                searchSourceBuilder.query(QueryBuilders.matchAllQuery()).sort(ElasticConstant.COUNT, SortOrder.DESC);
            }
        }


        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(indexType);
        searchRequest.source(searchSourceBuilder);
        long tatalRow = 0;
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits searchHits = searchResponse.getHits();
            if (searchResponse != null && searchResponse.getHits() != null) {
                logger.info("当前匹配到数据size：" + searchResponse.getHits().totalHits);
                tatalRow = searchResponse.getHits().totalHits;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (int) tatalRow;

    }

    @Override
    public List<Map<String, Object>> selectElasticsearch(String indexName, String indexType, String fieldName, String value, Integer rowsPerPage, Integer currentPage, boolean isExactMatch) {
        //region 从缓存中获取sessionId
        Cache cache = CacheManager.getCacheContent("userIds");
        Integer userIds = 0;
        if (null != cache) {
            userIds = (Integer) cache.getValue();
        }
        //endregion
        logger.info("开始执行selectElasticsearch方法...start...");
        logger.info("进入selectElasticsearch方法获取到的当前搜索用户id为: {}", userIds);
        List<Map<String, Object>> list = new ArrayList<>();
        //region ES查询的准备工作
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (currentPage == 0) {
            currentPage = 1;
        }
        searchSourceBuilder.from((currentPage - 1) * rowsPerPage);
        searchSourceBuilder.size(rowsPerPage);
        //endregion
        // 给传入值匹配通配符
        value = ElasticConstant.WILDCARD + value + ElasticConstant.WILDCARD;
        //region 新闻资讯信息检索
        if (indexName.equals(ElasticConstant.NEWS_INDEX)) {
            // 传入参数为数字，则匹配字段名gp；传入参数为中文，则匹配字段名title；传入参数为英文，则匹配字段名spell；匹配方式：通配符匹配
            if (fieldName.equals(ElasticConstant.GP)) {
                if (isExactMatch) {
                    searchSourceBuilder.query(QueryBuilders.queryStringQuery(value)).sort(ElasticConstant.ISSUE_TIME, SortOrder.DESC);
                } else {
                    searchSourceBuilder.query(QueryBuilders.wildcardQuery(fieldName, value)).sort(ElasticConstant.ISSUE_TIME, SortOrder.DESC);
                }
            } else if (fieldName.equals(ElasticConstant.TITLE)) {
                searchSourceBuilder.query(QueryBuilders.matchQuery(fieldName, value)).sort(ElasticConstant.ISSUE_TIME, SortOrder.DESC);
            } else if (fieldName.equals(ElasticConstant.LABEL_NEWS_ID)) {
                searchSourceBuilder.query(QueryBuilders.matchAllQuery()).sort(ElasticConstant.LABEL_NEWS_ID, SortOrder.DESC);
            } else {
                // 传入参数为特殊符号时，则对所有字段进行分词查询
                searchSourceBuilder.query(QueryBuilders.queryStringQuery(value)).sort(ElasticConstant.ISSUE_TIME, SortOrder.DESC);
            }
            // 股票基本信息检索
        }
        //endregion
        //region 股票信息检索
        else if (indexName.equals(ElasticConstant.STOCK_INDEX)) {
            // 传入参数为数字，则匹配字段名stkCode；传入参数为中文，则匹配字段名stkName；匹配方式：通配符匹配
            if (fieldName.equals(ElasticConstant.STK_CODE)) {
                searchSourceBuilder.query(QueryBuilders.wildcardQuery(fieldName, value)).sort(ElasticConstant.MKT_TYPE, SortOrder.ASC).sort(ElasticConstant.SEC_TYPE, SortOrder.ASC);
            } else if (fieldName.equals(ElasticConstant.SPELLING)) {
                /**
                 * 此处精确匹配时，将小写字母转换为大写，是为了精确匹配美股相关股票（美股股票代码均为大写）
                 * 模糊匹配时，大小写同步匹配
                 */
                String convertValue = value.replace(ElasticConstant.WILDCARD, "") + ".US";
                searchSourceBuilder.query(QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery(ElasticConstant.ASSET_ID, PinYinUtils.convertLowToUp(convertValue))).should(QueryBuilders.queryStringQuery(PinYinUtils.convertLowToUp(value))).should(QueryBuilders.queryStringQuery(PinYinUtils.convertUpToLow(value))));
            } else {
                // 传入参数为非数字时（也就是字母和特殊符号等），则对所有字段进行分词查询
//                searchSourceBuilder.query(QueryBuilders.queryStringQuery(value)).sort(ElasticConstant.SEC_TYPE, SortOrder.ASC);
                /**
                 * 中文匹配
                 */
                searchSourceBuilder.query(QueryBuilders.matchQuery(fieldName, value));
            }
            // 热搜榜信息检索
        }
        //endregion
        //region 热搜信息检索
        else if (indexName.equals(ElasticConstant.SEARCH_INDEX)) {
            // 根据更新时间范围查询
//            searchSourceBuilder.query(QueryBuilders.rangeQuery(ElasticConstant.UPDATE_TIME).gt(DateUtils.getTwoHoursBeforeTimeStamp())).sort(ElasticConstant.COUNT, SortOrder.DESC);
            searchSourceBuilder.query(QueryBuilders.matchAllQuery()).sort(ElasticConstant.COUNT, SortOrder.DESC);

        }
        //endregion
        //region 动态信息检索
        else if (indexName.equals(ElasticConstant.DYNAMIC_INDEX)) {
            if (!StringUtils.isNotNull(fieldName)) {
                //根据 NOTE_TITLE , DYNAMIC_TITLE, CONTENT 检索
                if (StringUtils.isChineseChar(value)) {
                    searchSourceBuilder.query(
                            QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery(ElasticConstant.NOTE_TITLE, value))
                                    .should(QueryBuilders.matchPhraseQuery(ElasticConstant.DYNAMIC_TITLE, value))
                                    .should(QueryBuilders.matchPhraseQuery(ElasticConstant.CONTENT, value))
                    )
                            .sort(ElasticConstant.Dynamic_UPDATE_TIME, SortOrder.DESC);
                } else {
                    searchSourceBuilder.query(
                            QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery(ElasticConstant.NOTE_TITLE, value))
                                    .should(QueryBuilders.wildcardQuery(ElasticConstant.DYNAMIC_TITLE, value))
                                    .should(QueryBuilders.wildcardQuery(ElasticConstant.CONTENT, value))
                    )
                            .sort(ElasticConstant.Dynamic_UPDATE_TIME, SortOrder.DESC);
                }
            } else {
                searchSourceBuilder.query(QueryBuilders.matchAllQuery()).sort(ElasticConstant.PTF_NOTE_ID, SortOrder.DESC);
            }
        }
        //endregion
        //region 用户信息检索
        else if (indexName.equals(ElasticConstant.USER_INDEX)) {
            // 传入参数为手机号时,则匹配字段名cellPhone,其他则匹配 用户名 nickName 和 userId
            if (fieldName.equals(ElasticConstant.USER_PHONE)) {
                searchSourceBuilder.query(QueryBuilders.wildcardQuery(fieldName, value));
            } else {
                if (StringUtils.isChineseChar(value)) {
//                    searchSourceBuilder.query(QueryBuilders.matchPhraseQuery(ElasticConstant.NICK_NAME, value));
                    searchSourceBuilder.query(QueryBuilders.queryStringQuery(value));
                } else {
                    searchSourceBuilder.query(QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery(ElasticConstant.USER_PHONE, value))
                            .should(QueryBuilders.wildcardQuery(ElasticConstant.NICK_NAME, value))
                            .should(QueryBuilders.wildcardQuery(ElasticConstant.USER_INFO_USER_ID, value))
                    );
                }
            }
        }
        //endregion
        //region 策略信息检索
        else if (indexName.equals(ElasticConstant.STRATEGY_INDEX)) {
            if (StringUtils.isChineseChar(value)) {
                // value为中文时,不走分词器,直接以短语的形式进行检索数据
                searchSourceBuilder.query(
                        QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery(ElasticConstant.STRATEGY_NAME, value))
                                .should(QueryBuilders.matchPhraseQuery(ElasticConstant.STRATEGY_DESCRIPTION, value))
                                .should(QueryBuilders.matchPhraseQuery(ElasticConstant.STRATEGY_STOCK_NAME, value))
                                .should(QueryBuilders.matchPhraseQuery(ElasticConstant.STRATEGY_STOCK_CODE, value))
                )
                        .sort(ElasticConstant.STRATEGY_PUBLISH_TIME, SortOrder.DESC);
            } else {
                //value为其他时,以参数的形式进行模糊检索数据
                searchSourceBuilder.query(
                        QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery(ElasticConstant.STRATEGY_NAME, value))
                                .should(QueryBuilders.wildcardQuery(ElasticConstant.STRATEGY_DESCRIPTION, value))
                                .should(QueryBuilders.wildcardQuery(ElasticConstant.STRATEGY_STOCK_NAME, value))
                                .should(QueryBuilders.matchPhraseQuery(ElasticConstant.STRATEGY_STOCK_CODE, value))
                )
                        .sort(ElasticConstant.STRATEGY_PUBLISH_TIME, SortOrder.DESC);
            }
        }
        //endregion
        //region 在ES中检索数据
        request.indices(indexName);
        request.types(indexType);
        request.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询Elasticsearch，执行selectElasticsearch方法，获取数据信息异常：{}", e.getMessage());
        }
        //endregion
        //region 数据包装
        SearchHit[] hits = {};
        if (response != null && response.getHits() != null) {
            hits = response.getHits().getHits();
            logger.info("当前匹配到数据size：" + response.getHits().totalHits);
        }
        Map<String, Object> map = new HashMap<>();
        for (SearchHit hit : hits) {
            Map<String, Object> asMap = hit.getSourceAsMap();
            if (StringUtils.isNotNull(asMap)) {
                map = getConvertAfterValue(asMap, ipPath);
            }
            //region 操作从ES中查询出来的资讯数据
            if (indexName.equals(ElasticConstant.NEWS_INDEX)) {
                try {
                    getTheQuantityByInformationId(map, userIds);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("根据资讯id获取数量时发生了异常,异常信息为 {}", e.getMessage());
                }
                try {
                    newsMiddleIncludeStockInfo(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("操作资讯中包含股票信息时异常,异常信息为 {}", e.getMessage());
                }
            }
            //endregion
            //region 操作从ES中查询出来的动态数据
            if (indexName.equals(ElasticConstant.DYNAMIC_INDEX)) {
                try {
                    judgeIsWhatState(userIds, map);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("判断是什么状态的时候发生了异常,异常信息为 {}", e.getMessage());
                }
                try {
                    judgeAssembleParametersByAtids(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("判断根据@id装配参数的时候发生了异常,异常信息为 {}", e.getMessage());
                }
                try {
                    getTheQuantityBasedOnTheDynamicId(map, userIds);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("根据动态id获取数量时发生了异常,异常信息为 {}", e.getMessage());
                }
                try {
                    dynamicMiddleIncludeStockInfo(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("操作动态中包含股票信息时异常,异常信息为 {}", e.getMessage());
                }
                try {
                    dynamicaBringOutStrategyInfo(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("操作动态带出策略信息发生异常,异常信息为:{}", e.getMessage());
                }
            }
            //endregion
            //region 操作从ES中查询出来的用户数据
            if (indexName.equals(ElasticConstant.USER_INDEX)) {
                try {
                    judgeTheDataInESIsConsistentWithTheDatabase(map, userIds);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("判断判断ES中数据与数据库是否一致的时候发生了异常,异常信息为 {}", e.getMessage());
                }
            }
            //endregion
            //region 操作从ES中查询出来的策略数据
            if (indexName.equals(ElasticConstant.STRATEGY_INDEX)) {
                try {
                    strategyOverview(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("根据数据包装策略概要时发生异常,异常信息为:{}", e.getMessage());
                }
                try {
                    getQuantityBasedOnStrategy(map, userIds);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("根据策略获取数量时发生了异常,异常信息为 {}", e.getMessage());
                }
            }
            //endregion
            list.add(map);
        }
        //endregion
        logger.info("输出查询结果：" + JSONObject.toJSONString(list));
        logger.info("结束执行selectElasticsearch方法...end...");
        return list;
    }

    /**
     * 动态带出策略信息
     *
     * @param map
     */
    private void dynamicaBringOutStrategyInfo(Map<String, Object> map) {
        Map<String, Object> contentMap = (Map<String, Object>) JSONObject.parse(map.get("busContent").toString());
        UserStrategyListRespVO vo = new UserStrategyListRespVO();
        StrategyInfo info = new StrategyInfo();
        //如果是策略类型
        if (ElasticConstant.SY.equals(map.get("noteType").toString())) {
            Integer strategyId = Integer.valueOf(contentMap.get("artId").toString());
            info = strategyInfoService.getOne(strategyId);
            map.put("strategyId", strategyId);
            if (StringUtils.isNotNull(info)) {
                map.put("testStatus", info.getTestStatus());
                map.put("status", info.getStatus());
            }
            strategyOverview(map);
            //查询是否跟单
            Integer followed = strategyInfoService.whetherTrackOrder(Integer.valueOf(map.get("userId").toString()), strategyId);
            map.put("followed", followed);
            vo = (UserStrategyListRespVO) mapToObj(map, UserStrategyListRespVO.class);
            BeanUtils.copyProperties(info, vo);
            map.put("userStrategyListRespVO", vo);
        }
    }

    private Object mapToObj(Map<String, Object> map, Class clazz) {
        String resultMapJSON = JSONUtil.toCompatibleJson(map);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String key = field.getName();
            if ("userId".equals(key)) {
                continue;
            }
            if ("noteType".equals(key)) {
                continue;
            }
            if ("stockInfo".equals(key)) {
                continue;
            }
            map.remove(key);
        }
        return JSONUtil.fromJson(resultMapJSON, clazz);
    }

    private void getQuantityBasedOnStrategy(Map<String, Object> map, Integer userId) {
        //当前策略id
        Integer strategyId = Integer.valueOf(map.get("strategyId").toString());
        StockInfoVO stockInfoVO = new StockInfoVO();
        //region step 查询当前策略的互动信息
        Map<String, Object> hashMap = strategyInfoService.queryInteractiveInfoAboutTheCurrentStrategy(strategyId, userId);
        map.putAll(hashMap);
        //查询是否关注
        QueryWrapper<PtfFav> favwrapper = new QueryWrapper<>();
        favwrapper.eq("from_user_id", userId);
        favwrapper.eq("is_status", 1);
        //查询是否关注
        map.put("isReal", 0);
        List<PtfFav> ptfFavs = ptfFavService.list(favwrapper);
        ptfFavs.forEach(ptfFav -> {
            if (ptfFav.getUserId().equals(userId)) {
                map.put("isReal", 1);
            }
        });
        //是否收藏
        map.put("isLimit", 0);
        List<Integer> ids = new ArrayList<>();
        try {
            ids = ptfFavService.whetherToCollect(userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("根据用户id :{},获取用户是否收藏策略出现异常,异常信息为:{}", userId, e.getMessage());
        }
        if (ids.contains(strategyId)) {
            map.put("isLimit", 1);
        }
        map.put("uIdLong", IDTransUtil.encodeId(Long.valueOf(map.get("userId").toString()), SecurityKey.ID_KEY));
        map.put("perm", 0);
        //endregion
        QuotationVO quotationVO = strategyInfoService.queryStockDataFromRedis(map.get("stockCode").toString());
        BeanUtils.copyProperties(stockInfoVO, quotationVO);
        stockInfoVO.setStockName(quotationVO.getStkName() == null ? "" : quotationVO.getStkName());
        map.put("stockInfo", stockInfoVO);
        UserStrategyListRespVO vo = (UserStrategyListRespVO) mapToObj(map, UserStrategyListRespVO.class);
        map.put("userStrategyListRespVO", vo);
    }

    /**
     * 策略概述
     *
     * @param map
     */
    private void strategyOverview(Map<String, Object> map) {
        Integer strategyStatus = 0;
        Integer testStatus = (Integer) map.get("testStatus");
        Integer status = (Integer) map.get("status");
        Integer strategyId = (Integer) map.get("strategyId");
        if (testStatus == 0 || testStatus == 1) {
            strategyStatus = 1;
        }
        if (status == -1 || status == 1) {
            strategyStatus = 3;
        }
        //策略概述收益曲线
        List<StrategyIncome> list = strategyIncomeService.queryStrategyOverviewLine(strategyId, strategyStatus, ElasticConstant.LIMITNUMBER);
        map.put("userStrategyIncomes", list);
        //累计收益、年化收益、运行天数
        QueryWrapper<StrategyResult> wrapper = new QueryWrapper<StrategyResult>();
        wrapper.eq("strategy_id", strategyId);
        wrapper.eq("result_type", strategyStatus);
        wrapper.orderByDesc("create_time");
        List<StrategyResult> result = strategyResultService.list(wrapper);
        map.put("accumulateIncome", "0");
        map.put("annualIncome", "0");
        map.put("days", 0);
        //策略所需参数 -- 代表这是策略数据
        if (!StringUtils.isNotNull(map.get("noteType"))) {
            map.put("noteType", "STY");
        }
        if (StringUtils.isNEmpty(result)) {
            map.put("accumulateIncome", new BigDecimal(result.get(0).getAnnualIncome()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            map.put("annualIncome", new BigDecimal(result.get(0).getAccumulateIncome()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            map.put("days", result.get(0).getDays());
        }
    }

    /**
     * 新闻中包含股票信息
     *
     * @param map
     */
    private void newsMiddleIncludeStockInfo(Map<String, Object> map) {
        StockInfoVO stockInfo = null;
        String stockCode = "";
        if (StringUtils.isNotNull(map.get("gp"))) {
            stockCode = (String) map.get("gp");
        }
        String[] str = stockCode.split(",");
        for (String value : str) {
            //有多个股票代码只拿第一个
            stockCode = value;
            break;
        }
        //从redis中获取股票数据
        QuotationVO quotationVO = queryDataFromRedis(stockCode);
        if (quotationVO != null) {
            stockInfo = new StockInfoVO();
            BeanUtils.copyProperties(quotationVO, stockInfo);
            stockInfo.setStockName(quotationVO.getStkName());
        }
        map.put("stockInfo", stockInfo);
    }

    /**
     * 动态中包含股票信息
     *
     * @param map
     */
    private void dynamicMiddleIncludeStockInfo(Map<String, Object> map) {
        StockInfoVO stockInfo = new StockInfoVO();
        List<String> assets = new ArrayList<>();
        if (StringUtils.isNotNull(map.get("assets"))) {
            assets = (List<String>) map.get("assets");
        }
        for (String stockCode : assets) {
            QuotationVO quotationVO = queryDataFromRedis(stockCode);
            if (quotationVO != null) {
                BeanUtils.copyProperties(quotationVO, stockInfo);
                stockInfo.setStockName(quotationVO.getStkName());
            }
            map.put("stockInfo", stockInfo);
        }
    }

    /**
     * 从redis中获取股票数据
     *
     * @param stockCode
     * @return
     */
    private QuotationVO queryDataFromRedis(String stockCode) {
        QuotationVO quotationVO = new QuotationVO();
        if (stockCode.endsWith(MktTypeEnums.HK.getTypeName())) {
            quotationVO = redisMapService.findObject(HkexTsData.class, stockCode);
        } else if (stockCode.endsWith(MktTypeEnums.US.getTypeName())) {
            quotationVO = redisMapService.findObject(UsTsData.class, stockCode);
        } else if (stockCode.endsWith(MktTypeEnums.SH.getTypeName())) {
            quotationVO = redisMapService.findObject(ShTsData.class, stockCode);
        } else if (stockCode.endsWith(MktTypeEnums.SZ.getTypeName())) {
            quotationVO = redisMapService.findObject(SzTsData.class, stockCode);
        } else {
            return null;
        }
        return quotationVO;
    }

    private void getTheQuantityByInformationId(Map<String, Object> map, Integer userIds) {
        //点赞数量
        int thumbUpNumber = 0;
        //评论数量
        int commentNumber = 0;
        //点踩数量
        int numberOfBadReview = 0;
        //分享数量
        int shareNumber = 0;
        //浏览次数
        int browseNumber = 0;
        //互动状态
        String interactiveState = "";

        NewsInteractionVO newsInteractionVO = getDatabaseData(Integer.valueOf(map.get("labelNewsId").toString()));
        if (StringUtils.isNotNull(newsInteractionVO)) {
            List<NewsInteraction> list = newsInteractionVO.getList();
            browseNumber = newsInteractionVO.getReadNum();
            shareNumber = newsInteractionVO.getForwardNum();
            if (StringUtils.isNotNull(list) && list.size() > 0) {
                for (NewsInteraction newsInteraction : list) {
                    //点赞
                    if (newsInteraction.getInterType().equals(NewsInteractionEnum.LIKE.getCode())) {
                        thumbUpNumber++;
                    }
                    //点踩
                    if (newsInteraction.getInterType().equals(NewsInteractionEnum.DISLIKE.getCode())) {
                        numberOfBadReview++;
                    }
                    //评论
                    if (newsInteraction.getInterType().equals(NewsInteractionEnum.REPLY.getCode()) || newsInteraction.getInterType().equals(NewsInteractionEnum.COMMENTREPLY.getCode())) {
                        commentNumber++;
                    }
                    //互动状态
                    if (newsInteraction.getFromUser().equals(userIds)) {
                        if (newsInteraction.getInterType().equals(NewsInteractionEnum.LIKE.getCode()) || newsInteraction.getInterType().equals(NewsInteractionEnum.DISLIKE.getCode())) {
                            interactiveState = newsInteraction.getInterType();
                        }
                    }
                }
            }
        }
        map.put("thumbUpNumber", thumbUpNumber);
        map.put("commentNumber", commentNumber);
        map.put("numberOfBadReview", numberOfBadReview);
        map.put("shareNumber", shareNumber);
        map.put("browseNumber", browseNumber);
        map.put("interactiveState", interactiveState);
    }

    private NewsInteractionVO getDatabaseData(Integer labelNewsId) {
        NewsInteractionVO interactionVO = null;
//        Columns column = Columns.create().column("inter_id").column("news_id").column("from_user").column("inter_type").column("content").column("comment_id");
//        wrapper.setSqlSelect(column);
        QueryWrapper<NewsInteraction> wrapper = new QueryWrapper<>();
        wrapper.eq("news_id", labelNewsId);
        wrapper.eq("status", 1);
        wrapper.select("inter_id", "news_id", "from_user", "inter_type", "content", "comment_id");
        List<NewsInteraction> list = newsInteractionService.list(wrapper);
        if (StringUtils.isNotNull(list) && list.size() > 0) {
            interactionVO = new NewsInteractionVO();
            interactionVO.setList(list);
        }

        QueryWrapper<NewsRecordNum> ew = new QueryWrapper<>();
        ew.eq("news_id", labelNewsId);
//        ew.setSqlSelect(Columns.create().column("news_id").column("read_num").column("forward_num"));
        wrapper.select("news_id", "read_num", "forward_num");
        NewsRecordNum one = newsRecordNumService.getOne(ew);
        if (StringUtils.isNotNull(one)) {
            interactionVO.setReadNum(one.getReadNum());
            interactionVO.setForwardNum(one.getForwardNum());
        }
        return interactionVO;
    }


    private void judgeTheDataInESIsConsistentWithTheDatabase(Map<String, Object> map, Integer userIds) {
        logger.info("进入judgeTheDataInESIsConsistentWithTheDatabase方法,获取到的当前搜索用户id为: {}", userIds);
        logger.info("进入judgeTheDataInESIsConsistentWithTheDatabase方法,获取想要搜索的用户id为: {}", map.get("userId"));
        if (userIds == 0) {
            map.put("reqStatus", "");
            map.put("reqDirection", "");
            return;
        }
        List<UserInfoBO> userInfoBO = userInfoBOService.selectNewFriendInformationById(Integer.valueOf(map.get("userId").toString()));
        logger.info("根据想要搜索的用户ID获取到的新朋友信息为: {}", userInfoBO);
        if (userInfoBO.size() > 0) {
            for (UserInfoBO infoBO : userInfoBO) {
                if (infoBO.getTargetUserId().equals(userIds)) {
                    logger.info("查询到的userId为: {}", infoBO.getUserId());
                    if (StringUtils.isNotNull(infoBO.getReqStatus())) {
                        map.put("reqStatus", infoBO.getReqStatus());
                    }
                    if (StringUtils.isNotNull(infoBO.getReqDirection())) {
                        map.put("reqDirection", infoBO.getReqDirection());
                    }
                    break;
                } else {
                    map.put("reqStatus", "");
                    map.put("reqDirection", "");
                }
            }
        }
    }

    private void judgeAssembleParametersByAtids(Map<String, Object> map) {
        List<Integer> atIds = (List<Integer>) map.get("atIds");
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> mapId = null;
        for (Integer atId : atIds) {
            mapId = new HashMap<>();
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
//            Columns columns = Columns.create().column("user_id").column("nick_name");
//            wrapper.setSqlSelect(columns);
            wrapper.select("user_id", "nick_name");
            wrapper.eq("user_id", atId);
            UserInfo userInfo = userInfoService.getOne(wrapper);
            mapId.put("longId", IDTransUtil.encodeId(Long.valueOf(atId), SecurityKey.ID_KEY));
            if (StringUtils.isNotNull(userInfo)) {
                mapId.put("nickName", userInfo.getNickName());
            } else {
                mapId.put("nickName", "");
            }
            list.add(mapId);
        }
        map.put("atIds", list);
        logger.info("根据id装配参数,最终@Ids数据为{}", map.get("atIds"));
    }

    private void getTheQuantityBasedOnTheDynamicId(Map<String, Object> map, Integer userId) {
        //点赞数量
        int thumbUpNumber = 0;
        //评论数量
        int commentNumber = 0;
        //点踩数量
        int numberOfBadReview = 0;
        //互动状态
        String interactiveState = "";
//        Columns column = Columns.create().column("inter_id").column("ptf_note_id").column("from_user").column("inter_type").column("content").column("note_user");
//        wrapper.setSqlSelect(column);
        QueryWrapper<PtfNoteInteraction> wrapper = new QueryWrapper<>();
        wrapper.eq("ptf_note_id", map.get("ptfNoteId"));
        wrapper.eq("is_status", 1);
        wrapper.select("inter_id", "ptf_note_id", "from_user", "inter_type", "content", "note_user");
        List<PtfNoteInteraction> interactions = ptfNoteInteractionService.list(wrapper);
        if (StringUtils.isNotNull(interactions) && interactions.size() > 0) {
            for (PtfNoteInteraction interaction : interactions) {
                //点赞
                if (interaction.getInterType().equals(PtfNoteInteractionEnum.L.getCode())) {
                    thumbUpNumber++;
                }
                //评论
                if (interaction.getInterType().equals(PtfNoteInteractionEnum.R.getCode())) {
                    commentNumber++;
                }
                //点踩
                if (interaction.getInterType().equals(PtfNoteInteractionEnum.D.getCode())) {
                    numberOfBadReview++;
                }
                //互动状态
                if (interaction.getFromUser().equals(userId)) {
                    if (interaction.getInterType().equals(PtfNoteInteractionEnum.L.getCode()) || interaction.getInterType().equals(PtfNoteInteractionEnum.D.getCode())) {
                        interactiveState = interaction.getInterType();
                    }
                }
            }
        }
        map.put("thumbUpNumber", thumbUpNumber);
        map.put("commentNumber", commentNumber);
        map.put("numberOfBadReview", numberOfBadReview);
        map.put("interactiveState", interactiveState);
    }

    private void judgeIsWhatState(Integer userIds, Map<String, Object> map) {
        QueryWrapper<PtfNoteInteraction> wrapper = new QueryWrapper<>();
        wrapper.eq("from_user", userIds);
        wrapper.eq("parent_inter_id", 0);
        wrapper.like("inter_type", "C");
        wrapper.eq("is_status", 1);
        //查询是否收藏
        List<PtfNoteInteraction> ptfNoteInteractions = ptfNoteInteractionService.list(wrapper);
        if (ptfNoteInteractions != null && ptfNoteInteractions.size() > 0) {
            for (PtfNoteInteraction ptfNoteInteraction : ptfNoteInteractions) {
                if (ptfNoteInteraction.getPtfNoteId().equals(map.get("ptfNoteId"))) {
                    map.put("isLimit", 1);
                }
            }
        }
        QueryWrapper<PtfFav> favwrapper = new QueryWrapper<>();
        favwrapper.eq("from_user_id", userIds);
        favwrapper.eq("is_status", 1);
        //查询是否关注
        List<PtfFav> ptfFavs = ptfFavService.list(favwrapper);
        if (ptfFavs != null && ptfFavs.size() > 0) {
            for (PtfFav ptfFav : ptfFavs) {
                if (ptfFav.getUserId().equals(map.get("userId"))) {
                    map.put("isReal", 1);
                }
            }
        }
        if (deletablePtfNoteType(map.get("noteType").toString()) && userIds.equals(map.get("userId"))) {
            map.put("perm", 1);
        }
    }

    private boolean deletablePtfNoteType(String type) {
        return type.equals(ENoteType.M.toString())
                || type.equals(ENoteType.N.toString())
                || type.equals(ENoteType.V.toString())
                || type.equals(ENoteType.A.toString())
                || type.equals(ENoteType.G.toString())
                || type.equals(ENoteType.Y.toString());
    }

    @Override
    public List<HotSearchVO> convertElasticsearchToHotSearchVO(String indexName, String indexType, String fieldName, String value, Integer rowsPerPage, Integer currentPage, boolean isExactMatch) {
        logger.info("convertElasticsearchToHotSearchVO...start...");
        List<HotSearchVO> list = new ArrayList<>();
        SearchRequest request = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (currentPage == 0) {
            currentPage = 1;
        }
        searchSourceBuilder.from((currentPage - 1) * rowsPerPage);
        searchSourceBuilder.size(rowsPerPage);

        boolean conditionQuery = false;
        if (StringUtils.isNEmpty(value)) {
            conditionQuery = true;
        }

        // 给传入值匹配通配符
        value = ElasticConstant.WILDCARD + value + ElasticConstant.WILDCARD;
        // 新闻资讯信息检索
        if (indexName.equals(ElasticConstant.NEWS_INDEX)) {
            // 传入参数为数字，则匹配字段名gp；传入参数为中文，则匹配字段名title；传入参数为英文，则匹配字段名spell；匹配方式：通配符匹配
            if (fieldName.equals(ElasticConstant.GP)) {
                if (isExactMatch) {
                    searchSourceBuilder.query(QueryBuilders.queryStringQuery(value)).sort(ElasticConstant.ISSUE_TIME, SortOrder.DESC);
                } else {
                    searchSourceBuilder.query(QueryBuilders.wildcardQuery(fieldName, value)).sort(ElasticConstant.ISSUE_TIME, SortOrder.DESC);
                }
            } else if (fieldName.equals(ElasticConstant.TITLE)) {
                searchSourceBuilder.query(QueryBuilders.matchQuery(fieldName, value)).sort(ElasticConstant.ISSUE_TIME, SortOrder.DESC);
            } else if (fieldName.equals(ElasticConstant.LABEL_NEWS_ID)) {
                searchSourceBuilder.query(QueryBuilders.matchAllQuery()).sort(ElasticConstant.LABEL_NEWS_ID, SortOrder.DESC);
            } else {
                // 传入参数为特殊符号时，则对所有字段进行分词查询
                searchSourceBuilder.query(QueryBuilders.queryStringQuery(value)).sort(ElasticConstant.ISSUE_TIME, SortOrder.DESC);
            }
            // 股票基本信息检索
        } else if (indexName.equals(ElasticConstant.STOCK_INDEX)) {
            // 传入参数为数字，则匹配字段名stkCode；传入参数为中文，则匹配字段名stkName；匹配方式：通配符匹配
            if (fieldName.equals(ElasticConstant.STK_CODE)) {
                searchSourceBuilder.query(QueryBuilders.wildcardQuery(fieldName, value)).sort(ElasticConstant.MKT_TYPE, SortOrder.ASC).sort(ElasticConstant.SEC_TYPE, SortOrder.ASC);
            } else {
                // 传入参数为非数字时（也就是字母和特殊符号等），则对所有字段进行分词查询
                searchSourceBuilder.query(QueryBuilders.queryStringQuery(value)).sort(ElasticConstant.SEC_TYPE, SortOrder.ASC);
            }
            // 热搜榜信息检索
        } else if (indexName.equals(ElasticConstant.SEARCH_INDEX)) {
            // 根据更新时间范围查询
//            searchSourceBuilder.query(QueryBuilders.rangeQuery(ElasticConstant.UPDATE_TIME).gt(DateUtils.getTwoHoursBeforeTimeStamp())).sort(ElasticConstant.COUNT, SortOrder.DESC);


            if (conditionQuery) {
                if (isExactMatch) {
                    searchSourceBuilder.query(QueryBuilders.queryStringQuery(value)).sort(ElasticConstant.COUNT, SortOrder.DESC);
                } else {
                    searchSourceBuilder.query(QueryBuilders.wildcardQuery(fieldName, value)).sort(ElasticConstant.COUNT, SortOrder.DESC);
                }
            } else {
                searchSourceBuilder.query(QueryBuilders.matchAllQuery()).sort(ElasticConstant.COUNT, SortOrder.DESC);
            }
        }
        request.types(indexType);
        request.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询Elasticsearch，执行selectAllFieldElasticsearch方法，获取数据信息异常：{}", e.getMessage());
        }
        SearchHit[] hits = {};
        if (response != null && response.getHits() != null) {
            hits = response.getHits().getHits();
            logger.info("当前匹配到数据size：" + response.getHits().totalHits);
        }
        HotSearchVO hotSearchVO = null;
        for (SearchHit hit : hits) {
            if (StringUtils.isNotNull(hit.getSourceAsMap())) {
                hotSearchVO = convertSearchHitsToHotSearchVO(hit, ipPath);
            }
            list.add(hotSearchVO);
        }
        logger.info("输出查询结果：" + JSONObject.toJSONString(list));
        logger.info("结束执行selectAllFieldElasticsearch方法...end...");
        return list;
    }

    @Override
    public List<Map<String, String>> selectEsDataById(String index, String type, String id) {
        GetRequest getRequest = new GetRequest(index, type, id);
        GetResponse getResponse = null;
        try {
            getResponse = restHighLevelClient.get(getRequest);
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
            logger.error("根据ID获取ES数据异常，异常信息：{}", e.getMessage());
        }
        List<Object> list = new ArrayList<>();
        List<Map<String, String>> resultList = new ArrayList<>();
        // 判断返回结果是否为空
        if (StringUtils.isNotNull(getResponse.getSourceAsMap())) {
            // 有返回结果时，再次判断返回结果中是否存在对应key的json数据信息。注：后续新增其他标签数据，可适配else代码块
            if (StringUtils.isNotNull(getResponse.getSourceAsMap().get(ElasticConstant.STOCK_TAGS))) {
                // 根据索引名判断，传入对应 json 数组的key
                if (index.equals(ElasticConstant.STOCK_TAG_INDEX)) {
                    list = JsonUtils.parseJson(ElasticConstant.STOCK_TAGS, JSONObject.toJSONString(getResponse.getSourceAsMap()));
                    if (list != null && list.size() > 0) {
                        for (Object obj : list) {
                            JsonObject jsonObject = (JsonObject) obj;
                            Map<String, String> map = new HashMap<>();
                            map.put(ElasticConstant.STK_CODE, jsonObject.get("id").getAsString());
                            map.put(ElasticConstant.STK_NAME, jsonObject.get("name").getAsString());
                            resultList.add(map);
                        }
                    }
                }
            }
        }
        logger.info("输出解析之后的信息数据：{}", JSONObject.toJSONString(resultList));
        return resultList;
    }

    @Override
    public Boolean isExistEsDataById(String index, String type, String id) {
        GetRequest getRequest = new GetRequest(index, type, id);
        GetResponse getResponse = null;
        boolean isExist = false;
        try {
            getResponse = restHighLevelClient.get(getRequest);
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
            logger.error("根据ID获取ES数据异常，异常信息：{}", e.getMessage());
        }
        // 判断返回结果是否为空
        if (null != getResponse.getSourceAsMap()) {
            isExist = true;
        }
        return isExist;
    }

    @Override
    public void insertElasticsearch(String index, String type, String id, Object obj) {
        logger.info("插入ID：" + id + "，数据到ES索引：" + index + "中开始...");
        IndexRequest indexRequest = new IndexRequest(index, type, id)
                .source(JSONObject.toJSONString(obj), XContentType.JSON);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            logger.info("插入ID：" + id + "，数据到ES索引：" + index + "中成功...");
        } catch (ElasticsearchException e) {
            e.getDetailedMessage();
            logger.error("ID：" + id + "，数据插入ES异常，异常信息：{}", e);
        } catch (java.io.IOException ex) {
            ex.getLocalizedMessage();
            logger.error("ID：" + id + "，数据插入ES出现IO异常，异常信息：{}", ex.getMessage());
        }
        logger.info("插入ID：" + id + "，数据到ES索引：" + index + "中结束...");
    }

    @Override
    public void insertBatchElasticsearch() {

    }

    @Override
    public void updateElasticsearchById(String index, String type, String id, Object obj) {
        logger.info("根据ID：" + id + "，更新ES索引：" + index + "中数据开始...");
        UpdateRequest updateRequest = new UpdateRequest(index, type, id)
                .fetchSource(true);
        try {
            String json = "";
            Map<String, Object> map = getObjectModelProperties(obj);
            logger.info("通过ID: " + id + ",更新时,获取到用户想要更新的字段为: " + JSONUtil.toCompatibleJson(map));
            String str = selectESInfoById(index, type, id);
            logger.info("通过ID: " + id + ",更新时,查询到的ES数据为: " + str);
            Map<String, Object> resultMap = JSON.parseObject(str, Map.class);
            readingSetDataCopyProperties(map, resultMap, obj);
            logger.info("根据ID：" + id + "，更新ES索引：" + index + "中数据,数据信息为:{}", JSONUtil.toCompatibleJson(obj));
            if (obj instanceof EsNewsInfo) {
                json = objectMapper.writeValueAsString((EsNewsInfo) obj);
            }
            if (obj instanceof EsPtfNoteInfo) {
                json = objectMapper.writeValueAsString((EsPtfNoteInfo) obj);
            }
            updateRequest.doc(json, XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest);
            updateResponse.getGetResult().sourceAsMap();
            logger.info("根据ID：" + id + "，更新ES索引：" + index + "中数据成功...");
        } catch (JsonProcessingException e) {
            e.getMessage();
            logger.error("根据ID：" + id + "，更新ES索引：" + index + "中数据时，发生json处理异常，异常信息{}", e.getMessage());
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
            logger.error("根据ID：" + id + "，更新ES索引：" + index + "中数据异常，异常信息{}", e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            logger.error("根据ID：" + id + "，更新ES索引：" + index + "中读取数据复制属性集时发生异常，异常信息{}", e);
        }
        logger.info("根据ID：" + id + "，更新ES索引：" + index + "中数据结束...");
    }

    /**
     * 读取集合数据拷贝属性值
     *
     * @param map
     * @param resultMap
     * @param obj
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void readingSetDataCopyProperties(Map<String, Object> map, Map<String, Object> resultMap, Object obj) throws InvocationTargetException, IllegalAccessException {
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (resultMap == null) {
                resultMap = new HashMap<>();
            }else {
                Object o = resultMap.get(key);
                if (null != o) {
                    resultMap.put(key, value);
                }
            }
        }
        Set<Map.Entry<String, Object>> entrie = resultMap.entrySet();
        for (Map.Entry<String, Object> entry : entrie) {
            Object value = entry.getValue();
            if (value != null) {
                org.apache.commons.beanutils.BeanUtils.setProperty(obj, entry.getKey(), value);
            }
        }
    }


    /**
     * 获取对象模型的属性
     *
     * @param obj
     * @return
     */
    private Map<String, Object> getObjectModelProperties(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = field.getName();
            field.setAccessible(true); // 私有属性必须设置访问权限
            Object value = field.get(obj);
            if (value != null) {
                map.put(name, value);
            }
        }
        return map;
    }

    @Override
    public void deleteElasticsearchById(String index, String type, String id) {
        logger.info("根据ID：" + id + "，删除ES索引" + index + "中数据开始...");
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        try {
            restHighLevelClient.delete(deleteRequest);
            logger.info("根据ID：" + id + "，删除ES索引" + index + "中数据成功");
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
            logger.error("根据ID：" + id + "，删除ES索引" + index + "中数据异常，异常信息{}", e.getMessage());
        }
        logger.info("根据ID：" + id + "，删除ES索引" + index + "中数据结束...");
    }

    @Override
    public List<Map<String, Object>> multiToOneSelectByEs(String indexName, String indexType, String fieldName, List<String> listValue, Integer rowsPerPage, Integer currentPage, Integer labelNewsId) {
        logger.info("开始执行multiToOneSelectByEs方法...start...");
        List<Map<String, Object>> list = new ArrayList<>();
        SearchRequest request = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (currentPage == 0) {
            currentPage = 1;
        }
        searchSourceBuilder.from((currentPage - 1) * rowsPerPage);
        searchSourceBuilder.size(rowsPerPage);

        // 新闻资讯信息检索
        if (indexName.equals(ElasticConstant.NEWS_INDEX)) {
            DisMaxQueryBuilder disMaxQueryBuilder = new DisMaxQueryBuilder();
            for (int i = 0; i < listValue.size(); i++) {
                String value = ElasticConstant.WILDCARD + listValue.get(i) + ElasticConstant.WILDCARD;
                if (labelNewsId != null && labelNewsId > 0) {
                    // 全文匹配
//                    disMaxQueryBuilder.add(QueryBuilders.boolQuery().must(QueryBuilders.queryStringQuery(value)).must(QueryBuilders.rangeQuery(ElasticConstant.LABEL_NEWS_ID).lt(labelNewsId)));
                    // 指定列匹配，并指定条件 形如：labelNewsId < 传入value,分页时使用
                    disMaxQueryBuilder.add(QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery(fieldName, value)).must(QueryBuilders.rangeQuery(ElasticConstant.LABEL_NEWS_ID).lt(labelNewsId)));
                    disMaxQueryBuilder.add(QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery(ElasticConstant.TITLE, value)).must(QueryBuilders.rangeQuery(ElasticConstant.LABEL_NEWS_ID).lt(labelNewsId)));
                } else {
                    // 全文匹配
//                    disMaxQueryBuilder.add(QueryBuilders.queryStringQuery(value));
                    // 指定列匹配，不添加条件
                    disMaxQueryBuilder.add(QueryBuilders.matchPhraseQuery(fieldName, value));
                    disMaxQueryBuilder.add(QueryBuilders.matchPhraseQuery(ElasticConstant.TITLE, value));
                    // 注：fuzzyQuery() 等同于 matchPhraseQuery() ， 只是传入值不用增加通配符 * 号
//                    disMaxQueryBuilder.add(QueryBuilders.fuzzyQuery(fieldName, listValue.get(i)));
//                    disMaxQueryBuilder.add(QueryBuilders.fuzzyQuery(ElasticConstant.TITLE, listValue.get(i)));
                }
            }
            searchSourceBuilder.query(disMaxQueryBuilder).sort(ElasticConstant.ISSUE_TIME, SortOrder.DESC);
        }
        searchSourceBuilder.timeout(TimeValue.timeValueMinutes(10));
        request.types(indexType);
        request.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询Elasticsearch，执行multiToOneSelectByEs方法，获取数据信息异常：{}", e.getMessage());
        }
        SearchHit[] hits = {};
        if (response != null && response.getHits() != null) {
            hits = response.getHits().getHits();
            logger.info("当前匹配到数据size：" + response.getHits().totalHits);
        }
        Map<String, Object> map = new HashMap<>();
        for (SearchHit hit : hits) {
            if (StringUtils.isNotNull(hit.getSourceAsMap())) {
                map = getConvertAfterValue(hit.getSourceAsMap(), ipPath);
            }
            list.add(map);
        }
        logger.info("输出查询结果：" + JSONObject.toJSONString(list));
        logger.info("结束执行multiToOneSelectByEs方法...end...");
        return list;
    }

    /**
     * 对匹配检索出的返回结果做包装处理
     *
     * @param map
     * @return
     */
    public static Map<String, Object> getConvertAfterValue(Map<String, Object> map, String ipPath) {
        if (StringUtils.isNotNull(map.get(ElasticConstant.ISSUE_TIME))) {
            try {
                map.put(ElasticConstant.ISSUE_TIME, DateUtils.stampToDate(map.get(ElasticConstant.ISSUE_TIME).toString()));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("日期转换异常，异常信息：{}", e.getMessage());
            }
        }
        if (StringUtils.isNotNull(map.get(ElasticConstant.IMG_URL))) {
            try {
                if (!map.get(ElasticConstant.IMG_URL).toString().contains(ElasticConstant.HTTP)) {
                    map.put(ElasticConstant.IMG_URL, ipPath + map.get(ElasticConstant.IMG_URL));
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("照片url拼接异常，异常信息：{}", e.getMessage());
            }
        }
        return map;
    }

    /**
     * 讲searchhit转成map
     *
     * @param
     * @return
     */
    public static HotSearchVO convertSearchHitsToHotSearchVO(SearchHit searchHit, String ipPath) {
        Map<String, Object> map = searchHit.getSourceAsMap();

        HotSearchVO hotSearchVO = new HotSearchVO();
        hotSearchVO.setId(searchHit.getId());

        if (StringUtils.isNotNull(map.get(ElasticConstant.UPDATE_TIME))) {
            try {
//                map.put(ElasticConstant.UPDATE_TIME, DateUtils.stampToDate(map.get(ElasticConstant.UPDATE_TIME).toString()));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                hotSearchVO.setUpdateTime(sdf.parse(DateUtils.stampToDate(map.get(ElasticConstant.UPDATE_TIME).toString())));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("热搜更新日期转换异常，异常信息：{}", e.getMessage());
            }
        }
        if (StringUtils.isNotNull(map.get(ElasticConstant.TEXT))) {
            try {
                hotSearchVO.setStockName((String) map.get(ElasticConstant.TEXT));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("热搜股票代码转换异常，异常信息：{}", e.getMessage());
            }
        }
        if (StringUtils.isNotNull(map.get(ElasticConstant.COUNT))) {
            try {
                if (map.get(ElasticConstant.COUNT) instanceof Integer) {
                    hotSearchVO.setSearchNum(BigInteger.valueOf((Integer) map.get(ElasticConstant.COUNT)));
                }
                if (map.get(ElasticConstant.COUNT) instanceof Long) {
                    hotSearchVO.setSearchNum(BigInteger.valueOf((Long) map.get(ElasticConstant.COUNT)));
                }

            } catch (Exception e) {
                e.printStackTrace();
                logger.error("热搜count转换异常，异常信息：{}", e.getMessage());
            }
        }


        return hotSearchVO;
    }

    @Override
    public ResultMessage<String> updateHotSearchCount(EsSearchInfoReqVo vo, BigInteger count) {
        try {
            if (!StringUtils.isNEmpty(vo.getText())) {
                logger.info("用户输入text为空......");
                return ResultMessage.fail("用户输入text为空......");
            }
            if (null == count) {
                logger.info("用户输入count为空......");
                return ResultMessage.fail("用户输入text为空......");
            }
            String timeStamp = System.currentTimeMillis() + "";
            //消息队列消费者那边是根据最后的参数的个数来确定方法的,如果是3个,则为中台修改count,2个则为移动端count+1或者新增(如果es没有搜索到主键id存在过,则会自动新增),
            // ,没有删除操作,删除操作只能等效为把count改为0
            producer.sendWithFlush(new ProducerRecord<>(producerTopic, timeStamp, timeStamp + "," + vo.getText() + "," + count));//这里是更新count操作
            logger.info("记录关键词：" + vo.getText() + "，推送kafka成功......");
            return ResultMessage.success("修改搜索量保存成功", null);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("记录关键词推送kafka异常，异常信息{}", e.getMessage());
            return ResultMessage.fail(e.getMessage());
        }

    }

    @Override
    public ResultMessage<String> addHotSearchText(EsSearchInfoReqVo vo) {
        try {
            if (!StringUtils.isNEmpty(vo.getText())) {
                logger.info("用户输入关键词为空......");
                return ResultMessage.fail("用户输入关键词为空......");
            }
            String id = StringUtils.encodeUnicode(vo.getText());
            boolean isExist = isExistEsDataById(ElasticConstant.SEARCH_INDEX, ElasticConstant.SEARCH_TYPE, id);
            if (isExist) {
                logger.info("要新增的股票名称已存在,新增失败");
                return ResultMessage.fail("要新增的股票名称已存在,新增失败");
            }

            String timeStamp = System.currentTimeMillis() + "";
            //消息队列消费者那边是根据最后的参数的个数来确定方法的,如果是3个,则为中台修改count,2个则为移动端count+1或者新增(如果es没有搜索到主键id存在过,则会自动新增),
            // ,没有删除操作,删除操作只能等效为把count改为0
            producer.sendWithFlush(new ProducerRecord<>(producerTopic, timeStamp, timeStamp + "," + vo.getText()));
            logger.info("记录关键词：" + vo.getText() + "，推送kafka成功......");
            return ResultMessage.success("保存成功", null);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("记录关键词推送kafka异常，异常信息{}", e.getMessage());
            return ResultMessage.fail(e.getMessage());
        }

    }

    @Override
    public String selectESInfoById(String index, String type, String id) {
        logger.info("****newsid============" + id);
        GetRequest getRequest = new GetRequest(index, type, id);
        GetResponse getResponse = null;
        try {
            getResponse = restHighLevelClient.get(getRequest);
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
            logger.error("根据ID获取ES数据异常，异常信息：{}", e.getMessage());
        }
        if (getResponse != null) {
            logger.info("****getResponse=========" + getResponse.getSourceAsString());
            return getResponse.getSourceAsString();
        } else {
            return null;
        }
    }

}
