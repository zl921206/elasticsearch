package com.kamluen.elasticsearch.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * 包: com.kamluen.elasticsearch.entity
 * 开发者: LQW
 * 开发时间: 2019/5/20
 * 功能：
 */
@TableName("kamluen.ptf_note")
public class PtfNoteInfo extends Model<PtfNoteInfo> {

    @TableId(value = "ptf_note_id", type = IdType.AUTO)
    private Integer ptfNoteId;
    @TableField("c_note_id")
    private String cNoteId;
    @TableField("ptf_id")
    private Integer ptfId;
    @TableField("user_id")
    private Integer userId;
    @TableField("ptf_creator_id")
    private Integer ptfCreatorId;
    @TableField("note_type")
    private String noteType;
    @TableField("bus_content")
    private String busContent;
    @TableField("is_real")
    private Integer isReal;
    @TableField("is_status")
    private Integer isStatus;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("ptf_trans_id")
    private Integer ptfTransId;
    @TableField("note_share_num")
    private Integer noteShareNum;
    @TableField("note_read_num")
    private Integer noteReadNum;

    public Integer getPtfNoteId() {
        return ptfNoteId;
    }

    public void setPtfNoteId(Integer ptfNoteId) {
        this.ptfNoteId = ptfNoteId;
    }

    public String getcNoteId() {
        return cNoteId;
    }

    public void setcNoteId(String cNoteId) {
        this.cNoteId = cNoteId;
    }

    public Integer getPtfId() {
        return ptfId;
    }

    public void setPtfId(Integer ptfId) {
        this.ptfId = ptfId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPtfCreatorId() {
        return ptfCreatorId;
    }

    public void setPtfCreatorId(Integer ptfCreatorId) {
        this.ptfCreatorId = ptfCreatorId;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getBusContent() {
        return busContent;
    }

    public void setBusContent(String busContent) {
        this.busContent = busContent;
    }

    public Integer getIsReal() {
        return isReal;
    }

    public void setIsReal(Integer isReal) {
        this.isReal = isReal;
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

    public Integer getPtfTransId() {
        return ptfTransId;
    }

    public void setPtfTransId(Integer ptfTransId) {
        this.ptfTransId = ptfTransId;
    }

    public Integer getNoteShareNum() {
        return noteShareNum;
    }

    public void setNoteShareNum(Integer noteShareNum) {
        this.noteShareNum = noteShareNum;
    }

    public Integer getNoteReadNum() {
        return noteReadNum;
    }

    public void setNoteReadNum(Integer noteReadNum) {
        this.noteReadNum = noteReadNum;
    }

    @Override
    protected Serializable pkVal() {
        return this.ptfNoteId;
    }
}
