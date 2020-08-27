package com.kamluen.elasticsearch.bean.req;

import com.kamluen.elasticsearch.common.Page;

import java.io.Serializable;

/**
 * @author zhanglei
 *  热搜榜ES检索请求实体类
 * @date 2018-11-13
 */
public class EsSearchInfoReqVo extends Page implements Serializable {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
