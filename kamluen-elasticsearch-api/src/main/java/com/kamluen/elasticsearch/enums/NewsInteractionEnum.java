package com.kamluen.elasticsearch.enums;

/**
 * @Package: com.kamluen.elasticsearch.enums
 * @Author: LQW
 * @Date: 2019/6/26
 * @Description: 资讯互动类型
 */
public enum NewsInteractionEnum {
    /**
     *  点赞
     */
    LIKE("like","点赞"),
    /**
     * 差评
     */
    DISLIKE("disLike","点踩"),
    /**
     * 评论
     */
    REPLY("reply","评论"),

    /**
     *  对评论的回复
     */
    COMMENTREPLY("commentReply","评论回复");

    private String code;

    private String message;

    NewsInteractionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
