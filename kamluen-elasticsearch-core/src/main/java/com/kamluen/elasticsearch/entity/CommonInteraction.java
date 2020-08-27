package com.kamluen.elasticsearch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

/**
 * @Package: com.kamluen.elasticsearch.entity
 * @Author: LQW
 * @Date: 2019/10/22
 * @Description:公共互动实体类
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("kamluen.common_interaction")
public class CommonInteraction extends Model<CommonInteraction> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模块类型（0=公告，新增的请自行添加模块的值） 1：策略
     */
    @TableField("module_type")
    private Integer moduleType;

    /**
     * 模块id
     */
    @TableField("module_id")
    private String moduleId;
    /**
     * 已读数量
     */
    @TableField("read_num")
    private Integer readNum;
    /**
     * 转发数量
     */
    @TableField("forward_num")
    private Integer forwardNum;
    /**
     * 点赞数量
     */
    @TableField("thumbs_up_count")
    private Integer thumbsUpCount;
    /**
     * 点踩数量
     */
    @TableField("thumbs_down_count")
    private Integer thumbsDownCount;
    /**
     * 状态（0=无效，1=有效）
     */
    @TableField("status")
    private Integer status;

    /**
     * 乐观锁版本号
     */
    @TableField("version")
    private Long version;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
