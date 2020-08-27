package com.kamluen.elasticsearch.cache;

/**
 * @Package: com.kamluen.elasticsearch.cache
 * @Author: LQW
 * @Date: 2019/6/10
 * @Description: 用于单个缓存
 */
public class Cache {

    private String key;
    private Object value;
    /**
     *  过期时间
     */
    private long timeOut;
    /**
     * 是否过期 默认为不过期
     */
    private boolean expired;

    public Cache() {
    }

    public Cache(String key, Object value, long timeOut, boolean expired) {
        this.key = key;
        this.value = value;
        this.timeOut = timeOut;
        this.expired = expired;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
