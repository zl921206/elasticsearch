package com.kamluen.elasticsearch.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 包: com.kamluen.elasticsearch.common
 * 开发者: LQW
 * 开发时间: 2019/5/16
 * 功能： 综合搜索共有父类
 */
@Data
public class MutualInfo implements Serializable {

    /**
     * 查询检索条件
     */
    private String condition;
    /**
     * 搜索对象状态
     * 默认为综合搜索
     * 1 : 股票
     * 2 : 资讯
     * 3 : 动态
     * 4 : 用户
     * 5 : 策略
     */
    private Integer type;

    /**
     * 用户id(羊羊id)
     */
    private Integer userId;

}
