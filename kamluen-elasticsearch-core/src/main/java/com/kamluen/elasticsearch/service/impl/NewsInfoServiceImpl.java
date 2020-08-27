package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.api.vo.ResponseVO;
import com.kamluen.elasticsearch.bean.resp.EsNewsInfoRespVo;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.dao.mktinfo.NewsInfoDao;
import com.kamluen.elasticsearch.entity.NewsInfo;
import com.kamluen.elasticsearch.service.ElasticsearchService;
import com.kamluen.elasticsearch.service.mktinfo.NewsInfoService;
import com.kamluen.elasticsearch.utils.StkUtils;
import com.kamluen.protocol.StaticType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglei
 * 新闻资讯信息服务接口实现类
 * @date 2018-11-01
 */
@Service
public class NewsInfoServiceImpl extends ServiceImpl<NewsInfoDao, NewsInfo> implements NewsInfoService {

    private static final Logger logger = LoggerFactory.getLogger(NewsInfoServiceImpl.class);

    @Resource
    private ElasticsearchService elasticsearchService;

    @Override
    public ResponseVO selectNewsInfoByEs(String value, Integer rowsPerPage, Integer currentPage) {
        ResponseVO responseVO = new ResponseVO();
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String fieldName = "";
            String type = StkUtils.judgeCondition(value);
            // 新闻资讯仅支持中文以及股票代码（即：数字）检索。传入中文时，匹配title属性；传入为数字时，匹配gp属性，非中文以及数字（即特殊符号等）查询全文匹配
            if(type.equals("0")){   // 表示传入值为中文
                fieldName = ElasticConstant.TITLE;
            } else if(type.equals("1")) {   // 为 1 表示传入值为字母
                fieldName = ElasticConstant.SPELL;
            } else if(type.equals("2")) {   // 为 2 表示传入值为数字
                fieldName = ElasticConstant.GP;
            }
            list = elasticsearchService.selectElasticsearch(ElasticConstant.NEWS_INDEX, ElasticConstant.NEWS_TYPE, fieldName, value, rowsPerPage, currentPage, false);
            if (list != null && list.size() > 0) {
                EsNewsInfoRespVo respVo = new EsNewsInfoRespVo();
                respVo.setResultInfo(list);
                responseVO.setResult(respVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
            responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
            logger.error("查询新闻资讯信息异常：{}", e.getMessage());
        }
        return responseVO;
    }
}
