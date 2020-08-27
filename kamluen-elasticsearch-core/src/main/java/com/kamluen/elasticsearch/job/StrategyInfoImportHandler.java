package com.kamluen.elasticsearch.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kamluen.api.mktinfo.vo.QuotationVO;
import com.kamluen.common.utils.JSONUtil;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.entity.ElasticsearchVO.EsStrategyInfo;
import com.kamluen.elasticsearch.entity.StrategyInfo;
import com.kamluen.elasticsearch.entity.UserInfo;
import com.kamluen.elasticsearch.enums.StrategyEnum;
import com.kamluen.elasticsearch.service.kamluen.UserInfoService;
import com.kamluen.elasticsearch.service.strategy.StrategyInfoService;
import com.kamluen.elasticsearch.utils.StringUtils;
import com.kamluen.odps.service.RedisMapService;
import com.kamluen.odps.service.RedisSetService;
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
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

import static com.kamluen.elasticsearch.constant.ElasticConstant.STRATEGY_INDEX;

/**
 * @Package: com.kamluen.elasticsearch.job
 * @Author: LQW
 * @Date: 2019/10/16
 * @Description:将策略所需要的相关信息导入ES
 */
@JobHandler("strategyInfoImportHandler")
@Component
public class StrategyInfoImportHandler extends IJobHandler {

    private static final String HTTPS = "https";
    private static final String SUFFIX = "strategy:";
    private static final String PREFIX = "_follow";
    private static Logger log = LoggerFactory.getLogger(StrategyInfoImportHandler.class);

    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private StrategyInfoService strategyInfoService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private RedisSetService redisSetService;
    @Resource
    private RedisMapService redisMapService;
    @Value("${oss.prefix}")
    private String prefix;
    @Value("${oss.user.suffix}")
    private String suffix;
    @Value("${oss.wrong.data}")
    private String writeKey;
    @Value("${oss.default.image}")
    private String defaultImage;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("StrategyInfoImportHandler 策略数据导入elasticsearch任务开始执行......");
        log.info("StrategyInfoImportHandler 策略数据导入elasticsearch任务开始执行......");

        selectStrategyInformationToImportIntoES();

        XxlJobLogger.log("StrategyInfoImportHandler 策略数据导入elasticsearch任务执行结束......");
        log.info("StrategyInfoImportHandler 策略数据导入elasticsearch任务执行结束......");
        return SUCCESS;
    }

    /**
     * 选择要导入ES的策略信息
     */
    private void selectStrategyInformationToImportIntoES() {
        log.info("进入方法 selectStrategyInformationToImportIntoES.... start...");
        //region -step1 先删除原有索引（因为es暂时没有提供清除所有doc的api），再创建索引并指定分词器，最后倒入数据
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(STRATEGY_INDEX));
            restHighLevelClient.indices().create(new CreateIndexRequest(STRATEGY_INDEX));
        } catch (IOException e) {
            e.printStackTrace();
            log.info("删除策略数据:{}索引时出现异常,异常信息为:{}", "", e.getMessage());
        }
        //endregion
        //region -step2 组装策略数据
        EsStrategyInfo esInfo = new EsStrategyInfo();
        // 每次查询导入20000条
        Page<StrategyInfo> page = new Page<StrategyInfo>(1, 20000);
        QueryWrapper<StrategyInfo> wrapper = new QueryWrapper<StrategyInfo>();
        wrapper.select("strategy_id", "strategy_type", "stock_code", "user_id", "strategy_name", "strategy_explain", "publish_time", "test_status", "status", "from_strategy_id", "strategy_cycle", "strategy_role", "publish_status", "create_time");
        wrapper.eq("publish_status", StrategyEnum.published.getCode());
        wrapper.eq("strategy_role", StrategyEnum.published.getCode());
        wrapper.ne("status", StrategyEnum.delete.getCode());
        IPage<StrategyInfo> pageInfo = strategyInfoService.page(page, wrapper);
        BulkRequest request = new BulkRequest();
        request.timeout(TimeValue.timeValueMillis(5));
        if (null != pageInfo && 0 != pageInfo.getRecords().size()) {
            for (StrategyInfo info : pageInfo.getRecords()) {
                BeanUtils.copyProperties(info, esInfo);
                splicingUserInformationBasedOnUserId(info, esInfo);
                Integer followed = strategyInfoService.whetherTrackOrder(esInfo.getUserId(), esInfo.getStrategyId());
                esInfo.setFollowed(followed);
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
            log.info("查询数据库未获取到策略相关数据......");
            return;
        }
        //endregion
        //region -step3 策略数据插入elasticSearch
        log.info("策略数据封装完毕,准备执行插入请求,开始执行插入......");
        log.info("策略数据封装完毕,封装结果:{}", request);
        try {
            BulkResponse response = restHighLevelClient.bulk(request);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("策略数据插入elasticSearch产生IO异常,异常信息为:{}", e);
        } catch (ElasticsearchException es) {
            es.getDetailedMessage();
            log.error("策略数据插入elasticSearch失败,原因:{}", es);
        }
        log.info("策略数据插入ElasticSearch完成......");
        //endregion
        log.info("方法执行完成 selectStrategyInformationToImportIntoES.... end...");
    }

    /**
     * 策略中包含的股票信息
     *
     * @param info   策略信息
     * @param esInfo 策略信息ES检索类
     */
    private void stockInformationIncludedInTheStrategy(StrategyInfo info, EsStrategyInfo esInfo) {
        String stockCode = info.getStockCode() != null ? info.getStockCode() : "";
        log.info("策略中包含股票信息,Stock Code In The Strategy:{}.....start....", stockCode);
        QuotationVO quotationVO = strategyInfoService.queryStockDataFromRedis(stockCode);
        String stockName = quotationVO.getStkName() != null ? quotationVO.getStkName() : "";
        esInfo.setStockName(stockName);
        log.info("策略中包含股票信息,Stock Code In The Strategy:{} The Stock Name Obtained : {},.....end....", stockCode, stockName);
    }


    /**
     * 根据用户id拼接用户信息
     *
     * @param info   策略信息
     * @param esInfo 策略信息ES检索类
     */
    private void splicingUserInformationBasedOnUserId(StrategyInfo info, EsStrategyInfo esInfo) {
        log.info("根据用户id拼接用户信息,获取到的用户id为:{},准备存入es的实体信息为:{}, start....", info.getUserId() == null ? 0 : info.getUserId(), JSONUtil.toCompatibleJson(esInfo));
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
        log.info("根据用户id拼接用户信息,获取到的用户id为:{},准备存入es的实体信息为:{}, end....", info.getUserId() == null ? 0 : info.getUserId(), JSONUtil.toCompatibleJson(esInfo));
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
        if (userIcon.contains(HTTPS)) {
            return userIcon;
        } else if (userIcon.contains(writeKey)) {
            return prefix + suffix + userIcon.substring(writeKey.length());
        } else if (userIcon.contains(suffix)) {
            return prefix + userIcon;
        } else {
            return prefix + suffix + userIcon;
        }
    }
}
