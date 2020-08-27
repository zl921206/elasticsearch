package com.kamluen.elasticsearch.enums;

/**
 * @Package: com.kamluen.elasticsearch.enums
 * @Author: LQW
 * @Date: 2019/10/16
 * @Description:策略相关枚举
 */
public enum StrategyEnum {

    /**
     * 未启用
     */
    notenable(-2),
    /**
     * 启用
     */
    enable(-1),
    /**
     * 结束
     */
    end(1),
    /**
     * 删除
     */
    delete(0),
    /**
     * 未发布
     */
    unpublished(0),
    /**
     * 已发布
     */
    published(1),
    /**
     * 审核中
     */
    inreview(2);

    private Integer code;

    StrategyEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
