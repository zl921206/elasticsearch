package com.kamluen.elasticsearch.dao.kamluen;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kamluen.elasticsearch.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @Package: com.kamluen.elasticsearch.dao
 * @Author: LQW
 * @Date: 2019/5/29
 * @Description: 用户信息数据交互接口
 */
public interface UserInfoDao extends BaseMapper<UserInfo> {

    UserInfo selectByNickName(@Param("userId") Integer userId);
}
