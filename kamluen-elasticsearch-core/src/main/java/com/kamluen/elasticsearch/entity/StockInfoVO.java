package com.kamluen.elasticsearch.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Package: com.kamluen.elasticsearch.entity
 * @Author: LQW
 * @Date: 2019/8/7
 * @Description:
 */
@Data
public class StockInfoVO implements Serializable {
    private String assetId;
    private String stockName;
    private int secType;
    private int secSType;
    private double price;
    private double change;
    private double changePct;
}
