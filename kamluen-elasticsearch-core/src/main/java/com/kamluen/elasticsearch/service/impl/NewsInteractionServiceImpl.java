package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.elasticsearch.dao.kamluen.NewsInteractionDao;
import com.kamluen.elasticsearch.entity.NewsInteraction;
import com.kamluen.elasticsearch.service.kamluen.NewsInteractionService;
import org.springframework.stereotype.Service;

/**
 * @Package: com.kamluen.elasticsearch.service.impl
 * @Author: LQW
 * @Date: 2019/6/26
 * @Description: 资讯互动信息接口实现类
 */
@Service
public class NewsInteractionServiceImpl extends ServiceImpl<NewsInteractionDao,NewsInteraction> implements NewsInteractionService {
}
