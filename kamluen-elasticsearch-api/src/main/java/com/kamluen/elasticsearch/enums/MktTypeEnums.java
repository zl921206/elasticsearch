package com.kamluen.elasticsearch.enums;

/**
 * 市场类别枚举类
 */
public enum MktTypeEnums {

    /**
     * 港股
     */
    HK("HK", 1),
    /**
     * 沪深
     */
    SZ("SZ", 2),
    SH("SH", 3),
    /**
     * 美股
     */
    US("US", 4);

    private String typeName;
    private int typeValue;

    private MktTypeEnums(String typeName, Integer typeValue) {
        this.typeName = typeName;
        this.typeValue = typeValue;
    }

    public int getTypeValue() {
        return this.typeValue;
    }

    public String getTypeName() {
        return typeName;
    }
}
