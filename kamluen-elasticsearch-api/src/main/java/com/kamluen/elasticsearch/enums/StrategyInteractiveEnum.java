package com.kamluen.elasticsearch.enums;

/**
 * @Package: com.kamluen.elasticsearch.enums
 * @Author: LQW
 * @Date: 2019/10/22
 * @Description:策略互动枚举类
 */
public enum StrategyInteractiveEnum {

    /**
     * 已点踩
     */
    SteppedOn(1, "S", "已点踩"),

    /**
     * 未点踩
     */
    NotSteppedOn(0, "NS", "未点踩"),

    /**
     * 已点赞
     */
    Liked(1, "L", "已点赞"),

    /**
     * 未点赞
     */
    NotLiked(0, "NL", "未点赞");

    private Integer code;

    private String interactiveState;

    private String msg;

    StrategyInteractiveEnum(Integer code, String interactiveState, String msg) {
        this.code = code;
        this.interactiveState = interactiveState;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInteractiveState() {
        return interactiveState;
    }

    public void setInteractiveState(String interactiveState) {
        this.interactiveState = interactiveState;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
