package com.kamluen.elasticsearch.constant;

/**
 * @author zhanglei
 * @date 2018-11-14
 * Elasticsearch 常量类
 */
public class ElasticConstant {

    /**
     * 动态信息数据存在elasticsearch中索引以及类型命名
     */
    public static final String DYNAMIC_INDEX = "dynamic_index";
    public static final String DYNAMIC_TYPE = "dynamic_type";

    public static final String DYNAMIC_WRONG_DATA = "https://39.104.9.213/";
    public static final String DYNAMIC_WRONG_SUFFIX = "/APP/UPLOAD/USER_DEPOSIT";

    /**
     * 动态信息在ElasticSearch中用于检索的字段名
     */
    public static final String PTF_NOTE_ID = "ptfNoteId";
    public static final String USER_ID = "userId";
    public static final String NOTE_TYPE = "noteType";
    public static final String ART_ID = "artId";
    public static final String CONTENT = "content";
    public static final String NOTE_TITLE = "noteTitle";
    public static final String DYNAMIC_TITLE = "title";
    public static final String NEWS_IMG = "newsImg";
    public static final String CATEGORY_NAME = "categoryName";
    public static final String URLS = "urls";
    public static final String TYPE = "type";
    public static final String Dynamic_UPDATE_TIME = "updateTime";

    /**
     * 用户信息数据存在elasticSearch中索引以及类型命名
     */
    public static final String USER_INDEX = "user_index";
    public static final String USER_TYPE = "user_type";

    public static final String USER_INFO_USER_ID = "userId";
    public static final String USER_PHONE = "cellPhone";
    public static final String NICK_NAME = "nickName";

    /**
     * 策略信息数据存在elasticSearch中索引以及类型命名
     */
    public static final String STRATEGY_INDEX = "strategy_index";
    public static final String STRATEGY_TYPE = "strategy_type";

    public static final String STRATEGY_NAME = "strategyName";
    public static final String STRATEGY_DESCRIPTION = "strategyExplain";
    public static final String STRATEGY_STOCK_NAME = "stockName";
    public static final String STRATEGY_STOCK_CODE = "stockCode";
    public static final String STRATEGY_PUBLISH_TIME = "publishTime";

    /**
     * 股票基本信息数据存在elasticsearch中索引以及类型命名
     */
    public static final String STOCK_INDEX = "stock_index";
    public static final String STOCK_TYPE = "stock_type";

    /**
     * 新闻资讯信息数据存在elasticsearch中索引以及类型命名
     */
    public static final String NEWS_INDEX = "news_index";
    public static final String NEWS_TYPE = "news_type";

    /**
     * 新闻资讯推荐信息数据存在elasticsearch中索引以及类型命名
     */
    public static final String NEWS_RECOMMEND_INDEX = "news_recommend_index";
    public static final String NEWS_RECOMMEND_TYPE = "news_recommend_type";

    /**
     * 关键词热搜榜数据存在elasticsearch中索引以及类型命名
     */
    public static final String SEARCH_INDEX = "searching_index";
    public static final String SEARCH_TYPE = "searching_type";

    /**
     * 股票标签信息数据存在elasticsearch中索引以及类型命名
     */
    public static final String STOCK_TAG_INDEX = "stock_tag_index";
    public static final String STOCK_TAG_TYPE = "stock_tag_type";

    /**
     * 股票标签信息数据存在elasticsearch中的名称
     */
    public static final String STOCK_TAGS = "stock_tags";

    /**
     * 通配符 * 匹配 ， 用于模糊查询
     */
    public static final String WILDCARD = "*";

    /**
     * 新闻资讯信息用于检索的字段名称
     */
    public static final String GP = "gp";
    public static final String TITLE = "title";
    public static final String ISSUE_TIME = "issueTime";
    public static final String INFO_TREE_ID = "infotreeid";
    public static final String SPELL = "spell";
    public static final String LABEL_NEWS_ID = "labelNewsId";
    public static final String TAG = "tag";
    public static final String NEWS_SOURCE = "newsSource";
    /**
     * 股票基本信息用于检索的字段名称
     */
    public static final String STK_CODE = "stkCode";
    public static final String STK_NAME = "stkName";
    public static final String SPELLING = "spelling";
    public static final String STK_TYPE = "stkType";
    public static final String MKT_TYPE = "mktType";
    public static final String SEC_TYPE = "secType";
    public static final String ASSET_ID = "assetId";


    public static final String HTTP = "http";
    public static final String IMG_URL = "imgUrl";

    /**
     * 热搜榜信息用于检索的字段名称
     */
    public static final String COUNT = "count";
    public static final String UPDATE_TIME = "update_time";
    public static final String TEXT = "text";


    /**
     * 股票类别-市场分类
     */
    public static final String HK = "HK";
    public static final String SZ = "SZ";
    public static final String SH = "SH";
    public static final String US = "US";
    /**
     * 限定limit后的数量
     */
    public static final Integer LIMITNUMBER = 90;
    /**
     * 动态中 - 策略类别
     */
    public static final String SY = "SY";
}
