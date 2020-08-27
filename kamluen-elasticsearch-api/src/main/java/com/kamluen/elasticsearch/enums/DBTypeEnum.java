package com.kamluen.elasticsearch.enums;

public enum DBTypeEnum {
    /**
     * 股票信息库
     */
    mktinfo("mktinfo"),
    /**
     * ipo库
     */
    kamluen("kamluen"),
    /**
     * 策略库
     */
    strategy("strategy");

    private String value;

    DBTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
