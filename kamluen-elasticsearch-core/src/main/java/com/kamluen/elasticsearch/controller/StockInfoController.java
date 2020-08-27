package com.kamluen.elasticsearch.controller;

import com.kamluen.api.vo.ResponseVO;
import com.kamluen.elasticsearch.bean.req.EsStockInfoReqVo;
import com.kamluen.elasticsearch.common.PaternalController;
import com.kamluen.protocol.StaticType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock_index")
public class StockInfoController extends PaternalController {

    @RequestMapping("/search")
    public ResponseVO selectStockInfo(@RequestBody EsStockInfoReqVo vo) {
        ResponseVO responseVO = new ResponseVO();
        try {
            responseVO = stockInfoService.selectStockInfoByEs(vo.getCondition(),vo.getRowsPerPage(),vo.getCurrentPage());
        } catch (Exception e) {
            e.printStackTrace();
            responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
            responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
            logger.error("通过Elasticsearch检索股票基本信息发生异常，异常信息{}", e.getMessage());
        }
        responseVO.setCode(StaticType.CodeType.OK.getCode());
        responseVO.setMessage(StaticType.CodeType.OK.getMessage());
        return responseVO;
    }

}
