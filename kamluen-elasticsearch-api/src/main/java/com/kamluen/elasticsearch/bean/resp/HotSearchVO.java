package com.kamluen.elasticsearch.bean.resp;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

public class HotSearchVO implements Serializable {

    private static final long serialVersionUID = -5825256737892206413L;
    /**
     * 主键ID
     */
    private String id;

    /**
     * 股票名称
     */
    private String stockName;
    /**
     * 搜索量
     */
    private BigInteger searchNum;

    /**
     * 更新时间
     */
    private Date updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public BigInteger getSearchNum() {
        return searchNum;
    }

    public void setSearchNum(BigInteger searchNum) {
        this.searchNum = searchNum;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
