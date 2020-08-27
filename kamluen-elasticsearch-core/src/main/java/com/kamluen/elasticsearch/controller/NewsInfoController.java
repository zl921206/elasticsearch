package com.kamluen.elasticsearch.controller;

import com.kamluen.api.vo.ResponseVO;
import com.kamluen.elasticsearch.bean.req.EsNewsInfoReqVo;
import com.kamluen.elasticsearch.common.PaternalController;
import com.kamluen.protocol.StaticType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news_index")
public class NewsInfoController extends PaternalController {

    @RequestMapping("/search")
    public ResponseVO selectNewsInfo(@RequestBody EsNewsInfoReqVo vo) {
        ResponseVO responseVO = new ResponseVO();
        try {
            responseVO = newsInfoService.selectNewsInfoByEs(vo.getCondition(),vo.getRowsPerPage(),vo.getCurrentPage());
        } catch (Exception e) {
            e.printStackTrace();
            responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
            responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
            logger.error("通过Elasticsearch检索新闻资讯信息发生异常，异常信息{}", e.getMessage());
        }
        responseVO.setCode(StaticType.CodeType.OK.getCode());
        responseVO.setMessage(StaticType.CodeType.OK.getMessage());
        return responseVO;
    }
}
