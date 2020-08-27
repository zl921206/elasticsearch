package com.kamluen.elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kamluen.elasticsearch.dao.kamluen.PtfFavDao;
import com.kamluen.elasticsearch.entity.PtfFav;
import com.kamluen.elasticsearch.service.kamluen.PtfFavService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Package: com.kamluen.elasticsearch.service.impl
 * @Author: LQW
 * @Date: 2019/5/29
 * @Description: 是否关注接口实现类
 */
@Service
public class PtfFavServiceImpl extends ServiceImpl<PtfFavDao, PtfFav> implements PtfFavService {
    @Resource
    private PtfFavDao ptfFavDao;

    @Override
    public List<Integer> whetherToCollect(Integer userId) {
        return ptfFavDao.whetherToCollect(userId);
    }
}
