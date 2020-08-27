package com.kamluen.elasticsearch.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * @Package: com.kamluen.elasticsearch.entity
 * @Author: LQW
 * @Date: 2019/6/26
 * @Description: 资讯互动
 */
@TableName("kamluen.news_interaction")
public class NewsInteraction extends Model<NewsInteraction> {

    @TableId(value = "inter_id", type = IdType.AUTO)
    private Integer interId;
    @TableField("client_req_id")
    private String clientReqId;
    @TableField("news_id")
    private Integer newsId;
    @TableField("inter_type")
    private String interType;
    @TableField("from_user")
    private Integer fromUser;
    @TableField("to_user")
    private Integer toUser;
    private String content;
    @TableField("is_first_commentReply")
    private Integer isFirstCommentReply;
    @TableField("comment_id")
    private Integer commentId;
    @TableField("status")
    private Integer status;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("at_ids")
    private String atIds;

    public Integer getInterId() {
        return interId;
    }

    public void setInterId(Integer interId) {
        this.interId = interId;
    }

    public String getClientReqId() {
        return clientReqId;
    }

    public void setClientReqId(String clientReqId) {
        this.clientReqId = clientReqId;
    }

    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    public String getInterType() {
        return interType;
    }

    public void setInterType(String interType) {
        this.interType = interType;
    }

    public Integer getFromUser() {
        return fromUser;
    }

    public void setFromUser(Integer fromUser) {
        this.fromUser = fromUser;
    }

    public Integer getToUser() {
        return toUser;
    }

    public void setToUser(Integer toUser) {
        this.toUser = toUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getIsFirstCommentReply() {
        return isFirstCommentReply;
    }

    public void setIsFirstCommentReply(Integer isFirstCommentReply) {
        this.isFirstCommentReply = isFirstCommentReply;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getAtIds() {
        return atIds;
    }

    public void setAtIds(String atIds) {
        this.atIds = atIds;
    }

    @Override
    protected Serializable pkVal() {
        return this.interId;
    }
}
