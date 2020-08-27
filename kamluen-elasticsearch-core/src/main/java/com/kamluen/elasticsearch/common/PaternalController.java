package com.kamluen.elasticsearch.common;

import com.kamluen.elasticsearch.service.*;
import com.kamluen.elasticsearch.service.mktinfo.NewsInfoService;
import com.kamluen.elasticsearch.service.mktinfo.StockInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PaternalController {

    public static final Logger logger = LoggerFactory.getLogger(PaternalController.class);

    @Resource
    public RocketMQService rocketMQService;
    @Resource
    public ElasticsearchService elasticsearchService;
    @Resource
    public StockInfoService stockInfoService;
    @Resource
    public NewsInfoService newsInfoService;
    @Resource
    public CombinedSearchService searchService;
}
