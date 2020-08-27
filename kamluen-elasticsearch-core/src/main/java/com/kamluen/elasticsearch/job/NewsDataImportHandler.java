package com.kamluen.elasticsearch.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kamluen.api.mktinfo.vo.enums.ENewsEnums;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.entity.ElasticsearchVO.EsNewsInfo;
import com.kamluen.elasticsearch.entity.NewsInfo;
import com.kamluen.elasticsearch.service.ElasticsearchService;
import com.kamluen.elasticsearch.service.mktinfo.NewsInfoService;
import com.kamluen.elasticsearch.utils.PinYinUtils;
import com.kamluen.elasticsearch.utils.StringUtils;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.log.XxlJobLogger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 新闻资讯信息数据导入elasticsearch定时任务
 */
@JobHandler(value = "newsDataImportHandler")
@Component
public class NewsDataImportHandler extends IJobHandler {

    private static Logger logger = LoggerFactory.getLogger(NewsDataImportHandler.class);

    @Resource
    private NewsInfoService newsInfoService;
    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private ElasticsearchService elasticsearchService;

    /**
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("NewsDataImportHandler 新闻资讯信息数据导入elasticsearch任务开始执行......");
        logger.info("NewsDataImportHandler 新闻资讯信息数据导入elasticsearch任务开始执行......");
        /**
         *  查询新闻资讯信息数据并导入elasticsearch库
         */
        selectNewsDataImportElasticsearch();
        XxlJobLogger.log("NewsDataImportHandler 新闻资讯信息数据导入elasticsearch任务执行结束......");
        logger.info("NewsDataImportHandler 新闻资讯信息数据导入elasticsearch任务执行结束......");
        return SUCCESS;
    }

    /**
     * 查询新闻资讯信息数据并导入elasticsearch库
     */
    public void selectNewsDataImportElasticsearch() {
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
                    ens.setTag(info.getTag());
                    if (StringUtils.isNEmpty(info.getGp())) {
                        ens.setGp(info.getGp());
                    } else {
                        ens.setGp("");
                    }
                    if (StringUtils.isNEmpty(info.getImgUrl())) {
                        ens.setInfotreeid(Integer.valueOf(ENewsEnums.IMPORTANT_NEWS.getTypeValue()));
                        ens.setImgUrl(info.getImgUrl());
                    } else {
                        ens.setInfotreeid(Integer.valueOf(ENewsEnums.STK_NEWS.getTypeValue()));
                        ens.setImgUrl("");
                    }
                    if (StringUtils.isNEmpty(info.getStkName())) {
                        ens.setStkName(info.getStkName());
                    } else {
                        ens.setStkName(""); // 等待爬虫填充时写入es,用于资讯检索匹配（股票名称）
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

}
