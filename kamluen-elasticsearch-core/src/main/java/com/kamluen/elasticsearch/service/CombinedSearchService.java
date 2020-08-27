package com.kamluen.elasticsearch.service;

import com.kamluen.api.vo.ResponseVO;
import com.kamluen.elasticsearch.bean.req.CommonEsInfoReqVo;

import java.util.List;

/**
 * 包: com.kamluen.elasticsearch.service
 * 开发者: LQW
 * 开发时间: 2019/5/16
 * 功能： 综合搜索
 */
public interface CombinedSearchService {
    /**
     *  综合选择搜索
     * @param vo
     * @return
     */
    List<ResponseVO> combinedSelection(CommonEsInfoReqVo vo);
}
