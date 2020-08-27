package com.kamluen.elasticsearch.bean.req;

import com.kamluen.elasticsearch.common.Page;

import java.io.Serializable;

/**
 * 包: com.kamluen.elasticsearch.bean.req
 * 开发者: LQW
 * 开发时间: 2019/5/16
 * 功能：
 */
public class EsUserInfoReqVo extends Page implements Serializable {

    /**
     *  昵称
     */
    private String nickName;
    /**
     *  用户id(羊羊id)
     */
    private Integer userId;
    /**
     * 手机号
     */
    private String cellPhoneNumber ;


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }
}
