package com.kamluen.elasticsearch.bean.req;

import com.kamluen.elasticsearch.common.Page;

import java.io.Serializable;

/**
 * 包: com.kamluen.elasticsearch.bean.req
 * 开发者: LQW
 * 开发时间: 2019/5/16
 * 功能： 公共的请求对象
 */
public class CommonEsInfoReqVo extends Page implements Serializable {

    private EsNewsInfoReqVo newsInfoReqVo;

    private EsStockInfoReqVo stockInfoReqVo;

    private EsDynamicInfoReqVo dynamicInfoReqVo;

    private EsUserInfoReqVo userInfoReqVo;

    public EsNewsInfoReqVo getNewsInfoReqVo() {
        return newsInfoReqVo;
    }

    public void setNewsInfoReqVo(EsNewsInfoReqVo newsInfoReqVo) {
        this.newsInfoReqVo = newsInfoReqVo;
    }

    public EsStockInfoReqVo getStockInfoReqVo() {
        return stockInfoReqVo;
    }

    public void setStockInfoReqVo(EsStockInfoReqVo stockInfoReqVo) {
        this.stockInfoReqVo = stockInfoReqVo;
    }

    public EsDynamicInfoReqVo getDynamicInfoReqVo() {
        return dynamicInfoReqVo;
    }

    public void setDynamicInfoReqVo(EsDynamicInfoReqVo dynamicInfoReqVo) {
        this.dynamicInfoReqVo = dynamicInfoReqVo;
    }

    public EsUserInfoReqVo getUserInfoReqVo() {
        return userInfoReqVo;
    }

    public void setUserInfoReqVo(EsUserInfoReqVo userInfoReqVo) {
        this.userInfoReqVo = userInfoReqVo;
    }
}
