package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.elasticsearch.dao.kamluen.CommonUserInteractionDao;
import com.kamluen.elasticsearch.entity.CommonUserInteraction;
import com.kamluen.elasticsearch.service.kamluen.CommonUserInteractionService;
import org.springframework.stereotype.Service;

/**
 * @Package: com.kamluen.elasticsearch.service.impl
 * @Author: LQW
 * @Date: 2019/10/22
 * @Description:公共(策略)用户互动服务接口实现类
 */
@Service
public class CommonUserInteractionServiceImpl extends ServiceImpl<CommonUserInteractionDao,CommonUserInteraction> implements CommonUserInteractionService {
}
