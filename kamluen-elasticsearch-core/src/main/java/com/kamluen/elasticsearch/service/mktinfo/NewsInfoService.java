package com.kamluen.elasticsearch.service.mktinfo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kamluen.api.vo.ResponseVO;
import com.kamluen.elasticsearch.entity.NewsInfo;

/**
 * @author zhanglei
 * 新闻资讯信息服务接口
 * @date 2018-11-01
 */
public interface NewsInfoService extends IService<NewsInfo> {

    /**
     * 从ES中查询新闻资讯信息数据
     * @return
     */
    ResponseVO selectNewsInfoByEs(String stockCode, Integer rowsPerPage, Integer currentPage);
}
