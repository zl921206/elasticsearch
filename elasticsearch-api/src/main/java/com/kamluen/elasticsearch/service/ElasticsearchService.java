package com.kamluen.elasticsearch.service;

import com.kamluen.elasticsearch.enums.SearchTypeEnums;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch公共服务接口
 */
public interface ElasticsearchService<T> {

    /**
     * 从Elasticsearch中获取数据
     * @Param indexName 当前索引名
     * @Param indexType 当前索引下的类型
     * @Param fieldName 要检索的字段名
     * @Param value 字段对应的值（该值为部分数据，支持模糊查询）
     * @Param rowsPerPage 每页行数
     * @Param currentPage 当前页数
     */
    List<Map<String, Object>> selectElasticsearch(String indexName, String indexType, String fieldName, String value, Integer rowsPerPage, Integer currentPage);

    /**
     * 根据ID获取ES数据
     * @param index
     * @param type
     * @param id
     * @return
     */
    List<Map<String, Object>> selectEsDataById(String index, String type, String id);

    /**
     * 向Elasticsearch中插入数据
     * @param index
     * @param type
     * @param id
     * @param obj
     */
    void insertElasticsearch(String index, String type, String id, Object obj);

    /**
     * 向Elasticsearch查询是否存在该主键的数据
     * @param index
     * @param type
     * @param id
     * @return
     */
    Boolean isExistEsDataById(String index, String type, String id);

    /**
     * 向Elasticsearch中批量插入数据
     */
    void insertBatchElasticsearch();

    /**
     * 根据ID更新Elasticsearch中的数据
     * @param index
     * @param type
     * @param id
     * @param obj
     */
    void updateElasticsearchById(String index, String type, String id, Object obj);

    /**
     * 根据ID删除Elasticsearch中的数据
     * @param index
     * @param type
     * @param id
     */
    void deleteElasticsearchById(String index, String type, String id);

    /**
     * 通过id查询es信息
     * @param index
     * @param type
     * @param id
     * @return
     */
    String selectESInfoById(String index, String type, String id);
}
