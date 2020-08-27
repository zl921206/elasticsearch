package com.kamluen.elasticsearch.entity;

import java.util.List;

/**
 * @Package: com.kamluen.elasticsearch.entity
 * @Author: LQW
 * @Date: 2019/6/26
 * @Description: 资讯返回视图
 */
public class NewsInteractionVO{

    private Integer readNum;

    private Integer forwardNum;

    private List<NewsInteraction> list;

    public Integer getReadNum() {
        return readNum;
    }

    public void setReadNum(Integer readNum) {
        this.readNum = readNum;
    }

    public Integer getForwardNum() {
        return forwardNum;
    }

    public void setForwardNum(Integer forwardNum) {
        this.forwardNum = forwardNum;
    }

    public List<NewsInteraction> getList() {
        return list;
    }

    public void setList(List<NewsInteraction> list) {
        this.list = list;
    }
}
