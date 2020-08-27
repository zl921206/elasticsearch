package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.elasticsearch.dao.kamluen.UserInfoBODao;
import com.kamluen.elasticsearch.entity.UserInfoBO;
import com.kamluen.elasticsearch.service.kamluen.UserInfoBOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 包: com.kamluen.elasticsearch.service.impl
 * 开发者: LQW
 * 开发时间: 2019/5/21
 * 功能：用户信息服务实现类
 */
@Service
public class UserInfoBOServiceImpl extends ServiceImpl<UserInfoBODao,UserInfoBO> implements UserInfoBOService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoBOServiceImpl.class);

    @Autowired
    private UserInfoBODao userInfoBODao;

    @Override
    public List<UserInfoBO> selectJoinQuery() {
        logger.info("进入UserInfoServiceImpl SelectJoinQuery方法,开始执行.....");
        List<UserInfoBO> infos = userInfoBODao.selectJoinQuery();
        logger.info("执行SelectJoinQuery方法完毕,执行结果{}",infos);
        if (infos.size() > 0){
            return infos;
        }
        return null;
    }

    @Override
    public List<UserInfoBO> selectNewFriendInformationById(Integer userId) {
        logger.info("进入UserInfoServiceImpl selectNewFriendInformationById方法,开始执行.....");
        List<UserInfoBO> userInfoBO = userInfoBODao.selectNewFriendInformationById(userId);
        if (null != userInfoBO){
            return userInfoBO;
        }
        return null;
    }
}
