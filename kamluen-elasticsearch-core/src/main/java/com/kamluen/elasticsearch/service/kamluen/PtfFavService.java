package com.kamluen.elasticsearch.service.kamluen;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kamluen.elasticsearch.entity.PtfFav;

import java.util.List;

/**
 * @Package: com.kamluen.elasticsearch.service
 * @Author: LQW
 * @Date: 2019/5/29
 * @Description: 是否关注接口
 */
public interface PtfFavService extends IService<PtfFav> {
    /**
     *  是否收藏
     * @param userId    用户id
     * @return  List<Integer>
     */
    List<Integer> whetherToCollect(Integer userId);
}
