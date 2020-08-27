package com.kamluen.elasticsearch.dao.kamluen;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kamluen.elasticsearch.entity.PtfFav;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Package: com.kamluen.elasticsearch.dao
 * @Author: LQW
 * @Date: 2019/5/29
 * @Description: 是否关注数据交互接口
 */
public interface PtfFavDao extends BaseMapper<PtfFav> {
    List<Integer> whetherToCollect(@Param("userId") Integer userId);
}
