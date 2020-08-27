package com.kamluen.elasticsearch.controller;

import com.kamluen.api.vo.ResponseVO;
import com.kamluen.elasticsearch.bean.req.CommonEsInfoReqVo;
import com.kamluen.elasticsearch.common.PaternalController;
import com.kamluen.protocol.StaticType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包: com.kamluen.elasticsearch.controller
 * 开发者: LQW
 * 开发时间: 2019/5/14
 * 功能： 综合搜索
 */
@RestController
@RequestMapping("/synthesize_index")
public class ComprehensiveSearchController extends PaternalController {


    /**
     *   综合检索
     * @param condition 搜索关键值
     * @return
     */
    @RequestMapping("/search")
    public Map<String,List<ResponseVO>> selectDynamicInfo(@RequestBody CommonEsInfoReqVo condition) {
        ResponseVO responseVO = new ResponseVO();
        Map<String,List<ResponseVO>> map = new HashMap<>();
        List<ResponseVO> list = new ArrayList<>();
        try {
            list = searchService.combinedSelection(condition);
        } catch (Exception e) {
            e.printStackTrace();
            responseVO.setCode(StaticType.CodeType.INTERNAL_ERROR.getCode());
            responseVO.setMessage(StaticType.CodeType.INTERNAL_ERROR.getMessage());
            logger.error("综合搜索时发生异常,异常信息{}",e);
            list.add(responseVO);
        }
        map.put("list",list);
        return map;
    }
}
