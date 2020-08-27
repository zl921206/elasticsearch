package com.kamluen.elasticsearch.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.elasticsearch.dao.kamluen.UserInfoDao;
import com.kamluen.elasticsearch.entity.UserInfo;
import com.kamluen.elasticsearch.entity.UserInfoBO;
import com.kamluen.elasticsearch.service.kamluen.UserInfoService;
import com.kamluen.elasticsearch.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Package: com.kamluen.elasticsearch.service.impl
 * @Author: LQW
 * @Date: 2019/5/29
 * @Description: 用户信息实现类
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoDao,UserInfo> implements UserInfoService {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);
    @Resource
    private UserInfoDao userInfoDao;

    @Override
    public UserInfo selectByNickName(Integer userId) {
        logger.info("进入UserInfoServiceImpl selectByNickName方法,开始执行.....");
        UserInfo userInfo = userInfoDao.selectByNickName(userId);
        logger.info("执行selectByNickName方法完毕,执行结果{}",userInfo);
        if (StringUtils.isNotNull(userInfo)){
            return userInfo;
        }
        return null;
    }
}
