package com.kamluen.elasticsearch.dao.kamluen;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kamluen.elasticsearch.entity.UserInfoBO;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 包: com.kamluen.elasticsearch.dao
 * 开发者: LQW
 * 开发时间: 2019/5/21
 * 功能：用户信息数据交互接口
 */
public interface UserInfoBODao extends BaseMapper<UserInfoBO> {

    /**
     *  每次查询导入10000条
     * @return
     */
    @Select("SELECT\n" +
            "\tuInfo.user_id,\n" +
            "\tuInfo.nick_name,\n" +
            "\tuInfo.user_icon,\n" +
            "\tuInfo.user_status,\n" +
            "\tuInfo.privacy,\n" +
            "\tuInfo.cell_phone,\n" +
            "\tuInfo.update_time,\n" +
            "\tfInfo.is_status,\n" +
            "\tfInfo.update_time AS fUpdateTime,\n" +
            "\tfnInfo.user_id AS fnUserId,\n" +
            "\tfnInfo.target_user_id,\n" +
            "\tfnInfo.req_status,\n" +
            "\tfnInfo.req_direction,\n" +
            "\tfnInfo.update_time AS fnUpdateTime\n" +
            "FROM\n" +
            "\tkamluen.user_info AS uInfo\n" +
            "LEFT JOIN kamluen.user_friend AS fInfo ON uInfo.user_id = fInfo.user_id\n" +
            "LEFT JOIN kamluen.user_friend_new AS fnInfo ON uInfo.user_id = fnInfo.user_id\n" +
            "GROUP BY\n" +
            "\tuInfo.user_id\n" +
            "LIMIT 0,\n" +
            " 10000")
    List<UserInfoBO> selectJoinQuery();

    @Select("SELECT user_id,req_direction,req_status,target_user_id,is_status,update_time FROM kamluen.user_friend_new WHERE user_id = #{userId} ")
    List<UserInfoBO> selectNewFriendInformationById(Integer userId);
}
