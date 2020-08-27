package com.kamluen.elasticsearch.utils;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * json 解析 工具类
 */
public class JsonUtils {

    public static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    public static List<Object> parseJson(String key, String jsonText) {
        List<Object> list = new ArrayList<>();
        try {
            JsonParser parser = new JsonParser();  //创建JSON解析器
            JsonObject object = (JsonObject) parser.parse(jsonText);  //创建JsonObject对象
            JsonArray array = object.get(key).getAsJsonArray();    //得到为json的数组
            for (int i = 0; i < array.size(); i++) {
                JsonObject subObject = array.get(i).getAsJsonObject();
                logger.debug("输出 json 数组信息：" + subObject);
                // json 数组解析，将json对象存入list并返回
                list.add(subObject);
            }
            logger.info("输出集合list：{}", list.toString());
        } catch (JsonIOException e) {
            e.printStackTrace();
            logger.error("json 解析发生IO异常，异常信息：{}", e.getMessage());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            logger.error("json 解析语法异常，异常信息：{}", e.getMessage());
        }
        return list;
    }
}
