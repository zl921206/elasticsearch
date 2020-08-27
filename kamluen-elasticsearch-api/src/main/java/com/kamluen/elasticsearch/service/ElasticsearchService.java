package com.kamluen.elasticsearch.service;

import com.kamluen.common.bean.ResultMessage;
import com.kamluen.elasticsearch.bean.req.EsSearchInfoReqVo;
import com.kamluen.elasticsearch.bean.resp.HotSearchVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch公共服务接口
 */
public interface ElasticsearchService {

    public static final Logger logger = LoggerFactory.getLogger(ElasticsearchService.class);

    /**
     * 从Elasticsearch中获取数据
     * @Param indexName 当前索引名
     * @Param indexType 当前索引下的类型
     * @Param fieldName 要检索的字段名
     * @Param 字段对应的值（该值为部分数据，支持模糊查询）
     * @Param rowsPerPage 每页行数
     * @Param currentPage 当前页数
     */
    List<Map<String, Object>> selectElasticsearch(String indexName, String indexType, String fieldName, String value, Integer rowsPerPage, Integer currentPage, boolean isExactMatch);

    /**
     * 获取热搜vo对象
     * @Param indexName 当前索引名
     * @Param indexType 当前索引下的类型
     * @Param fieldName 要检索的字段名
     * @Param 字段对应的值（该值为部分数据，支持模糊查询）
     * @Param rowsPerPage 每页行数
     * @Param currentPage 当前页数
     */
    List<HotSearchVO> convertElasticsearchToHotSearchVO(String indexName, String indexType, String fieldName, String value, Integer rowsPerPage, Integer currentPage, boolean isExactMatch);

    /**
     * 对应selectElasticsearch方法的count
     * @param indexName
     * @param indexType
     * @return
     */
    Integer getCount(String indexName, String indexType, String fieldName, String value, boolean isExactMatch);

    /**
     * 从Elasticsearch中获取数据。 多对一：即多个值对应一个字段进行匹配
     * @Param indexName 当前索引名
     * @Param indexType 当前索引下的类型
     * @Param fieldName 要检索的字段名
     * @Param list 字段对应的数值集合
     * @Param rowsPerPage 每页行数
     * @Param currentPage 当前页数
     * @Param labelNewsId 新闻资讯的ID值
     */
    List<Map<String, Object>> multiToOneSelectByEs(String indexName, String indexType, String fieldName, List<String> list, Integer rowsPerPage, Integer currentPage, Integer labelNewsId);
    /**
     * 根据ID获取ES数据
     */
    List<Map<String, String>> selectEsDataById(String index, String type, String id);
    /**
     * 向Elasticsearch中插入数据
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
     */
    void updateElasticsearchById(String index, String type, String id, Object obj);
    /**
     * 根据ID删除Elasticsearch中的数据
     */
    void deleteElasticsearchById(String index, String type, String id);

    /**
     * oms中台修改elasticsearch热搜榜count
     * @param vo
     * @param count
     * @return
     */
    ResultMessage<String> updateHotSearchCount(EsSearchInfoReqVo vo, BigInteger count);



    /**
     * oms新增elasticsearch热搜榜股票名
     * @param vo
     * @return
     */
    ResultMessage<String> addHotSearchText(EsSearchInfoReqVo vo);


    /**
     * 通过id查询es信息
     * @param id
     * @return
     */
    String selectESInfoById(String index, String type, String id);
}
