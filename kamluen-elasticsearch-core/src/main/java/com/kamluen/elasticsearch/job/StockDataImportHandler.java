package com.kamluen.elasticsearch.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kamluen.api.grm.utils.EStkType;
import com.kamluen.api.grm.utils.ESubStkType;
import com.kamluen.elasticsearch.enums.MktTypeEnums;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.entity.ElasticsearchVO.EsStockInfo;
import com.kamluen.elasticsearch.entity.StockInfo;
import com.kamluen.elasticsearch.service.mktinfo.StockInfoService;
import com.kamluen.elasticsearch.utils.DateUtils;
import com.kamluen.elasticsearch.utils.PinYinUtils;
import com.kamluen.elasticsearch.utils.StkUtils;
import com.kamluen.elasticsearch.utils.StringUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 股票基本信息数据导入elasticsearch定时任务
 */
@JobHandler(value = "stockDataImportHandler")
@Component
public class StockDataImportHandler extends IJobHandler {

    private static Logger logger = LoggerFactory.getLogger(StockDataImportHandler.class);

    @Resource
    private StockInfoService stockInfoService;
    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("StockDataImportHandler 股票基本信息数据导入elasticsearch任务开始执行......");
        logger.info("StockDataImportHandler 股票基本信息数据导入elasticsearch任务开始执行......");
        /**
         *  查询新闻资讯信息数据并导入elasticsearch库
         */
        selectStockDataImportElasticsearch();
        XxlJobLogger.log("StockDataImportHandler 股票基本信息数据导入elasticsearch任务执行结束......");
        logger.info("StockDataImportHandler 股票基本信息数据导入elasticsearch任务执行结束......");
        return SUCCESS;
    }

    /**
     * 查询股票基本信息数据并导入elasticsearch库
     */
    public void selectStockDataImportElasticsearch() {
        logger.info("进入方法：selectStockDataImportElasticsearch...... start... ");
        // 先删除原有索引（因为es暂时没有提供清除所有doc的api），再创建索引并指定分词器，最后倒入数据
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(ElasticConstant.STOCK_INDEX));
            restHighLevelClient.indices().create(new CreateIndexRequest(ElasticConstant.STOCK_INDEX).settings(Settings.builder().put("analysis.analyzer.default.type", "ik_max_word")));
        } catch (Exception e) {
            logger.error("删除索引：{}异常，异常信息{}", ElasticConstant.STOCK_INDEX, e.getMessage());
        }
        QueryWrapper<StockInfo> ew = new QueryWrapper<StockInfo>();
        // 获取股票信息数据导入ES，过滤已经退市的股票
        ew.eq("is_status", 1);
        ew.and(wrapper -> wrapper.isNull("delist_date").or().gt("delist_date", DateUtils.getCurrentDate()));
//        ew.andNew();
//        ew.isNull("delist_date").or().gt("delist_date", DateUtils.getCurrentDate());
        // 1：股票(正股)，2：债券，3：基金，4：权证(涡轮·牛熊)，5：指数
        ew.and(wrapper -> wrapper.notIn("sec_type", 2));
//        ew.andNew();
//        ew.notIn("sec_type", 2);
        List<StockInfo> stockInfos = stockInfoService.list(ew);
        logger.info("输出需要导入ES未退市的股票信息集合size：" + stockInfos.size());
        BulkRequest request = new BulkRequest();
        request.timeout("10m"); // 设置超时时间为10分钟
        EsStockInfo ens = new EsStockInfo();
        try {
            if (stockInfos != null && stockInfos.size() > 0) {
                for (StockInfo info : stockInfos) {
                    // 子类型过滤
                    if(info.getSecType() == EStkType.FUND.getNo() && info.getSecStype() != ESubStkType.HK_FUND_ETF.getSubType()){
                        continue;
                    }
                    ens.setAssetId(info.getAssetId());
                    ens.setStkCode(info.getStkCode());
                    if (StringUtils.isNotNull(info.getStkName())){
                        ens.setStkName(info.getStkName());
                        ens.setSpelling(PinYinUtils.getPingYin(info.getStkName()));
                        ens.setSpellingAbbr(PinYinUtils.getFirstSpell(info.getStkName()));
                    } else if (StringUtils.isNotNull(info.getEngName())){
                        ens.setStkName(info.getEngName());
                        ens.setSpelling(PinYinUtils.getPingYin(info.getEngName()));
                        ens.setSpellingAbbr(PinYinUtils.getFirstSpell(info.getEngName()));
                    } else {
                        ens.setStkName("");
                        ens.setSpelling("");
                        ens.setSpellingAbbr("");
                    }
                    ens.setStkType(info.getSecStype()); // 细分类别
                    ens.setSecType(info.getSecType());  // 证券类别
                    ens.setMkt(StkUtils.determineMarketCode(info.getAssetId()));
                    if (info.getAssetId().endsWith(MktTypeEnums.HK.getTypeName())) {
                        ens.setMktType(MktTypeEnums.HK.getTypeValue());
                    } else if (info.getAssetId().endsWith(MktTypeEnums.SZ.getTypeName())) {
                        ens.setMktType(MktTypeEnums.SZ.getTypeValue());
                    } else if (info.getAssetId().endsWith(MktTypeEnums.SH.getTypeName())) {
                        ens.setMktType(MktTypeEnums.SH.getTypeValue());
                    } else if (info.getAssetId().endsWith(MktTypeEnums.US.getTypeName())) {
                        ens.setMktType(MktTypeEnums.US.getTypeValue());
                    }
                    logger.debug("输出股票基本信息数据：" + JSONObject.toJSONString(ens));
                    // 将数据转换为json格式写入
                    String jsonData = JSONObject.toJSONString(ens);
                    // 批量数据插入,即：将数据批量写入请求，统一执行
                    request.add(new IndexRequest(ElasticConstant.STOCK_INDEX, ElasticConstant.STOCK_TYPE, info.getAssetId())
                            .source(jsonData, XContentType.JSON));
                }
            } else {
                logger.info("查询数据库股票信息数据为空......");
                return;
            }
            logger.info("所有股票基本信息需要插入Elasticsearch中的数据请求包装成功，开始执行插入动作......");

            BulkResponse response = restHighLevelClient.bulk(request);
        } catch (ElasticsearchException e) {
            e.printStackTrace();
            e.getDetailedMessage();
            logger.error("股票基本信息数据批量插入 Elasticsearch 异常，异常信息：{}", e.getMessage());
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            ex.getLocalizedMessage();
            logger.error("股票基本信息数据批量插入 Elasticsearch 发生IO异常，异常信息：{}", ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("批量导入股票信息集合数据异常：{}", e.getMessage());
        }
        logger.info("股票基本信息数据批量插入 Elasticsearch 完成......");
        logger.info("结束方法：selectStockDataImportElasticsearch...... end... ");
        return;
    }
}
