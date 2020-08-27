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
 * @Description:公共互动评论实体类
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("kamluen.common_comment_interaction")
public class CommonCommentInteraction extends Model<CommonCommentInteraction> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 互动id
     */
    @TableField("interaction_id")
    private Long interactionId;

    /**
     * 头像
     */
    @TableField("head_url")
    private String headUrl;

    /**
     * 昵称
     */
    @TableField("nick_name")
    private String nickName;

    /**
     * 回复目标的昵称
     */
    @TableField("to_nick_name")
    private String toNickName;

    /**
     * 互动用户
     */
    @TableField("from_user")
    private Long fromUser;

    /**
     * 互动目标用户
     */
    @TableField("to_user")
    private Long toUser;

    /**
     * 父级评论id
     */
    @TableField("parent_id")
    private Long parentId;


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
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 内容显示（0=不显示，1=显示）
     */
    @TableField("is_content_show")
    private Integer isContentShow;

    /**
     * 状态（0=无效，1=有效）
     */
    @TableField("status")
    private Integer status;

    /**
     * AiteId
     */
    @TableField("at_ids")
    private String atIds;

    /**
     * 是否是回复子评论 false=否， true=是
     */
    @TableField("is_reply")
    private Integer isReply;

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
