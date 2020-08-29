package com.kamluen.elasticsearch;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.service.*;
import com.kamluen.elasticsearch.utils.*;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.ElasticsearchException;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Elastic6ApplicationTests {

    private static Logger logger = LoggerFactory.getLogger(Elastic6ApplicationTests.class);

    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ElasticsearchService elasticsearchService;


    /**
     * 单条插入
     */
    @Test
    public void insertOne() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("1000000");
        userInfo.setUserName("张三");
        userInfo.setUserNameEn("admin");
        userInfo.setPhone("13560786042");
        userInfo.setAge("25");
        userInfo.setSex("男");
        logger.info("输出用户信息数据：" + JSONObject.toJSONString(userInfo));
        Map<String, Object> dataMap = objectMapper.convertValue(userInfo, Map.class);
//         单条数据插入
        IndexRequest indexRequest = new IndexRequest(ElasticConstant.USER_INDEX).id(userInfo.getUserId().toString())
                .source(dataMap);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex) {
            ex.getLocalizedMessage();
        }
        logger.info("测试用户信息数据写入 Elasticsearch 完成......");
        return;

    }

    /**
     * 检索
     */
    @Test
    public void searchUserInfoByEs(){
        // 分页参数
        Integer currentPage = 1;
        Integer rowsPerPage = 10;
        String searchField = "";
        String searchValue = "wangwu";
        List<Map<String, Object>> list = elasticsearchService.selectElasticsearch(ElasticConstant.USER_INDEX, ElasticConstant.COMMON_SEARCH_TYPE, searchField, searchValue, rowsPerPage, currentPage);
        logger.info("ES检索返回数据：{}", JSONObject.toJSONString(list));
    }

    /**
     * 批量插入股票基本信息数据
     */
    @Test
    public void insertBatchUserInfo() {
        List<UserInfo> list = new ArrayList<>();
        assembleUserInfo(list);
        // 先删除原有索引（因为es暂时没有提供清除所有doc的api），再创建索引，并指定分词器，最后倒入数据
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(ElasticConstant.USER_INDEX), RequestOptions.DEFAULT);
            restHighLevelClient.indices().create(new CreateIndexRequest(ElasticConstant.USER_INDEX), RequestOptions.DEFAULT);
            // 指定IK分词器
//            restHighLevelClient.indices().create(new CreateIndexRequest(ElasticConstant.USER_INDEX).settings(Settings.builder().put("analysis.analyzer.default.type", "ik_max_word")), RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.error("删除索引：{}异常，异常信息{}", ElasticConstant.USER_INDEX, e.getMessage());
        }
        BulkRequest request = new BulkRequest();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (UserInfo info : list) {
            String jsonData = JSONObject.toJSONString(info);
            // 批量数据插入,即：将数据批量写入请求，统一执行
//            request.add(new IndexRequest(ElasticConstant.USER_INDEX, null, info.getUserId().toString())
//                    .source(jsonData, XContentType.JSON));
            request.add(new IndexRequest(ElasticConstant.USER_INDEX).id(info.getUserId().toString()).source(jsonData, XContentType.JSON));
        }
        logger.info("所有需要插入Elasticsearch中的数据请求包装成功，开始执行插入动作......");
        try {
            BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex) {
            ex.getLocalizedMessage();
        }
        logger.info("用户基本信息数据批量插入 Elasticsearch 完成......");
        return;
    }

    /**
     * 删除index
     */
    @Test
    public void deleteIndexTest() {
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(ElasticConstant.USER_INDEX), RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.error("删除索引：{}异常，异常信息{}", ElasticConstant.USER_INDEX, e.getMessage());
        }
    }

    /**
     * 组装用户数据
     */
    public static void assembleUserInfo(List<UserInfo> list) {

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("1000000");
        userInfo.setUserName("张三");
        userInfo.setUserNameEn("admin");
        userInfo.setPhone("13560786042");
        userInfo.setAge("25");
        userInfo.setSex("男");
        list.add(userInfo);

        userInfo = new UserInfo();
        userInfo.setUserId("1111111");
        userInfo.setUserName("李如");
        userInfo.setUserNameEn("administrator");
        userInfo.setPhone("13845785412");
        userInfo.setAge("22");
        userInfo.setSex("女");
        list.add(userInfo);

        userInfo = new UserInfo();
        userInfo.setUserId("2222222");
        userInfo.setUserName("王五");
        userInfo.setUserNameEn("wangwu");
        userInfo.setPhone("13569745894");
        userInfo.setAge("21");
        userInfo.setSex("男");
        list.add(userInfo);

        userInfo = new UserInfo();
        userInfo.setUserId("3333333");
        userInfo.setUserName("赵兰");
        userInfo.setUserNameEn("zhaoliu");
        userInfo.setPhone("13296428671");
        userInfo.setAge("30");
        userInfo.setSex("女");
        list.add(userInfo);
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
}
