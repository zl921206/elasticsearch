package com.kamluen.elasticsearch.entity.ElasticsearchVO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 包: com.kamluen.elasticsearch.entity.ElasticsearchVO
 * 开发者: LQW
 * 开发时间: 2019/5/20
 * 功能： 动态信息ES检索类
 */
@Data
public class EsPtfNoteInfo implements Serializable {

    private Integer ptfNoteId;

    private Integer userId;

    private Long uIdLong;

    private String userName;

    private String userIcon;

    private String noteType;

    //region step-1: 对应 ptfNoteInfo中的 busContent

    private List<Integer> atIds;
    private List<String> assets;
    private Integer artId;
    private String content;
    private String noteTitle ;
    private String title;
    private String newsImg;
    private String categoryName;
    private List<String> urls;
    private Integer type;
    //endregion

    private String busContent;
    /**
     * 是否有删除权限
     */
    private Integer perm;
    /**
     * 是否收藏
     */
    private Integer isLimit;

    private Date updateTime;
    /**
     * 是否关注
     */
    private Integer isReal;

    /**
     * 分享数量
     */
    private Integer noteShareNum;
    /**
     *  阅读数
     */
    private Integer noteReadNum;
}
