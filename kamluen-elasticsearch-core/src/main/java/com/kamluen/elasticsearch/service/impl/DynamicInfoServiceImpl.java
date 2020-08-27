package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.elasticsearch.dao.kamluen.DynamicInfoDao;
import com.kamluen.elasticsearch.entity.PtfNoteInfo;
import com.kamluen.elasticsearch.service.kamluen.DynamicInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 包: com.kamluen.elasticsearch.service.impl
 * 开发者: LQW
 * 开发时间: 2019/5/20
 * 功能：动态信息服务接口实现类
 */
@Service
public class DynamicInfoServiceImpl extends ServiceImpl<DynamicInfoDao,PtfNoteInfo> implements DynamicInfoService {

    private static final Logger logger = LoggerFactory.getLogger(NewsInfoServiceImpl.class);
}
