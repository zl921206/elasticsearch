package com.kamluen.elasticsearch.service.kamluen;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kamluen.elasticsearch.entity.UserInfoBO;

import java.util.List;
import java.util.Map;

/**
 * 包: com.kamluen.elasticsearch.service
 * 开发者: LQW
 * 开发时间: 2019/5/21
 * 功能： 用户信息服务接口
 */
public interface UserInfoBOService extends IService<UserInfoBO> {
    List<UserInfoBO> selectJoinQuery();

    /**
     *  根据id获取新朋友信息
     * @param userId
     * @return
     */
    List<UserInfoBO> selectNewFriendInformationById(Integer userId);
}
