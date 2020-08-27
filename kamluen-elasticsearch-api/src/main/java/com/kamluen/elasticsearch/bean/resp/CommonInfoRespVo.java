package com.kamluen.elasticsearch.bean.resp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 包: com.kamluen.elasticsearch.common
 * 开发者: LQW
 * 开发时间: 2019/5/16
 * 功能：公共的ES检索响应实体类
 */
public class CommonInfoRespVo implements Serializable {

    private static final long serialVersionUID = 9010801195531764645L;

    /**
     *  返回时用于判断是哪个对象的type
     *  1 : 股票
     *  2 : 资讯
     *  3 : 动态
     *  4 : 用户
     *  5 : 策略
     *
     */
    Integer ObjectType;

    /**
     * 返回结果包装
     */
    List<Map<String, Object>> resultInfo;


    public Integer getObjectType() {
        return ObjectType;
    }

    public void setObjectType(Integer objectType) {
        ObjectType = objectType;
    }

    public List<Map<String, Object>> getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(List<Map<String, Object>> resultInfo) {
        this.resultInfo = resultInfo;
    }
}
