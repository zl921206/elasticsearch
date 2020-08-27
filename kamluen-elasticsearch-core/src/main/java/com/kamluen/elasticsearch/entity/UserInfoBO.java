package com.kamluen.elasticsearch.entity;


import java.util.Date;

/**
 * 包: com.kamluen.elasticsearch.entity
 * 开发者: LQW
 * 开发时间: 2019/5/21
 * 功能： 连接查询所需参数实体类
 */
public class UserInfoBO extends UserInfo {

    private Integer friendId;
    private Integer friendUserId;
    private Integer isStatus;
    /**
     * user_friend 中 update_time AS fUpdateTime
     */
    private Date fUpdateTime;
    /**
     * user_friend_new 中 user_id AS fnUserId;
     */
    private Integer fnUserId;

    private Integer targetUserId;

    private String reqStatus;

    private String reqDirection;
    /**
     * user_friend_new 中 update_time AS fnUpdateTime;
     */
    private Date fnUpdateTime;

    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    public Integer getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(Integer friendUserId) {
        this.friendUserId = friendUserId;
    }

    public Integer getIsStatus() {
        return isStatus;
    }

    public void setIsStatus(Integer isStatus) {
        this.isStatus = isStatus;
    }

    public Date getfUpdateTime() {
        return fUpdateTime;
    }

    public void setfUpdateTime(Date fUpdateTime) {
        this.fUpdateTime = fUpdateTime;
    }

    public Integer getFnUserId() {
        return fnUserId;
    }

    public void setFnUserId(Integer fnUserId) {
        this.fnUserId = fnUserId;
    }

    public Integer getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Integer targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(String reqStatus) {
        this.reqStatus = reqStatus;
    }

    public String getReqDirection() {
        return reqDirection;
    }

    public void setReqDirection(String reqDirection) {
        this.reqDirection = reqDirection;
    }

    public Date getFnUpdateTime() {
        return fnUpdateTime;
    }

    public void setFnUpdateTime(Date fnUpdateTime) {
        this.fnUpdateTime = fnUpdateTime;
    }
}
