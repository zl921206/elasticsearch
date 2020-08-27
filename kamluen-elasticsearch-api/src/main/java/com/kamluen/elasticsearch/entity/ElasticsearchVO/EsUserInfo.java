package com.kamluen.elasticsearch.entity.ElasticsearchVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 包: com.kamluen.elasticsearch.entity.ElasticsearchVO
 * 开发者: LQW
 * 开发时间: 2019/5/21
 * 功能： 用戶信息ES检索类
 */
public class EsUserInfo implements Serializable {

    private String userId;
    private String nickName;
    private String userIcon;
    private String privacy;
    private String cellPhone;
    private Date updateTime;
    private Long uIdLong;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getuIdLong() {
        return uIdLong;
    }

    public void setuIdLong(Long uIdLong) {
        this.uIdLong = uIdLong;
    }
}
