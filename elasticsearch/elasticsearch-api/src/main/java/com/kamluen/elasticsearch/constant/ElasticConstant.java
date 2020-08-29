package com.kamluen.elasticsearch.constant;

/**
 * @author zhanglei
 * @date 2018-11-14
 * Elasticsearch 常量类
 */
public class ElasticConstant {

    /**
     * ES公共搜索类型
     */
    public static final String COMMON_SEARCH_TYPE = "_doc";

    /**
     * 用户信息数据存在elasticSearch中索引以及类型命名
     */
    public static final String USER_INDEX = "user_index";

    public static final String USER_INFO_USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String USER_PHONE = "phone";
    public static final String USER_AGE = "age";
    public static final String USER_SEX = "sex";


    /**
     * 通配符 * 匹配 ， 用于模糊查询
     */
    public static final String WILDCARD = "*";

}
