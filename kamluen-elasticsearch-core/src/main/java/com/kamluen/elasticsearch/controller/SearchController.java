package com.kamluen.elasticsearch.controller;

import com.kamluen.api.vo.ResponseVO;
import com.kamluen.elasticsearch.bean.req.EsSearchInfoReqVo;
import com.kamluen.elasticsearch.bean.resp.EsSearchInfoRespVo;
import com.kamluen.elasticsearch.common.PaternalController;
import com.kamluen.elasticsearch.constant.ElasticConstant;
import com.kamluen.elasticsearch.utils.StringUtils;
import com.kamluen.protocol.StaticType;
import com.kamluen2.common.message.MessageProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search_index")
public class SearchController extends PaternalController {

    @Resource
    MessageProducer producer;
    /**
     * 生产者标题
     */
    @Value("${apache.kafka.producer.producerTopic}")
    private String producerTopic;

    /**
     * 热搜关键词统计(动词,不是名词)
     *
     * @param vo
     */
    @RequestMapping("/count")
    public ResponseVO searchKeywordCount(@RequestBody EsSearchInfoReqVo vo) {
        ResponseVO responseVO = new ResponseVO();
        try {
            if (StringUtils.isEmpty(vo.getText())) {
                responseVO.setCode(StaticType.CodeType.OK.getCode());
                responseVO.setMessage(StaticType.CodeType.OK.getMessage());
                logger.info("用户输入关键词为空......");
                return responseVO;
            }
            String timeStamp = System.currentTimeMillis() + "";
            producer.sendWithFlush(new ProducerRecord<>(producerTopic, timeStamp, timeStamp + "," + vo.getText()));
            logger.info("记录关键词：" + vo.getText() + "，推送kafka成功......");
//            SendResult result = rocketMQService.sendMsg(vo.getText());
//            if(result != null && result.getSendStatus().equals(SendStatus.SEND_OK)){
//                logger.info("记录关键词：" + vo.getText() + "，推送rocketMQ成功......");
//            } else {
//                logger.info("记录关键词：" + vo.getText() + "，推送rocketMQ失败,响应码：" + result.getSendStatus());
//            }
            responseVO.setCode(StaticType.CodeType.OK.getCode());
            responseVO.setMessage(StaticType.CodeType.OK.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
            responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
//            logger.error("记录关键词推送rocketMQ异常，异常信息{}", e.getMessage());
            logger.error("记录关键词推送kafka异常，异常信息{}", e.getMessage());
        }
        return responseVO;
    }

    /**
     * 获取关键词热搜榜数据
     *
     * @param vo
     * @return
     */
    @RequestMapping("/search")
    public ResponseVO selectSearchRank(@RequestBody EsSearchInfoReqVo vo) {
        ResponseVO responseVO = new ResponseVO();
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            // 目前仅返回十条数据,当获取第二页时直接返回
            if (vo.getCurrentPage() > 1) {
                return responseVO;
            }
            list = elasticsearchService.selectElasticsearch(ElasticConstant.SEARCH_INDEX, ElasticConstant.SEARCH_TYPE, null, null, vo.getRowsPerPage(), vo.getCurrentPage(), false);

            for(Map<String, Object> map :list)
            {
                BigDecimal count=new BigDecimal(String.valueOf(map.get("count")));
                int result=count.compareTo(new BigDecimal(10000));
               if (result!=-1)
               {
                   //count大于1万,显示规则是1.2w,
                   BigDecimal count2=count.divide(BigDecimal.valueOf(10000)).
                           setScale(1,BigDecimal.ROUND_DOWN);
                   map.put("count",count2+"w");
               }

            }

            if (list != null && list.size() > 0) {
                EsSearchInfoRespVo respVo = new EsSearchInfoRespVo();
                respVo.setResultInfo(list);
                responseVO.setResult(respVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
            responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
            logger.error("通过Elasticsearch获取热搜榜数据发生异常，异常信息{}", e.getMessage());
        }
        responseVO.setCode(StaticType.CodeType.OK.getCode());
        responseVO.setMessage(StaticType.CodeType.OK.getMessage());
        return responseVO;
    }
}
