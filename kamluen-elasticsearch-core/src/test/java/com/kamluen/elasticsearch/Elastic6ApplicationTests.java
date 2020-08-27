package com.kamluen.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.kamluen.api.grm.utils.EStkType;
import com.kamluen.api.grm.utils.ESubStkType;
import com.kamluen.api.mktinfo.vo.QuotationVO;
import com.kamluen.api.mktinfo.vo.enums.ENewsEnums;
import com.kamluen.api.mktserver.HkexTsData;
import com.kamluen.api.mktserver.ShTsData;
import com.kamluen.api.mktserver.SzTsData;
import com.kamluen.api.mktserver.UsTsData;
import com.kamluen.api.ptf.vo.enums.ENoteType;
import com.kamluen.api.vo.ResponseVO;
import com.kamluen.common.utils.JSONUtil;
import com.kamluen.elasticsearch.bean.req.CommonEsInfoReqVo;
import com.kamluen.elasticsearch.bean.req.EsUserInfoReqVo;
import com.kamluen.elasticsearch.bean.resp.EsNewsInfoRespVo;
import com.kamluen.elasticsearch.cache.Cache;
import com.kamluen.elasticsearch.cache.CacheManager;
import com.kamluen.elasticsearch.common.CombinedSearch;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.entity.*;
import com.kamluen.elasticsearch.entity.ElasticsearchVO.*;
import com.kamluen.elasticsearch.enums.MktTypeEnums;
import com.kamluen.elasticsearch.enums.StrategyEnum;
import com.kamluen.elasticsearch.service.*;
import com.kamluen.elasticsearch.service.kamluen.*;
import com.kamluen.elasticsearch.service.mktinfo.NewsInfoService;
import com.kamluen.elasticsearch.service.mktinfo.StockInfoService;
import com.kamluen.elasticsearch.service.strategy.StrategyIncomeService;
import com.kamluen.elasticsearch.service.strategy.StrategyInfoService;
import com.kamluen.elasticsearch.utils.*;
import com.kamluen.odps.service.RedisMapService;
import com.kamluen.odps.service.RedisSetService;
import com.kamluen.protocol.StaticType;
import com.kamluen.security.SecurityKey;
import com.kamluen.security.util.IDTransUtil;
import com.kamluen.utils.ProtocolUtils;
import org.apache.poi.ss.formula.functions.Columns;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.ElasticsearchException;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kamluen.elasticsearch.constant.ElasticConstant.STRATEGY_INDEX;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan(basePackages = {"com.kamluen.elasticsearch", "com.kamluen.odps.service"})
@ImportResource(locations = {"classpath:applicationContext-redis.xml"})
public class Elastic6ApplicationTests {

    private static final String HTTPS = "https";
    private static final String SUFFIX = "strategy:";
    private static final String PREFIX = "_follow";
    private static Logger logger = LoggerFactory.getLogger(Elastic6ApplicationTests.class);

    @Value("${oss.prefix}")
    private String prefix;
    @Value("${oss.user.suffix}")
    private String suffix;
    @Value("${oss.wrong.data}")
    private String writeKey;
    @Value("${oss.dynamic.suffix}")
    private String dynamic_suffix;
    @Value("${oss.default.image}")
    private String defaultImage;

    @Resource
    private NewsInfoService newsInfoService;
    @Resource
    private StockInfoService stockInfoService;
    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ElasticsearchService elasticsearchService;
    @Resource
    private DynamicInfoService dynamicInfoService;
    @Resource
    private UserInfoBOService userInfoBOService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private CombinedSearchService combinedSearchService;
    @Resource
    private PtfNoteInteractionService ptfNoteInteractionService;
    @Resource
    private PtfFavService ptfFavService;
    @Resource
    private RedisMapService redisMapService;
    @Resource
    private RedisSetService redisSetService;
    @Resource
    private StrategyInfoService strategyInfoService;
    @Resource
    private StrategyIncomeService strategyIncomeService;

    /**
     * 单条插入
     */
    @Test
    public void insertOne() {
        List<NewsInfo> newsInfos = newsInfoService.list(new QueryWrapper<NewsInfo>());
        logger.info("输出新闻资讯信息数据：" + JSONObject.toJSONString(newsInfos));
        for (NewsInfo info : newsInfos) {
            Map<String, Object> dataMap = objectMapper.convertValue(info, Map.class);
//         单条数据插入
            IndexRequest indexRequest = new IndexRequest(ElasticConstant.NEWS_INDEX, ElasticConstant.NEWS_TYPE, info.getLabelNewsId().toString())
                    .source(dataMap);
            try {
                IndexResponse response = restHighLevelClient.index(indexRequest);
            } catch (ElasticsearchException e) {
                e.getDetailedMessage();
            } catch (java.io.IOException ex) {
                ex.getLocalizedMessage();
            }
            logger.info("新闻资讯信息数据批量插入 Elasticsearch index 完成......");
            return;
        }
    }

    /**
     * 测试动态切换数据源安全性
     */
    @Test
    public void test01() {
        int numnum = 0;
        while (true) {
            if (numnum > 50000) break;
            new Thread(() -> dynamicredynamicreports()).start();
            new Thread(() -> newInfo()).start();
//            dynamicredynamicreports();
//            newInfo();
            numnum++;
        }
    }

    @Test
    public void demo06() {
        StringBuffer sb = new StringBuffer();
        sb.append("publish_status = %s");
        sb.append(" and status != %s");
        String sql = String.format(sb.toString(), StrategyEnum.published.getCode(), StrategyEnum.delete.getCode());
        System.out.println(sql);
    }

    public void dynamicredynamicreports() {
        Integer ptfNoteId = 1;
        Page<PtfNoteInfo> page = new Page<PtfNoteInfo>(1, 2);
        QueryWrapper<PtfNoteInfo> wrapper = new QueryWrapper<>();
//        columns.column("ptf_note_id")
//                .column("user_id")
//                .column("note_type")
//                .column("bus_content")
//                .column("is_status")
//                .column("update_time");
//        wrapper.setSqlSelect(columns);
        wrapper.select("ptf_note_id", "user_id", "note_type", "bus_content", "is_status", "update_time");
        //只查询有效的动态信息
        wrapper.gt("ptf_note_id", ptfNoteId);
        wrapper.eq("is_status", 1);
        IPage<PtfNoteInfo> ptfNoteInfoPage = dynamicInfoService.page(page, wrapper);
//        for (PtfNoteInfo ptfNoteInfo : ptfNoteInfoPage.getRecords()) {
        System.out.println("输出kamluen库信息: " + ptfNoteInfoPage);
//        }
    }

    //    @Test
    public void newInfo() {
//        Integer labelNewsId = 1;
        Page<NewsInfo> npage = new Page<NewsInfo>(1, 2); // 每次查询导入2000条
        QueryWrapper<NewsInfo> ew = new QueryWrapper<>();
        // 获取新闻资讯信息数据导入ES，过滤不可见的资讯，即过滤：is_status=0
//        ew.gt("label_news_id", labelNewsId);
        ew.eq("is_status", 1);
        IPage<NewsInfo> newsInfoPage = newsInfoService.page(npage, ew);
//        for (NewsInfo newsInfo : newsInfoPage.getRecords()) {
        System.out.println("输出mktinfo库信息: " + newsInfoPage);
//        }
    }

    /**
     * 单个插入动态信息数据
     */
    @Test
    public void insertOneDynamicInfo() {
        List<PtfNoteInfo> newsInfos = dynamicInfoService.list(new QueryWrapper<PtfNoteInfo>());
        logger.info("输出新闻资讯信息数据：" + JSONObject.toJSONString(newsInfos));
        for (PtfNoteInfo info : newsInfos) {
            Map<String, Object> dataMap = objectMapper.convertValue(info, Map.class);
//         单条数据插入
            IndexRequest indexRequest = new IndexRequest(ElasticConstant.DYNAMIC_INDEX, ElasticConstant.DYNAMIC_TYPE, info.getPtfNoteId().toString())
                    .source(dataMap);
            try {
                IndexResponse response = restHighLevelClient.index(indexRequest);
            } catch (ElasticsearchException e) {
                e.getDetailedMessage();
            } catch (java.io.IOException ex) {
                ex.getLocalizedMessage();
            }
            logger.info("新闻资讯信息数据批量插入 Elasticsearch index 完成......");
            return;
        }
    }

    @Test
    public void demo() {
        List<String> list = new ArrayList<String>();
        list.add("/APP/UPLOAD/HEAD_PICTURE/2019/01/10/464f50f61fdc4ba19c892fb44da877de_640x640.png");
        list.add("https://kamluen02.oss-cn-hangzhou.aliyuncs.com/APP/UPLOAD/HEAD_PICTURE/2019/01/10/464f50f61fdc4ba19c892fb44da877de_640x640.png");
        list.add("-hangzhou.aliyuncs.com/APP/UPLOAD/HEAD_PICTURE/2019/01/10/464f50f61fdc4ba19c892fb44da877de_640x640.png");
        list.add("/2019/01/10/464f50f61fdc4ba19c892fb44da877de_640x640.png");

        for (String url : list) {
            if (url.contains("https")) {
                url = url;
            } else if (url.contains(writeKey)) {
                url = url.substring(writeKey.length());
                url = prefix + suffix + url;
            } else if (url.contains(suffix)) {
                url = prefix + url;
            } else {
                url = prefix + suffix + url;
            }
            System.out.println(url);
        }
    }

    /**
     * 全量导入用户信息数据
     */
    @Test
    public void insertBatchUserInfo() {
        logger.info("进入方法：insertBatchUserInfo...... start... ");
        // 先删除原有索引（因为es暂时没有提供清除所有doc的api），再创建索引并指定分词器，最后倒入数据
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(ElasticConstant.USER_INDEX));
            restHighLevelClient.indices().create(new CreateIndexRequest(ElasticConstant.USER_INDEX).settings(Settings.builder().put("analysis.analyzer.default.type", "ik_max_word")));
        } catch (Exception e) {
            logger.error("删除索引：{}异常，异常信息{}", ElasticConstant.USER_INDEX, e.getMessage());
        }
        EsUserInfo userInfo = new EsUserInfo();
        BulkRequest request = new BulkRequest();
        // 设置超时时间为5分钟
        request.timeout(TimeValue.timeValueMinutes(5));
        Page<UserInfo> page = new Page<UserInfo>(1, 2000);
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
//        Columns column = Columns.create()
//                .column("user_id")
//                .column("nick_name")
//                .column("user_icon")
//                .column("privacy")
//                .column("cell_phone")
//                .column("update_time");
//        wrapper.setSqlSelect(column);
        wrapper.select("user_id", "nick_name", "user_icon", "privacy", "cell_phone", "update_time");
        IPage<UserInfo> userInfos = userInfoService.page(page, wrapper);
        logger.info("查找到的用户数据集合size:" + userInfos.getRecords().size());
        try {
            if (null != userInfos && userInfos.getRecords().size() > 0) {
                for (UserInfo info : userInfos.getRecords()) {
                    try {
                        judgeIsNull(userInfo, info);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.info("在判断设置中出现异常,异常信息为:{}", e.getMessage());
                    }
                    logger.info("输出用户信息数据：" + JSONObject.toJSONString(userInfo));
                    //将数据转换为json格式写入
                    String str = JSONObject.toJSONString(userInfo);
                    request.add(new IndexRequest(ElasticConstant.USER_INDEX, ElasticConstant.USER_TYPE, String.valueOf(userInfo.getUserId()))
                            .source(str, XContentType.JSON));
                }
            } else {
                logger.info("查询数据库用户信息数据为空......");
                return;
            }
            logger.info("所有用户信息需要插入Elasticsearch中的数据请求包装成功，开始执行插入动作......");
            BulkResponse response = restHighLevelClient.bulk(request);
        } catch (ElasticsearchException e) {
            e.printStackTrace();
            e.getDetailedMessage();
            logger.error("用户信息数据批量插入 Elasticsearch 异常，异常信息：{}", e.getMessage());
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            ex.getLocalizedMessage();
            logger.error("用户信息数据批量插入 Elasticsearch 发生IO异常，异常信息：{}", ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("批量导入用户信息数据集合异常：{}", e.getMessage());
        }
        logger.info("股票基本信息数据批量插入 Elasticsearch 完成......");
        logger.info("结束方法：selectStockDataImportElasticsearch...... end... ");
        return;
    }

    private void judgeIsNull(EsUserInfo userInfo, UserInfo info) {
        logger.info("进入 judgeIsNull方法,查询到的数据为{}", info);
        userInfo.setUserId(info.getUserId().toString());
        userInfo.setuIdLong(IDTransUtil.encodeId(Long.parseLong(info.getUserId().toString()), SecurityKey.ID_KEY));
        userInfo.setNickName(info.getNickName());
        try {
            userCenterBasedUserAvatars(userInfo, info);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("用户信息设置数据异常,异常信息{}", e.getMessage());
        }
        userInfo.setPrivacy(info.getPrivacy());
        if (StringUtils.isNotNull(info.getCellPhone())) {
            //手机号解密存储
            userInfo.setCellPhone(ProtocolUtils.getDecryptPhone(info.getCellPhone()));
        } else {
            userInfo.setCellPhone("");
        }
        userInfo.setUpdateTime(info.getUpdateTime());
    }

    private void userCenterBasedUserAvatars(EsUserInfo userInfo, UserInfo info) {
        if (StringUtils.isNotNull(info.getUserIcon())) {
            if (info.getUserIcon().contains("https")) {
                userInfo.setUserIcon(info.getUserIcon());
            } else if (info.getUserIcon().contains(writeKey)) {
                String url = info.getUserIcon().substring(writeKey.length());
                userInfo.setUserIcon(prefix + suffix + url);
            } else if (info.getUserIcon().contains(suffix)) {
                userInfo.setUserIcon(prefix + info.getUserIcon());
            } else {
                userInfo.setUserIcon(prefix + suffix + info.getUserIcon());
            }
        } else {
            userInfo.setUserIcon(defaultImage);
        }
    }

    @Test
    public void testRedis() {
        String stockCode = "HSCCI.IDX.HK";
        HkexTsData data = redisMapService.findObject(HkexTsData.class, stockCode);
        System.out.println(JSONUtil.toCompatibleJson(data));
    }

    /**
     * 批量插入动态信息数据
     */
    @Test
    public void insertBatchDynamicInfo() {
        logger.info("进入方法：selectDynamicInformationToImportIntoES...... start... ");
        boolean flag = false;
        Integer ptfNoteId = 1;
        // 先删除原有索引（因为es暂时没有提供清除所有doc的api），再创建索引并指定分词器，最后倒入数据
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(ElasticConstant.DYNAMIC_INDEX));
            restHighLevelClient.indices().create(new CreateIndexRequest(ElasticConstant.DYNAMIC_INDEX).settings(Settings.builder().put("analysis.analyzer.default.type", "ik_max_word")));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("删除索引异常,{}的索引异常,异常信息为{}", ElasticConstant.DYNAMIC_INDEX, e.getMessage());
        }
        //开始从数据库导入数据到ES
        while (true) {
            if (flag) {
                List<Map<String, Object>> list = elasticsearchService.selectElasticsearch(ElasticConstant.DYNAMIC_INDEX, ElasticConstant.DYNAMIC_TYPE, ElasticConstant.PTF_NOTE_ID, null, 1, 1, false);
                if (null != list && list.size() > 0) {
                    for (Map<String, Object> map : list) {
                        ptfNoteId = (Integer) map.get(ElasticConstant.PTF_NOTE_ID);
                    }
                }
                logger.info("输出当前ES索引dynamic_index中最大的ptfNoteId：" + ptfNoteId);
            }
            EsPtfNoteInfo eNInfo = new EsPtfNoteInfo();
            // 每次查询导入2000条
            Page<PtfNoteInfo> page = new Page<PtfNoteInfo>(1, 2000);
            QueryWrapper<PtfNoteInfo> wrapper = new QueryWrapper<>();
//            Columns columns = Columns.create();
////            columns.column("ptf_note_id")
////                    .column("user_id")
////                    .column("note_type")
////                    .column("bus_content")
////                    .column("is_status")
////                    .column("update_time");
////            wrapper.setSqlSelect(columns);
            wrapper.select("ptf_note_id", "user_id", "note_type", "bus_content", "is_status", "update_time");
            //只查询有效的动态信息
            wrapper.gt("ptf_note_id", ptfNoteId);
            wrapper.eq("is_status", 1);
            IPage<PtfNoteInfo> ptfNoteInfo = dynamicInfoService.page(page, wrapper);
            BulkRequest request = new BulkRequest();
            request.timeout(TimeValue.timeValueMinutes(5));
            if (null != ptfNoteInfo && ptfNoteInfo.getRecords().size() > 0) {
                for (PtfNoteInfo ptfNote : ptfNoteInfo.getRecords()) {
                    //将bus_content转换为map字段
                    Map<String, Object> map = (Map<String, Object>) JSONObject.parse(ptfNote.getBusContent());
                    logger.info("在bus_content转换为map后取值,数据为: {}", map);
                    judgeValueByTheKeyOrSetting(ptfNote, eNInfo, map);
                    logger.debug("输出动态信息数据：" + JSONObject.toJSONString(eNInfo));
                    //将数据转换为json格式写入
                    String str = JSONObject.toJSONString(eNInfo);
                    request.add(new IndexRequest(ElasticConstant.DYNAMIC_INDEX, ElasticConstant.DYNAMIC_TYPE, eNInfo.getPtfNoteId().toString())
                            .source(str, XContentType.JSON));
                }
            } else {
                logger.info("数据库没有大于ptfNoteId：" + ptfNoteId + "的新闻资讯信息数据......");
                return;
            }
            logger.info("动态信息数据封装完毕,准备执行插入请求,开始执行插入......");

            try {
                BulkResponse bulk = restHighLevelClient.bulk(request);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("动态信息数据插入elasticSearch产生IO异常,异常信息: {}", e.getMessage());
            } catch (ElasticsearchException es) {
                es.getDetailedMessage();
                logger.error("动态信息数据插入elasticSearch失败,失败原因: {}", es.getMessage());
            }

            logger.info("动态信息数据插入ElasticSearch完成......");
            logger.info("结束方法：selectDynamicInformationToImportIntoES...... end... ");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            flag = true;
        }
    }

    /**
     * 批量插入策略信息数据
     */
    @Test
    public void insertBatchStrategyInfo() {
        logger.info("进入方法 selectStrategyInformationToImportIntoES.... start...");
        //region -step1 先删除原有索引（因为es暂时没有提供清除所有doc的api），再创建索引并指定分词器，最后倒入数据
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(STRATEGY_INDEX));
            restHighLevelClient.indices().create(new CreateIndexRequest(STRATEGY_INDEX).settings(Settings.builder().put("analysis.analyzer.default.type", "ik_max_word")));
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("删除策略数据:{}索引时出现异常,异常信息为:{}", "", e.getMessage());
        }
        //endregion
        //region -step2 组装策略数据
        EsStrategyInfo esInfo = new EsStrategyInfo();
        // 每次查询导入20000条
        Page<StrategyInfo> page = new Page<StrategyInfo>(1, 20000);
        QueryWrapper<StrategyInfo> wrapper = new QueryWrapper<StrategyInfo>();
//        wrapper.setSqlSelect(
//                Columns.create()
//                        .column("strategy_id")
//                        .column("strategy_type")
//                        .column("stock_code")
//                        .column("user_id")
//                        .column("strategy_name")
//                        .column("strategy_explain")
//                        .column("publish_time")
//                        .column("test_status")
//                        .column("status")
//        );
        wrapper.select("strategy_id","strategy_type","stock_code","user_id","strategy_name","strategy_explain","publish_time","test_status","status","from_strategy_id", "strategy_cycle", "strategy_role", "publish_status", "create_time");
        wrapper.eq("publish_status", StrategyEnum.published.getCode());
        wrapper.ne("status", StrategyEnum.delete.getCode());
        IPage<StrategyInfo> pageInfo = strategyInfoService.page(page, wrapper);
        BulkRequest request = new BulkRequest();
        request.timeout(TimeValue.timeValueMillis(5));
        if (null != pageInfo && 0 != pageInfo.getRecords().size()) {
            for (StrategyInfo info : pageInfo.getRecords()) {
                BeanUtils.copyProperties(info, esInfo);
                splicingUserInformationBasedOnUserId(info, esInfo);
                whetherTrackOrder(esInfo);
                stockInformationIncludedInTheStrategy(info, esInfo);
                //将数据转换为json格式写入
                String str = JSONObject.toJSONString(esInfo);
                request.add(
                        new IndexRequest(
                                ElasticConstant.STRATEGY_INDEX,
                                ElasticConstant.STRATEGY_TYPE,
                                esInfo.getStrategyId().toString()
                        )
                                .source(str, XContentType.JSON)
                );

            }
        } else {
            logger.info("查询数据库未获取到策略相关数据......");
            return;
        }
        //endregion
        //region -step3 策略数据插入elasticSearch
        logger.info("策略数据封装完毕,准备执行插入请求,开始执行插入......");
        logger.info("策略数据封装完毕,封装结果:{}", request);
        try {
            BulkResponse response = restHighLevelClient.bulk(request);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("策略数据插入elasticSearch产生IO异常,异常信息为:{}", e);
        } catch (ElasticsearchException es) {
            es.getDetailedMessage();
            logger.error("策略数据插入elasticSearch失败,原因:{}", es);
        }
        logger.info("策略数据插入ElasticSearch完成......");
        //endregion
        logger.info("方法执行完成 selectStrategyInformationToImportIntoES.... end...");
    }

    /**
     * 根据用户id拼接用户信息
     *
     * @param info   策略信息
     * @param esInfo 策略信息ES检索类
     */
    private void splicingUserInformationBasedOnUserId(StrategyInfo info, EsStrategyInfo esInfo) {
        logger.info("根据用户id拼接用户信息,获取到的用户id为:{},准备存入es的实体信息为:{}, start....", info.getUserId() == null ? 0 : info.getUserId(), JSONUtil.toCompatibleJson(esInfo));
        //region setp 获取用户信息并拼接用户头像
        UserInfo userInfo = userInfoService.getById(info.getUserId());
        esInfo.setUserName("");
        esInfo.setUserIcon("");
        if (StringUtils.isNotNull(userInfo)) {
            esInfo.setUserName(userInfo.getNickName() == null ? "" : userInfo.getNickName());
            String picture = judgeUserInfoSetUserIcon(userInfo.getUserIcon() == null ? defaultImage : userInfo.getUserIcon());
            esInfo.setUserIcon(picture);
        }
        //endregion
        logger.info("根据用户id拼接用户信息,获取到的用户id为:{},准备存入es的实体信息为:{}, end....", info.getUserId() == null ? 0 : info.getUserId(), JSONUtil.toCompatibleJson(esInfo));
    }

    /**
     * 是否跟单
     *
     * @param esInfo 策略信息ES检索类
     */
    private void whetherTrackOrder(EsStrategyInfo esInfo) {
        Integer userId = esInfo.getUserId();
        logger.info("whether Track Order is userId:{}....start...", userId);
        Set<String> followSet = redisSetService.findALL(SUFFIX + userId + PREFIX);
        followSet.forEach(s -> {
            logger.info("获取到的跟单策略id为:{}", s);
        });
        esInfo.setFollowed(followSet.contains(esInfo.getStrategyId().toString()) ? 1 : 0);
    }

    /**
     * 策略中包含的股票信息
     *
     * @param info   策略信息
     * @param esInfo 策略信息ES检索类
     */
    private void stockInformationIncludedInTheStrategy(StrategyInfo info, EsStrategyInfo esInfo) {
        String stockCode = info.getStockCode() != null ? info.getStockCode() : "";
        logger.info("策略中包含股票信息,Stock Code In The Strategy:{}.....start....", stockCode);
        QuotationVO quotationVO = queryDataFromRedis(stockCode);
        String stockName = quotationVO.getStkName() != null ? quotationVO.getStkName() : "";
        esInfo.setStockName(stockName);
        logger.info("策略中包含股票信息,Stock Code In The Strategy:{} The Stock Name Obtained : {},.....end....", stockCode, stockName);
    }

    /**
     * 从Redis查询数据
     *
     * @param stockCode 股票代码
     * @return QuotationVO
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
            return quotationVO;
        }
        return quotationVO;
    }

    /**
     * 判断用户信息设置用户图标
     *
     * @param userIcon 用户图标
     * @return 用户图标
     */
    private String judgeUserInfoSetUserIcon(String userIcon) {
        if (defaultImage.equals(userIcon)) {
            return userIcon;
        }
        if (HTTPS.contains(userIcon)) {
            return userIcon;
        } else if (writeKey.contains(userIcon)) {
            return prefix + suffix + userIcon.substring(writeKey.length());
        } else if (suffix.contains(userIcon)) {
            return prefix + userIcon;
        } else {
            return prefix + suffix + userIcon;
        }
    }

    private void judgeValueByTheKeyOrSetting(PtfNoteInfo ptfNote, EsPtfNoteInfo eNInfo, Map<String, Object> map) {
        logger.info("进入judgeValueByTheKeyOrSetting start... 动态信息数据 {}", ptfNote);
        try {
            UserInfo userInfo = userInfoService.getById(ptfNote.getUserId());
            logger.info("根据动态信息中的 userId {} ,查询到的用户信息 {}", ptfNote.getUserId(), userInfo);
            if (StringUtils.isNotNull(userInfo)) {
                dynamicCenterBasedUserAvatars(eNInfo, userInfo);
            } else {
                eNInfo.setUserIcon("");
                eNInfo.setUserName("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询用户信息数据异常,异常信息{}", e.getMessage());
        }
        eNInfo.setPtfNoteId(ptfNote.getPtfNoteId());
        eNInfo.setNoteType(ptfNote.getNoteType());
        eNInfo.setBusContent(ptfNote.getBusContent());
        eNInfo.setIsLimit(0);
        eNInfo.setIsReal(0);
        eNInfo.setPerm(0);
        //将用户ID转换成longID
        eNInfo.setUserId(ptfNote.getUserId());
        eNInfo.setUIdLong(IDTransUtil.encodeId(Long.parseLong(ptfNote.getUserId().toString()), SecurityKey.ID_KEY));
        eNInfo.setUpdateTime(ptfNote.getUpdateTime());
        try {
            JudgeValueByTheKey(eNInfo, map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("在bus_content转换为map后取值出现异常,异常信息{}", e.getMessage());
        }
    }

    /**
     * 基于用户头像
     *
     * @param eNInfo   ES中的动态信息
     * @param userInfo 用户信息
     */
    private void dynamicCenterBasedUserAvatars(EsPtfNoteInfo eNInfo, UserInfo userInfo) {
        if (StringUtils.isNotNull(userInfo)) {
            eNInfo.setUserName(userInfo.getNickName());
        } else {
            eNInfo.setUserName("");
        }
        if (StringUtils.isNotNull(userInfo.getUserIcon())) {
            if (userInfo.getUserIcon().contains("https")) {
                eNInfo.setUserIcon(userInfo.getUserIcon());
            } else if (userInfo.getUserIcon().contains(writeKey)) {
                String url = userInfo.getUserIcon().substring(writeKey.length());
                eNInfo.setUserIcon(prefix + suffix + url);
            } else if (userInfo.getUserIcon().contains(suffix)) {
                eNInfo.setUserIcon(prefix + userInfo.getUserIcon());
            } else {
                eNInfo.setUserIcon(prefix + suffix + userInfo.getUserIcon());
            }
        } else {
            eNInfo.setUserIcon(defaultImage);
        }
    }

    /**
     * 根据键判断值
     *
     * @param eNInfo ES中的动态信息
     * @param map    json转换为map后的对象
     */
    private void JudgeValueByTheKey(EsPtfNoteInfo eNInfo, Map<String, Object> map) {
        if (StringUtils.isNotNull(map.get("atIds"))) {
            eNInfo.setAtIds((List<Integer>) map.get("atIds"));
        } else {
            eNInfo.setAtIds(new ArrayList<>());
        }
        if (StringUtils.isNotNull(map.get("assets"))) {
            eNInfo.setAssets((List<String>) map.get("assets"));
        } else {
            eNInfo.setAssets(new ArrayList<>());
        }

        if (StringUtils.isNotNull(map.get("artId"))) {
            eNInfo.setArtId(IDTransUtil.encodeId(Long.valueOf(map.get("artId").toString()), SecurityKey.ID_KEY).intValue());
        } else {
            //没有的全部为-1
            eNInfo.setArtId(-1);
        }
        if (StringUtils.isNotNull(map.get("categoryName"))) {
            eNInfo.setCategoryName(map.get("categoryName").toString());
        } else {
            eNInfo.setCategoryName("");
        }
        if (StringUtils.isNotNull(map.get("content"))) {
            eNInfo.setContent(map.get("content").toString());
        } else {
            eNInfo.setContent("");
        }
        if (StringUtils.isNotNull(map.get("noteTitle"))) {
            eNInfo.setNoteTitle(map.get("noteTitle").toString());
        } else {
            eNInfo.setNoteTitle("");
        }
        if (StringUtils.isNotNull(map.get("title"))) {
            eNInfo.setTitle(map.get("title").toString());
        } else {
            eNInfo.setTitle("");
        }
        if (StringUtils.isNotNull(map.get("newsImg"))) {
            eNInfo.setNewsImg(map.get("newsImg").toString());
        } else {
            eNInfo.setNewsImg("");
        }
        if (StringUtils.isNotNull(map.get("urls"))) {
            List<String> address = new ArrayList<>();
            JSONArray array = JSONArray.parseArray(map.get("urls").toString());
            for (Object url : array) {
                String dynamicInformation = determineWhetherTheUrlIsCorrectBasedOnTheDynamicInformation(String.valueOf(url));
                address.add(dynamicInformation);
            }
            eNInfo.setUrls(address);
        } else {
            eNInfo.setUrls(new ArrayList<>());
        }
        if (StringUtils.isNotNull(map.get("type"))) {
            eNInfo.setType(Integer.valueOf(map.get("type").toString()));
        } else {
            eNInfo.setType(0);
        }
    }

    private String determineWhetherTheUrlIsCorrectBasedOnTheDynamicInformation(String url) {
        String dynamicUrl = "";
        if (url.contains(ElasticConstant.DYNAMIC_WRONG_DATA)) {
            url = url.substring(ElasticConstant.DYNAMIC_WRONG_DATA.length());
            dynamicUrl = prefix + dynamic_suffix + url;
        } else if (url.contains(dynamic_suffix)) {
            dynamicUrl = prefix + url;
        } else if (url.contains(ElasticConstant.DYNAMIC_WRONG_SUFFIX)) {
            url = url.substring(ElasticConstant.DYNAMIC_WRONG_SUFFIX.length());
            dynamicUrl = prefix + dynamic_suffix + url;
        } else {
            dynamicUrl = prefix + dynamic_suffix + url;
        }
        return dynamicUrl;
    }

    /**
     * 通过Atids判断装配参数
     *
     * @param map
     */
    private void JudgeAssembleParametersByAtids(Map<String, Object> map) {
        logger.info("根据id装配参数,@Ids数据为{}", map.get("atIds"));
        List<Integer> atIds = (List<Integer>) map.get("atIds");
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> mapId = new HashMap<>();
        try {
            for (Integer atId : atIds) {
                QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
//                Columns columns = Columns.create().column("user_id").column("nick_name");
//                wrapper.setSqlSelect(columns);
                wrapper.select("user_id", "nick_name");
                wrapper.eq("user_id", atId);
                UserInfo userInfo = userInfoService.getOne(wrapper);
                logger.info("根据@id拿到的用户信息为: {}", userInfo);
                mapId.put("longId", IDTransUtil.encodeId(Long.valueOf(atId), SecurityKey.ID_KEY));
                if (StringUtils.isNotNull(userInfo)) {
                    mapId.put("nickName", userInfo.getNickName());
                } else {
                    mapId.put("nickName", "");
                }
                list.add(mapId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mapId.put("longId", -1);
            mapId.put("nickName", "");
        }
        map.put("atIds", list);
        logger.info("根据id装配参数,最终@Ids数据为{}", map.get("atIds"));
    }

    @Test
    public void demo01() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> mapId = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        List<Integer> ids = new ArrayList<>();
        ids.add(100001);
        ids.add(100002);
        ids.add(100003);
        ids.add(100004);
        ids.add(100402);
        map.put("atIds", ids);
        JSONArray atIds = JSONArray.parseArray(map.get("atIds").toString());
        for (Object atId : atIds) {
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
//            Columns columns = Columns.create().column("user_id").column("nick_name");
//            wrapper.setSqlSelect(columns);
            wrapper.select("user_id", "nick_name");
            wrapper.eq("user_id", atId);
            UserInfo userInfo = userInfoService.getOne(wrapper);
            logger.info("根据@id拿到的用户信息为: {}", userInfo);
            if (userInfo != null) {
                mapId.put("longId", IDTransUtil.encodeId(Long.parseLong(atId.toString()), SecurityKey.ID_KEY));
                mapId.put("nickName", userInfo.getNickName());
            }
            list.add(mapId);
        }
        map.put("atIds", list);
        System.out.println(map);


    }

    /**
     * 批量插入新闻资讯信息数据
     */
    @Test
    public void insertBatchNewsInfo() {
        logger.info("进入方法：selectNewsDataImportElasticsearch...... start... ");
        Integer labelNewsId = 1;
        boolean sign = false;
        // 先删除原有索引（因为es暂时没有提供清除所有doc的api），再创建索引并指定分词器，最后倒入数据
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(ElasticConstant.NEWS_INDEX));
            restHighLevelClient.indices().create(new CreateIndexRequest(ElasticConstant.NEWS_INDEX).settings(Settings.builder().put("analysis.analyzer.default.type", "ik_max_word")));
        } catch (Exception e) {
            logger.error("删除索引：{}异常，异常信息{}", ElasticConstant.NEWS_INDEX, e.getMessage());
        }
        while (true) {
            if (sign) {
                List<Map<String, Object>> list = elasticsearchService.selectElasticsearch(ElasticConstant.NEWS_INDEX, ElasticConstant.NEWS_TYPE, ElasticConstant.LABEL_NEWS_ID, null, 1, 1, false);
                if (list != null && list.size() > 0) {
                    for (Map<String, Object> map : list) {
                        labelNewsId = (Integer) map.get(ElasticConstant.LABEL_NEWS_ID);
                    }
                }
                logger.info("输出当前ES索引news_index中最大的labelNewsId：" + labelNewsId);
            }
            Page<NewsInfo> page = new Page<NewsInfo>(1, 20000); // 每次查询导入2000条
            QueryWrapper<NewsInfo> ew = new QueryWrapper<>();
            // 获取新闻资讯信息数据导入ES，过滤不可见的资讯，即过滤：is_status=0
            ew.gt("label_news_id", labelNewsId);
            ew.eq("is_status", 1);
            IPage<NewsInfo> newsInfos = newsInfoService.page(page, ew);
            BulkRequest request = new BulkRequest();
            request.timeout(TimeValue.timeValueMinutes(5));  // 设置超时时间为5分钟
            EsNewsInfo ens = new EsNewsInfo();
            if (newsInfos != null && newsInfos.getRecords().size() > 0) {
                for (NewsInfo info : newsInfos.getRecords()) {
                    ens.setLabelNewsId(info.getLabelNewsId());
                    ens.setTitle(info.getTitle());
                    ens.setSpell(PinYinUtils.getPingYin(info.getTitle()));
                    ens.setSpellAbbr(PinYinUtils.getFirstSpell(info.getTitle()));
                    ens.setIssueTime(info.getIssueTime());
                    ens.setGp(info.getGp());
                    ens.setTag(info.getTag());
                    ens.setImgUrl(info.getImgUrl());
                    if (StringUtils.isNEmpty(info.getImgUrl())) {
                        ens.setInfotreeid(Integer.valueOf(ENewsEnums.IMPORTANT_NEWS.getTypeValue()));
                    } else {
                        ens.setInfotreeid(Integer.valueOf(ENewsEnums.STK_NEWS.getTypeValue()));
                    }
                    if (StringUtils.isNEmpty(info.getNewsSource())) {
                        ens.setNewsSource(info.getNewsSource());
                    } else {
                        ens.setNewsSource("格隆汇");
                    }
                    logger.debug("输出新闻资讯信息数据：" + JSONObject.toJSONString(ens));
                    // 将数据转换为json格式写入
                    String jsonData = JSONObject.toJSONString(ens);
                    // 批量数据插入,即：将数据批量写入请求，统一执行
                    request.add(new IndexRequest(ElasticConstant.NEWS_INDEX, ElasticConstant.NEWS_TYPE, info.getLabelNewsId().toString())
                            .source(jsonData, XContentType.JSON));
                }
            } else {
                logger.info("数据库没有大于labelNewsId：" + labelNewsId + "的新闻资讯信息数据......");
                return;
            }
            logger.info("所有新闻资讯信息需要插入Elasticsearch中的数据请求包装成功，开始执行插入动作......");
            try {
                BulkResponse response = restHighLevelClient.bulk(request);
            } catch (ElasticsearchException e) {
                e.getDetailedMessage();
                logger.error("新闻资讯信息数据批量插入 Elasticsearch 异常，异常信息：{}", e.getMessage());
            } catch (java.io.IOException ex) {
                ex.getLocalizedMessage();
                logger.error("新闻资讯信息数据批量插入 Elasticsearch 发生IO异常，异常信息：{}", ex.getMessage());
            }
            logger.info("新闻资讯信息数据批量插入 Elasticsearch 完成......");
            logger.info("结束方法：selectNewsDataImportElasticsearch...... end... ");
            try {
                Thread.sleep(3000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sign = true;
        }
//        return;
    }

    /**
     * 批量插入股票基本信息数据
     */
    @Test
    public void insertBatchStockInfo() {
        // 先删除原有索引（因为es暂时没有提供清除所有doc的api），再创建索引，并指定分词器，最后倒入数据
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(ElasticConstant.STOCK_INDEX));
            restHighLevelClient.indices().create(new CreateIndexRequest(ElasticConstant.STOCK_INDEX).settings(Settings.builder().put("analysis.analyzer.default.type", "ik_max_word")));
        } catch (Exception e) {
            logger.error("删除索引：{}异常，异常信息{}", ElasticConstant.STOCK_INDEX, e.getMessage());
        }
        QueryWrapper<StockInfo> ew = new QueryWrapper<StockInfo>();
        // 测试获取股票信息数据导入ES，过滤已经退市的股票
        ew.eq("is_status", 1);
        ew.and(wrapper -> wrapper.isNull("delist_date").or().gt("delist_date", DateUtils.getCurrentDate()));
//        ew.isNull("delist_date").or().gt("delist_date", DateUtils.getCurrentDate());
        // 1：股票(正股)，2：债券，3：基金，4：权证(涡轮·牛熊)，5：指数
        ew.and(wrapper -> wrapper.notIn("sec_type", 2, 3));
//        ew.notIn("sec_type", 2, 3);
        List<StockInfo> stockInfos = stockInfoService.list(ew);
        logger.info("输出需要导入ES未退市的股票信息集合size：" + stockInfos.size());
        BulkRequest request = new BulkRequest();
        EsStockInfo ens = new EsStockInfo();
        if (stockInfos != null && stockInfos.size() > 0) {
            for (StockInfo info : stockInfos) {
                // 子类型过滤
                if (info.getSecType() == EStkType.FUND.getNo() && info.getSecStype() != ESubStkType.HK_FUND_ETF.getSubType()) {
                    continue;
                }
                ens.setAssetId(info.getAssetId());
                ens.setStkCode(info.getStkCode());
                ens.setStkName(info.getStkName() == null ? "" : info.getStkName());
                ens.setSpelling(PinYinUtils.getPingYin(info.getStkName() == null ? "" : info.getStkName()));
                ens.setSpellingAbbr(PinYinUtils.getFirstSpell(info.getStkName() == null ? "" : info.getStkName()));
                ens.setStkType(info.getSecStype()); // 细分类别
                ens.setSecType(info.getSecType());  // 证券类别
                ens.setMkt(StkUtils.determineMarketCode(info.getAssetId()));
                if (info.getAssetId().endsWith(ElasticConstant.HK)) {
                    ens.setMktType(1);
                } else if (info.getAssetId().endsWith(ElasticConstant.SZ)) {
                    ens.setMktType(2);
                } else if (info.getAssetId().endsWith(ElasticConstant.SH)) {
                    ens.setMktType(3);
                } else if (info.getAssetId().endsWith(ElasticConstant.US)) {
                    ens.setMktType(4);
                }
//            logger.info("输出股票基本信息数据：" + JSONObject.toJSONString(ens));
                String jsonData = JSONObject.toJSONString(ens);
                // 批量数据插入,即：将数据批量写入请求，统一执行
                request.add(new IndexRequest(ElasticConstant.STOCK_INDEX, ElasticConstant.STOCK_TYPE, info.getAssetId())
                        .source(jsonData, XContentType.JSON));
            }
        } else {
            logger.info("查询数据库股票信息数据为空......");
            return;
        }
        logger.info("所有需要插入Elasticsearch中的数据请求包装成功，开始执行插入动作......");
        try {
            BulkResponse response = restHighLevelClient.bulk(request);
        } catch (ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex) {
            ex.getLocalizedMessage();
        }
        logger.info("股票基本信息数据批量插入 Elasticsearch index 完成......");
        return;
    }

    /**
     * 测试ES查询
     */
    @Test
    public void testElasticSearch() {
        Integer userIds = 100687;

        String value = "*啦啦*";
//        String value = "*13312963452*";
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        request.indices(ElasticConstant.DYNAMIC_INDEX);
        request.types(ElasticConstant.DYNAMIC_TYPE);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);
        //根据 NOTE_TITLE , DYNAMIC_TITLE, CONTENT 检索
        if (StringUtils.isChineseChar(value)) {
            searchSourceBuilder.query(
                    QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery(ElasticConstant.NOTE_TITLE, value))
                            .should(QueryBuilders.matchPhraseQuery(ElasticConstant.DYNAMIC_TITLE, value))
                            .should(QueryBuilders.matchPhraseQuery(ElasticConstant.CONTENT, value))
            );
        } else {
            searchSourceBuilder.query(
                    QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery(ElasticConstant.NOTE_TITLE, value))
                            .should(QueryBuilders.wildcardQuery(ElasticConstant.DYNAMIC_TITLE, value))
                            .should(QueryBuilders.wildcardQuery(ElasticConstant.CONTENT, value))
            );
        }
        request.source(searchSourceBuilder);
        logger.info("请求对象" + request);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("查询Elasticsearch，执行selectElasticsearch方法，获取数据信息异常：{}", e.getMessage());
        }
        SearchHit[] hits = null;

        if (response != null && response.getHits() != null) {
            hits = response.getHits().getHits();
            logger.info("当前匹配到数据size：" + response.getHits().totalHits);
        }
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            String[] indices = request.indices();
            for (String index : indices) {
                if (index.equals(ElasticConstant.DYNAMIC_INDEX)) {
                    try {
                        judgeIsWhatState(userIds, map);
                        List<Long> atIds = (List<Long>) map.get("atIds");
                        if (atIds.size() > 0) {
                            JudgeAssembleParametersByAtids(map);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.info("判断是什么状态的时候发生了异常,异常信息为 {}", e.getMessage());
                    }
                }
            }
            System.out.println("=========>" + map);
        }
    }

    private void judgeIsWhatState(Integer userIds, Map<String, Object> map) {
        logger.info("进入 judgeIsWhatState 方法");
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
                    logger.info("进入了是否收藏");
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
                    logger.info("进入了是否关注");
                    map.put("isReal", 1);
                }
            }
        }
        if (deletablePtfNoteType(map.get("noteType").toString()) && userIds.equals(map.get("userId"))) {
            logger.info("进入了是否有权限");
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

    private void judgeTheFieldType(String type, String fieldName, String stkName, String spelling, String stkCode) {
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
    }

    /**
     * 判断是新闻还是资讯
     *
     * @param condition 字段对应的值(该值为部分数据，支持模糊查询)
     * @param fieldName 要检索的字段名
     * @param status    状态 (1 : 股票, 2 : 资讯)
     */
    private void judgeIsInformationOrNews(String condition, String fieldName, Integer status) {
        String type = StkUtils.judgeCondition(condition);
        switch (status) {
            case CombinedSearch.StockStatus:
                judgeTheFieldType(type, fieldName, ElasticConstant.STK_NAME, ElasticConstant.SPELLING, ElasticConstant.STK_CODE);
                break;
            case CombinedSearch.StateInformation:
                judgeTheFieldType(type, fieldName, ElasticConstant.TITLE, ElasticConstant.SPELL, ElasticConstant.GP);
                break;
        }
    }

    /**
     * 综合查询
     */
    @Test
    public void testSearch() {
        CommonEsInfoReqVo vo = new CommonEsInfoReqVo();
        ResponseVO responseVO = new ResponseVO();
        vo.setCondition("ios");
        vo.setUserId(102625);
        vo.setType(5);  //查找全部

        List<ResponseVO> list = new ArrayList<>();
        try {
            list = combinedSearchService.combinedSelection(vo);
        } catch (Exception e) {
            e.printStackTrace();
            responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
            responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
            logger.error("综合搜索时发生异常,异常信息{}", e.getMessage());
            list.add(responseVO);
        }
        logger.info("访问综合搜索接口,获取到的最终数据为: {}", JSON.toJSON(list));
    }

    @Test
    public void testALLElasticSearch() {
        CommonEsInfoReqVo vo = new CommonEsInfoReqVo();
        vo.setCondition("47008");
        vo.setRowsPerPage(20);
        CacheManager.putCacheContent("userIds", 100687, System.currentTimeMillis());
        ResponseVO responseVO = new ResponseVO();
        List<ResponseVO> respList = new ArrayList<>();
        Integer status = 0;
        //ES中需要检索的字段名
        String fieldName = "";
        String[] strings = {ElasticConstant.DYNAMIC_INDEX, ElasticConstant.USER_INDEX};
        //ES中的type名称
        String typeName = "";
        for (String indexName : strings) {
            switch (indexName) {
                case ElasticConstant.NEWS_INDEX:
                    typeName = ElasticConstant.NEWS_TYPE;
                    status = 2;
                    judgeIsInformationOrNews(vo.getCondition(), fieldName, status);
                    break;
                case ElasticConstant.STOCK_INDEX:
                    typeName = ElasticConstant.STOCK_TYPE;
                    status = 1;
                    judgeIsInformationOrNews(vo.getCondition(), fieldName, status);
                    break;
                case ElasticConstant.DYNAMIC_INDEX:
                    typeName = ElasticConstant.DYNAMIC_TYPE;
                    break;
                case ElasticConstant.USER_INDEX:
                    typeName = ElasticConstant.USER_TYPE;
                    if (StringUtils.isPhoneNumber(vo.getCondition())) {
                        fieldName = ElasticConstant.USER_PHONE;
                    }
                    break;
            }
            List<Map<String, Object>> list = elasticsearchService.selectElasticsearch(indexName, typeName, fieldName, vo.getCondition(), vo.getRowsPerPage(), vo.getCurrentPage(), false);
            if (list != null && list.size() > 0) {
                EsNewsInfoRespVo respVo = new EsNewsInfoRespVo();
                respVo.setResultInfo(list);
                responseVO.setResult(respVo);
            }
            respList.add(responseVO);
            Map<String, List<ResponseVO>> map = new HashMap<>();
            map.put("list", respList);
            System.out.println(map);
        }
    }

    /**
     * 根据ID获取新闻资讯信息数据
     */
    @Test
    public void selectEsDataById() {
//        String id = "53781";
//        String id = "55146";
        String id = "55157";
        GetRequest getRequest = new GetRequest(ElasticConstant.STOCK_TAG_INDEX, ElasticConstant.STOCK_TAG_TYPE, id);
        GetResponse getResponse = null;
        try {
            getResponse = restHighLevelClient.get(getRequest);
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
            logger.error("根据ID获取ES数据异常，异常信息：{}", e.getMessage());
        }
        List<Map<String, String>> resultList = new ArrayList<>();
        if (StringUtils.isNotNull(getResponse.getSourceAsMap())) {
            if (StringUtils.isNotNull(getResponse.getSourceAsMap().get(ElasticConstant.STOCK_TAGS))) {
                List<Object> list = JsonUtils.parseJson(ElasticConstant.STOCK_TAGS, JSONObject.toJSONString(getResponse.getSourceAsMap()));
                // 做一级数据包装并返回
                if (list != null && list.size() > 0) {
                    for (Object obj : list) {
                        JsonObject jsonObject = (JsonObject) obj;
                        Map<String, String> map = new HashMap<>();
                        map.put("stkCode", jsonObject.get("id").getAsString());
                        map.put("stkName", jsonObject.get("name").getAsString());
                        resultList.add(map);
                    }
                }
            }
        }
        logger.info("输出解析之后的信息数据：{}", JSONObject.toJSONString(resultList));
        return;
    }

    @Test
    public void dateToString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.print("日期转换输出：" + sdf.format(new Date(Long.valueOf(1519796400000l))));
    }

    @Test
    public void chineseToEnglish() {
        String string = "宝燵控股";
        string = PinYinUtils.getPingYin(string);
        System.out.println("输出转换之后的字符串：" + string);
        string = "宝燵控股";
        string = PinYinUtils.getFirstSpell(string);
        System.out.println("输出转换之后的字符串：" + string);
    }

    @Test
    public void getNewsInfoByGp() {
        List<String> lists = new ArrayList<>();
        lists.add("000001.SZ");
        lists.add("000002.SZ");
        lists.add("000003.SZ");
        lists.add("000004.SZ");
        lists.add("000005.SZ");
        lists.add("000006.SZ");
        lists.add("000007.SZ");
        lists.add("000008.SZ");
        lists.add("000009.SZ");
        lists.add("000010.SZ");
        lists.add("000011.SZ");
        lists.add("000012.SZ");
        lists.add("000013.SZ");
        lists.add("000014.SZ");
        lists.add("000015.SZ");
        lists.add("000016.SZ");
        lists.add("000017.SZ");
        lists.add("000018.SZ");
        lists.add("000019.SZ");
        lists.add("000020.SZ");
        lists.add("000021.SZ");
        lists.add("000022.SZ");
        lists.add("000023.SZ");
        lists.add("000024.SZ");
        lists.add("000025.SZ");
        lists.add("000026.SZ");
        lists.add("000027.SZ");
        lists.add("000028.SZ");
        lists.add("000029.SZ");
        lists.add("000030.SZ");
        lists.add("000031.SZ");
        lists.add("000032.SZ");
        lists.add("000033.SZ");
        lists.add("000034.SZ");
        lists.add("000035.SZ");
        lists.add("000036.SZ");
        lists.add("000037.SZ");
        lists.add("000038.SZ");
        lists.add("000039.SZ");
        lists.add("000040.SZ");
        lists.add("000042.SZ");
        lists.add("000043.SZ");
        lists.add("000045.SZ");
        lists.add("000046.SZ");
        lists.add("000047.SZ");
        lists.add("000048.SZ");
        lists.add("000049.SZ");
        lists.add("000050.SZ");
        lists.add("000055.SZ");
        lists.add("000056.SZ");
        lists.add("000058.SZ");
        lists.add("000059.SZ");
        lists.add("000060.SZ");
        lists.add("000061.SZ");
        lists.add("000062.SZ");
        lists.add("000063.SZ");
        lists.add("000065.SZ");
        lists.add("000066.SZ");
        lists.add("000068.SZ");
        lists.add("000069.SZ");
        lists.add("000070.SZ");
        lists.add("000078.SZ");
        lists.add("000088.SZ");
        lists.add("000089.SZ");
        lists.add("000090.SZ");
        lists.add("000096.SZ");
        lists.add("000099.SZ");
        lists.add("000100.SZ");
        lists.add("000150.SZ");
        lists.add("000151.SZ");
        lists.add("000153.SZ");
        lists.add("000155.SZ");
        lists.add("000156.SZ");
        lists.add("000157.SZ");
        lists.add("000158.SZ");
        lists.add("000159.SZ");
        lists.add("000166.SZ");
        lists.add("000159.SZ");
        lists.add("000158.SZ");
        lists.add("000157.SZ");
        lists.add("000156.SZ");
        lists.add("000155.SZ");
        lists.add("000153.SZ");
        lists.add("000151.SZ");
        lists.add("000150.SZ");
        lists.add("00012.HK");
        lists.add("00013.HK");
        lists.add("00014.HK");
        lists.add("00015.HK");
        lists.add("00006.HK");
        lists.add("00005.HK");
        lists.add("00004.HK");
        lists.add("00003.HK");
        lists.add("00001.HK");
        lists.add("00002.HK");
        lists.add("00007.HK");
        lists.add("00008.HK");
        lists.add("00009.HK");
        lists.add("00010.HK");
        lists.add("00011.HK");
        lists.add("00016.HK");
        lists.add("00017.HK");
        lists.add("00018.HK");
        lists.add("00019.HK");
        lists.add("00020.HK");
        lists.add("00021.HK");
        lists.add("00022.HK");
        lists.add("00023.HK");
        lists.add("00029.HK");
        lists.add("00028.HK");
        lists.add("00027.HK");
        lists.add("00026.HK");
        lists.add("00025.HK");
        lists.add("00024.HK");
        lists.add("00023.HK");
        lists.add("00022.HK");
        lists.add("00021.HK");
        lists.add("00020.HK");
        lists.add("00019.HK");
        lists.add("00018.HK");
        lists.add("00017.HK");

        System.out.println("输出需要匹配的股票信息size：" + lists.size());
        Integer labelNewsId = 0;
        Integer currentPage = 1;
        Integer rowsPerPage = 20;
        String stockCode = "";
        Long startTime = System.currentTimeMillis();
        List<Map<String, Object>> list = elasticsearchService.multiToOneSelectByEs(ElasticConstant.NEWS_INDEX, ElasticConstant.NEWS_TYPE, ElasticConstant.GP, lists, rowsPerPage, currentPage, labelNewsId);
        System.out.println("输出ES检索耗时：" + (System.currentTimeMillis() - startTime) + " ms");
        System.out.println("输出匹配到的返回结果信息大小：" + JSONObject.toJSONString(list.size()));
        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
//                if (map.containsKey(ElasticConstant.GP)) {
//                    stockCode = map.get(ElasticConstant.GP).toString();
//                    if(stockCode.contains(",")){
//                        if (!lists.contains(stockCode.split("\\,")[0])) {
//                            continue;
//                        }
//                    } else {
//                        if (!lists.contains(stockCode)) {
//                            continue;
//                        }
//                    }
//                    logger.info("输出匹配到的返回结果信息labelNewsId：{}, dateTime：{}, title：{}，gp：{}", map.get("labelNewsId"), map.get("issueTime"), map.get("title"), map.get("gp"));
//                }
                logger.info("输出匹配到的返回结果信息labelNewsId：{}, dateTime：{}, title：{}，gp：{}", map.get("labelNewsId"), map.get("issueTime"), map.get("title"), map.get("gp"));
            }

        }
    }

    /**
     * 数据源切换测试
     */
    @Test
    public void dataSource() {
        System.out.println("执行切换数据源方法......");
        List<UserInfo> userInfos = userInfoService.list(null);
        logger.info("查找所有用户数据集合size:" + userInfos.size());
//        if (userInfos != null && userInfos.size() > 0){
//            userInfos.forEach(info -> System.out.println("输出用户信息：" + JSONObject.toJSONString(info)));
//        }

        System.out.println("=============================================================================================");

        QueryWrapper<StockInfo> ew = new QueryWrapper<StockInfo>();
        // 测试获取股票信息数据导入ES，过滤已经退市的股票
        List<StockInfo> stockInfos = stockInfoService.list(null);
        logger.info("输出需要导入ES未退市的股票信息集合size：" + stockInfos.size());
//        if (stockInfos != null && stockInfos.size() > 0){
//            stockInfos.forEach(info -> System.out.println("输出股票信息：" + JSONObject.toJSONString(info)));
//        }

        System.out.println("=============================================================================================");

        List<PtfFav> ptfFavs = ptfFavService.list(null);
        logger.info("查找所有关注表数据集合size:" + ptfFavs.size());

        System.out.println("结束切换数据源方法......");
    }

    public static void main(String[] args) {
        String type = "1";
        String str = "";
        test(type, str);
        System.out.println("main方法输出str: " + str);
    }

    public static void test(String type, String str) {
        switch (type) {
            // 表示传入值为中文
            case "0":
                str = "0";
                break;
            // 表示传入值为字母
            case "1":
                str = "1";
                break;
            // 表示传入值为数字
            case "2":
                str = "2";
                break;
        }
        System.out.println("test方法输出str: " + str);
    }
}
