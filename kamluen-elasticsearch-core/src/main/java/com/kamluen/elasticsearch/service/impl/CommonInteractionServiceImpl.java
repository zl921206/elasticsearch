package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.elasticsearch.dao.kamluen.CommonInteractionDao;
import com.kamluen.elasticsearch.entity.CommonInteraction;
import com.kamluen.elasticsearch.service.kamluen.CommonInteractionService;
import org.springframework.stereotype.Service;

/**
 * @Package: com.kamluen.elasticsearch.service.impl
 * @Author: LQW
 * @Date: 2019/10/22
 * @Description:公共互动服务接口实现类
 */
@Service
public class CommonInteractionServiceImpl extends ServiceImpl<CommonInteractionDao,CommonInteraction> implements CommonInteractionService {
}
