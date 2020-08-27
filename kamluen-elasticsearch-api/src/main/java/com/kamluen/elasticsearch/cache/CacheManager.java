package com.kamluen.elasticsearch.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.kamluen.elasticsearch.cache
 * @Author: LQW
 * @Date: 2019/6/10
 * @Description:   缓存管理
 */
public class CacheManager {

    private static Map map = new HashMap();

    private CacheManager(){
        super();
    }

    /**
     *  得到缓存。同步静态方法
     * @param key
     * @return
     */
    private static synchronized Cache getCache(String key){
        return (Cache) map.get(key);
    }

    /**
     *  是否存在缓存
     * @param key
     * @return
     */
    public static synchronized boolean hasCache(String key){
        return map.containsKey(key);
    }

    /**
     *  使所有缓存失效
     */
    public static synchronized void invalidAll(){
        map.clear();
    }

    /**
     *  使单个缓存失效
     * @param key
     */
    public static synchronized void invalid(String key){
        map.remove(key);
    }

    /**
     *  缓存是否能被覆盖
     * @param key       键
     * @param value     值
     * @param flag      是否能被覆盖
     * @return
     */
    public static synchronized boolean putCacheIsItCovered(String key,Object value,boolean flag) {
        if (flag && hasCache(key)){
            putCacheContent(key,value,System.currentTimeMillis());
            return true;
        }else {
            return false;
        }
    }

    /**
     *  添加缓存
     * @param key
     * @param cache
     */
    private static synchronized void putCache(String key,Cache cache){
        map.put(key,cache);
    }

    /**
     *  读取缓存中的内容
     * @param key
     * @return
     */
    public static Cache getCacheContent(String key){
        if (hasCache(key)){
            Cache cache = getCache(key);
            if (cacheExpired(cache)){
                cache.setExpired(true);
            }
            return cache;
        }else {
            return null;
        }
    }


    /**
     *   赋予缓存内容
     * @param key
     * @param value
     * @param timeOut
     */
    public static void putCacheContent(String key,Object value,long timeOut){
        Cache cache = new Cache(key,value,timeOut,false);
        putCache(key,cache);
    }

    /**
     *  缓存是否过期
     * @param cache
     * @return
     */
    private static boolean cacheExpired(Cache cache) {
        if (cache == null) {
            return false;
        }
        long milisNow = System.currentTimeMillis();
        long milisExpire = cache.getTimeOut();
        // 缓存永远不会过期
        if (milisExpire < 0) {
            return false;
        } else if (milisNow >= milisExpire) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *  获取缓存的大小
     * @return
     */
    public static int getCacheSize(){
        return map.size();
    }
}
