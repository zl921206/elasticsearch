package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.elasticsearch.dao.kamluen.CommonCommentInteractionDao;
import com.kamluen.elasticsearch.entity.CommonCommentInteraction;
import com.kamluen.elasticsearch.service.kamluen.CommonCommentInteractionService;
import org.springframework.stereotype.Service;

/**
 * @Package: com.kamluen.elasticsearch.service.impl
 * @Author: LQW
 * @Date: 2019/10/22
 * @Description:公共互动评论服务接口实现类
 */
@Service
public class CommonCommentInteractionServiceImpl extends ServiceImpl<CommonCommentInteractionDao,CommonCommentInteraction> implements CommonCommentInteractionService {
}
