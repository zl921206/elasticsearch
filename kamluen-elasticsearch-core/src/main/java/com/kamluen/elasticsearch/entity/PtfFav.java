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
 * @Description: 是否关注实体对象
 */
@TableName("kamluen.ptf_fav")
public class PtfFav extends Model<PtfFav> {

    @TableId(value="ptf_fav_id",type = IdType.AUTO)
    private Integer ptfFavId;
    @TableField("user_id")
    private Integer userId;
    @TableField("ptf_id")
    private Integer ptfId;
    @TableField("display_no")
    private Integer displayNo;
    private Integer owner;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("is_status")
    private Integer isStatus;
    @TableField("is_see")
    private Integer isSee;
    @TableField("from_user_id")
    private Integer fromUserId;


    public Integer getPtfFavId() {
        return ptfFavId;
    }

    public void setPtfFavId(Integer ptfFavId) {
        this.ptfFavId = ptfFavId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPtfId() {
        return ptfId;
    }

    public void setPtfId(Integer ptfId) {
        this.ptfId = ptfId;
    }

    public Integer getDisplayNo() {
        return displayNo;
    }

    public void setDisplayNo(Integer displayNo) {
        this.displayNo = displayNo;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
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

    public Integer getIsStatus() {
        return isStatus;
    }

    public void setIsStatus(Integer isStatus) {
        this.isStatus = isStatus;
    }

    public Integer getIsSee() {
        return isSee;
    }

    public void setIsSee(Integer isSee) {
        this.isSee = isSee;
    }

    public Integer getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Integer fromUserId) {
        this.fromUserId = fromUserId;
    }

    @Override
    protected Serializable pkVal() {
        return this.ptfFavId;
    }
}
