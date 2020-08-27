package com.kamluen.elasticsearch.enums;

/**
 * @Package: com.kamluen.elasticsearch.enums
 * @Author: LQW
 * @Date: 2019/6/25
 * @Description: 动态互动类型
 */
public enum PtfNoteInteractionEnum{
    /**
     *  点赞
     */
    L("L","点赞"),
    /**
     *  评论
     */
    R("R","评论"),
    /**
     *  差评
     */
    D("D","点踩");

    private String code;

    private String message;

    PtfNoteInteractionEnum(String code, String message) {
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
