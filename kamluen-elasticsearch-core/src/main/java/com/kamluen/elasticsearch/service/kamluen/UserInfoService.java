package com.kamluen.elasticsearch.service.kamluen;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kamluen.elasticsearch.entity.UserInfo;
import com.kamluen.elasticsearch.entity.UserInfoBO;

/**
 * @Package: com.kamluen.elasticsearch.service
 * @Author: LQW
 * @Date: 2019/5/29
 * @Description: 用户信息服务接口
 */
public interface UserInfoService extends IService<UserInfo> {

    UserInfo selectByNickName(Integer userId);
}
