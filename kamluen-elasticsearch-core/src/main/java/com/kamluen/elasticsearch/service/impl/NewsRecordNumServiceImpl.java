package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.elasticsearch.dao.kamluen.NewsRecordNumDao;
import com.kamluen.elasticsearch.entity.NewsRecordNum;
import com.kamluen.elasticsearch.service.kamluen.NewsRecordNumService;
import org.springframework.stereotype.Service;

/**
 * @Package: com.kamluen.elasticsearch.service.impl
 * @Author: LQW
 * @Date: 2019/6/26
 * @Description: 咨询数量记录接口实现类
 */
@Service
public class NewsRecordNumServiceImpl extends ServiceImpl<NewsRecordNumDao,NewsRecordNum> implements NewsRecordNumService {
}
