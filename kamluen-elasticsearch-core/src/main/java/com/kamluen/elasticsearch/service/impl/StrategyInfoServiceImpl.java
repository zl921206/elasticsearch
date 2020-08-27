package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.api.mktinfo.vo.QuotationVO;
import com.kamluen.api.mktserver.HkexTsData;
import com.kamluen.api.mktserver.ShTsData;
import com.kamluen.api.mktserver.SzTsData;
import com.kamluen.api.mktserver.UsTsData;
import com.kamluen.elasticsearch.dao.strategy.StrategyInfoDao;
import com.kamluen.elasticsearch.entity.CommonCommentInteraction;
import com.kamluen.elasticsearch.entity.CommonInteraction;
import com.kamluen.elasticsearch.entity.CommonUserInteraction;
import com.kamluen.elasticsearch.entity.StrategyInfo;
import com.kamluen.elasticsearch.enums.MktTypeEnums;
import com.kamluen.elasticsearch.enums.StrategyInteractiveEnum;
import com.kamluen.elasticsearch.service.kamluen.CommonCommentInteractionService;
import com.kamluen.elasticsearch.service.kamluen.CommonCommentService;
import com.kamluen.elasticsearch.service.kamluen.CommonInteractionService;
import com.kamluen.elasticsearch.service.kamluen.CommonUserInteractionService;
import com.kamluen.elasticsearch.service.strategy.StrategyInfoService;
import com.kamluen.elasticsearch.utils.StringUtils;
import com.kamluen.odps.service.RedisMapService;
import com.kamluen.odps.service.RedisSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Package: com.kamluen.elasticsearch.service.impl
 * @Author: LQW
 * @Date: 2019/10/16
 * @Description:策略信息服务接口实现类
 */
@Service
public class StrategyInfoServiceImpl extends ServiceImpl<StrategyInfoDao, StrategyInfo> implements StrategyInfoService {

    private static final Logger log = LoggerFactory.getLogger(NewsInfoServiceImpl.class);
    @Resource
    private CommonUserInteractionService commonUserInteractionService;
    @Resource
    private CommonInteractionService commonInteractionService;
    @Resource
    private CommonCommentInteractionService commentInteractionService;
    @Resource
    private CommonCommentService commonCommentService;
    @Resource
    private RedisSetService redisSetService;
    @Resource
    private RedisMapService redisMapService;
    @Resource
    private StrategyInfoDao strategyInfoDao;

    private static final String SUFFIX = "strategy:";
    private static final String PREFIX = "_follow";

    @Override
    public Map<String, Object> queryInteractiveInfoAboutTheCurrentStrategy(Integer strategyId, Integer userId) {
        //region step1 策略互动所需变量
        // 互动id
        Long interactionId = 0L;
        //该用户对当前策略的互动状态
        String interactiveState = "";
        //点赞数量
        int thumbUpNumber = 0;
        //评论数量
        int commentNumber = 0;
        //点踩数量
        int numberOfBadReview = 0;
        //阅读数量
        int readNumber = 0;
        //分享数量
        int numberOfShares = 0;
        //endregion
        //region step2 查询并组装策略互动所需数据
        //为HashMap使用默认容量大小
        Map<String, Object> map = new HashMap<>(16);
        //region 该用户对当前策略的点赞或点踩情况
        CommonUserInteraction commonUserInteraction = commonUserInteractionService.getOne(
                new QueryWrapper<CommonUserInteraction>()
                        .eq("user_id", userId)
                        .eq("module_id", strategyId)
                        .eq("module_type", 1)
        );
        //点赞点踩的状态
        if (StringUtils.isNotNull(commonUserInteraction)) {
            if (StrategyInteractiveEnum.Liked.getCode().equals(commonUserInteraction.getThumbsUp())) {
                interactiveState = StrategyInteractiveEnum.Liked.getInteractiveState();
            }
            if (StrategyInteractiveEnum.SteppedOn.getCode().equals(commonUserInteraction.getThumbsDown())) {
                interactiveState = StrategyInteractiveEnum.SteppedOn.getInteractiveState();
            }
        }
        //endregion
        //region 阅读数量、分享数量、点赞数量、点踩数量
        CommonInteraction commonInteraction = commonInteractionService.getOne(
                new QueryWrapper<CommonInteraction>()
                        .select("id", "module_id", "read_num", "forward_num", "thumbs_up_count", "thumbs_down_count")
                        .eq("module_id", strategyId)
                        .eq("module_type", 1)
                        .eq("status", 1)
        );
        if (StringUtils.isNotNull(commonInteraction)) {
            //记录下公共互动实体id
            interactionId = commonInteraction.getId();
            readNumber = commonInteraction.getReadNum();
            numberOfShares = commonInteraction.getForwardNum();
            thumbUpNumber = commonInteraction.getThumbsUpCount();
            numberOfBadReview = commonInteraction.getThumbsDownCount();
        }
        //endregion
        //region 评论数量
        commentNumber = commentInteractionService.count(
                new QueryWrapper<CommonCommentInteraction>()
                        .eq("interaction_id", interactionId)
                        .eq("from_user", userId)
        );
        //endregion
        //endregion
        // region step3  分装当前策略的互动信息
        //互动状态
        map.put("interactiveState", interactiveState);
        //点赞数量
        map.put("thumbUpNumber", thumbUpNumber);
        //评论数量
        map.put("commentNumber", commentNumber);
        //点踩数量
        map.put("numberOfBadReview", numberOfBadReview);
        //阅读数量
        map.put("noteReadNum", readNumber);
        //分享数量
        map.put("noteShareNum", numberOfShares);
        //endregion
        return map;
    }

    @Override
    public StrategyInfo getOne(Integer strategyId) {
        QueryWrapper<StrategyInfo> wrapper = new QueryWrapper<>();
        wrapper.select(
                "strategy_id",
                "strategy_type",
                "from_strategy_id",
                "strategy_cycle",
                "strategy_name",
                "strategy_explain",
                "strategy_role",
                "user_id",
                "stock_code",
                "publish_status",
                "status",
                "test_status",
                "publish_time",
                "create_time"
        );
        wrapper.eq("strategy_id", strategyId);
        return this.getOne(wrapper);
    }

    @Override
    public Integer whetherTrackOrder(Integer userId, Integer strategyId) {
        log.info("whether Track Order is userId:{}....start...", userId);
        Set<String> followSet = redisSetService.findALL(SUFFIX + userId + PREFIX);
        followSet.forEach(s -> {
            log.info("获取到的跟单策略id为:{}", s);
        });
        return followSet.contains(strategyId.toString()) ? 1 : 0;
    }

    @Override
    public QuotationVO queryStockDataFromRedis(String stockCode) {
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
}
