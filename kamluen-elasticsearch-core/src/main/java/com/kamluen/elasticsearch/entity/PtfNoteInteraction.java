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
 * @Date: 2019/5/29
 * @Description: 是否收藏实体对象
 */
@TableName("kamluen.ptf_note_interaction")
public class PtfNoteInteraction extends Model<PtfNoteInteraction> {

    @TableId(value = "inter_id", type = IdType.AUTO)
    private Integer interId;
    @TableField("client_req_id")
    private String clientReqId;
    @TableField("ptf_note_id")
    private Integer ptfNoteId;
    @TableField("inter_type")
    private String interType;
    @TableField("from_user")
    private Integer fromUser;
    @TableField("to_user")
    private Integer toUser;
    private String content;
    @TableField("is_comment")
    private Integer isComment;
    @TableField("parent_inter_id")
    private Integer parentInterId;
    @TableField("is_status")
    private Integer isStatus;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("note_user")
    private Integer noteUser;
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

    public Integer getPtfNoteId() {
        return ptfNoteId;
    }

    public void setPtfNoteId(Integer ptfNoteId) {
        this.ptfNoteId = ptfNoteId;
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

    public Integer getIsComment() {
        return isComment;
    }

    public void setIsComment(Integer isComment) {
        this.isComment = isComment;
    }

    public Integer getParentInterId() {
        return parentInterId;
    }

    public void setParentInterId(Integer parentInterId) {
        this.parentInterId = parentInterId;
    }

    public Integer getIsStatus() {
        return isStatus;
    }

    public void setIsStatus(Integer isStatus) {
        this.isStatus = isStatus;
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

    public Integer getNoteUser() {
        return noteUser;
    }

    public void setNoteUser(Integer noteUser) {
        this.noteUser = noteUser;
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
