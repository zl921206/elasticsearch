package com.kamluen.elasticsearch.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.enums.SearchTypeEnums;
import com.kamluen.elasticsearch.service.*;
import com.kamluen.elasticsearch.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;


/**
 * Elasticsearch 相关操作，公共服务实现类
 */
//@Service(application = "${dubbo.application.id}",
//        protocol = "${dubbo.protocol.id}",
//        registry = "${dubbo.registry.id}",
//        version = "${dubbo.version}")
@Component
@Slf4j
public class ElasticsearchServiceImpl<T> implements ElasticsearchService<T> {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public List<Map<String, Object>> selectElasticsearch(String indexName, String indexType, String fieldName, String value, Integer rowsPerPage, Integer currentPage) {
        log.info("开始执行selectElasticsearch方法...start...");
        List<Map<String, Object>> list = new ArrayList<>();
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (currentPage == 0) {
            currentPage = 1;
        }
        searchSourceBuilder.from((currentPage - 1) * rowsPerPage);
        searchSourceBuilder.size(rowsPerPage);

        // 根据传入值判断搜索类型
        SearchTypeEnums searchType = StringUtils.getSearchType(value);

        // 给传入值匹配通配符
        value = ElasticConstant.WILDCARD + value + ElasticConstant.WILDCARD;

        if (StringUtils.isEmpty(fieldName)) searchType = SearchTypeEnums.NONE;

        switch (searchType) {
            // 是数字
            case NUMBER:
                searchSourceBuilder.query(QueryBuilders.wildcardQuery(fieldName, value));
                break;
            // 是中文
            case CHINESE:
                searchSourceBuilder.query(QueryBuilders.matchQuery(fieldName, value));
                break;
            // 是字母
            case ENGLISH:
                searchSourceBuilder.query(QueryBuilders.matchQuery(fieldName, value));
                // 全字段匹配
            case NONE:
            default:
                searchSourceBuilder.query(QueryBuilders.queryStringQuery(value));
        }
        request.indices(indexName);
        request.types(indexType);
        request.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询Elasticsearch，执行selectElasticsearch方法，获取数据信息异常：{}", e.getMessage());
        }
        SearchHit[] hits = {};
        if (response != null && response.getHits() != null) {
            hits = response.getHits().getHits();
            log.info("当前匹配到数据size：" + response.getHits().getTotalHits());
        }
        for (SearchHit hit : hits) {
            list.add(hit.getSourceAsMap());
        }
        log.info("输出查询结果：" + JSONObject.toJSONString(list));
        log.info("结束执行selectElasticsearch方法...end...");
        return list;
    }

    @Override
    public List<Map<String, Object>> selectEsDataById(String index, String type, String id) {
        GetRequest req = new GetRequest(index, type, id);
        GetResponse resp = null;
        try {
            resp = restHighLevelClient.get(req, RequestOptions.DEFAULT);
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
            log.error("根据ID获取ES数据异常，异常信息：{}", e.getMessage());
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultList.add(resp.getSourceAsMap());
        return resultList;
    }

    @Override
    public Boolean isExistEsDataById(String index, String type, String id) {
        GetRequest req = new GetRequest(index, type, id);
        GetResponse resp = null;
        boolean isExist = false;
        try {
            resp = restHighLevelClient.get(req, RequestOptions.DEFAULT);
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
            log.error("根据ID获取ES数据异常，异常信息：{}", e.getMessage());
        }
        // 判断返回结果是否为空
        if (null != resp.getSourceAsMap()) {
            isExist = true;
        }
        return isExist;
    }

    @Override
    public void insertElasticsearch(String index, String type, String id, Object obj) {
        log.info("插入ID：" + id + "，数据到ES索引：" + index + "中开始...");
        IndexRequest indexRequest = new IndexRequest(index, type, id)
                .source(JSONObject.toJSONString(obj), XContentType.JSON);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info("插入ID：" + id + "，数据到ES索引：" + index + "中成功...");
        } catch (ElasticsearchException e) {
            e.getDetailedMessage();
            log.error("ID：" + id + "，数据插入ES异常，异常信息：{}", e);
        } catch (java.io.IOException ex) {
            ex.getLocalizedMessage();
            log.error("ID：" + id + "，数据插入ES出现IO异常，异常信息：{}", ex.getMessage());
        }
        log.info("插入ID：" + id + "，数据到ES索引：" + index + "中结束...");
    }

    @Override
    public void insertBatchElasticsearch() {

    }

    @Override
    public void updateElasticsearchById(String index, String type, String id, Object obj) {
        log.info("根据ID：" + id + "，更新ES索引：" + index + "中数据开始...");
        UpdateRequest updateRequest = new UpdateRequest(index, type, id)
                .fetchSource(true);
        try {
            String json = objectMapper.writeValueAsString(obj);
            updateRequest.doc(json, XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            updateResponse.getGetResult().sourceAsMap();
            log.info("根据ID：" + id + "，更新ES索引：" + index + "中数据成功...");
        } catch (Exception e) {
            e.getLocalizedMessage();
            log.error("根据ID：" + id + "，更新ES索引：" + index + "中数据异常，异常信息{}", e.getMessage());
        }
        log.info("根据ID：" + id + "，更新ES索引：" + index + "中数据结束...");
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
        log.info("根据ID：" + id + "，删除ES索引" + index + "中数据开始...");
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("根据ID：" + id + "，删除ES索引" + index + "中数据成功");
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
            log.error("根据ID：" + id + "，删除ES索引" + index + "中数据异常，异常信息{}", e.getMessage());
        }
        log.info("根据ID：" + id + "，删除ES索引" + index + "中数据结束...");
    }

    @Override
    public String selectESInfoById(String index, String type, String id) {
        log.info("****newsid============" + id);
        GetRequest req = new GetRequest(index, type, id);
        GetResponse resp = null;
        try {
            resp = restHighLevelClient.get(req, RequestOptions.DEFAULT);
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
            log.error("根据ID获取ES数据异常，异常信息：{}", e.getMessage());
        }
        if (resp != null) {
            log.info("selectESInfoById()：{}", resp.getSourceAsString());
            return resp.getSourceAsString();
        }
        return null;
    }

}
