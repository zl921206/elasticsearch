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
 * @Description:公共评论三方实体类
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("kamluen.common_user_comment")
public class CommonUserComment extends Model<CommonUserComment> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 评论id
     */
    @TableField("comment_id")
    private Long commentId;
    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 点赞状态（0=未激活，1=激活）
     */
    @TableField("thumbs_up")
    private Integer thumbsUp;
    /**
     * 点踩（0=未激活，1=激活）
     */
    @TableField("thumbs_down")
    private Integer thumbsDown;
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
