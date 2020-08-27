package com.kamluen.elasticsearch.bean.req;

import com.kamluen.elasticsearch.common.Page;

import java.io.Serializable;

/**
 * @author zhanglei
 *  股票基本信息ES检索请求实体类
 * @date 2018-11-02
 */
public class EsStockInfoReqVo extends Page implements Serializable {

	/** [股票基本信息检索] */
	private static final long serialVersionUID = -1256242655693685993L;

	private String assetId;//资产ID

	private String stkCode;//股票代码

	private String stkName;//股票名称

	private String spelling;//拼音

	private String spellingAbbr;//拼音简称
	
	private Integer stkType;//股票详细分类
	
	private String mkt; // 市场分类：HK、US、SH、SZ

	public String getMkt() {
		return mkt;
	}

	public void setMkt(String mkt) {
		this.mkt = mkt;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getStkCode() {
		return stkCode;
	}

	public void setStkCode(String stkCode) {
		this.stkCode = stkCode;
	}

	public String getStkName() {
		return stkName;
	}

	public void setStkName(String stkName) {
		this.stkName = stkName;
	}

	public String getSpelling() {
		return spelling;
	}

	public void setSpelling(String spelling) {
		this.spelling = spelling;
	}

	public String getSpellingAbbr() {
		return spellingAbbr;
	}

	public void setSpellingAbbr(String spellingAbbr) {
		this.spellingAbbr = spellingAbbr;
	}

	public Integer getStkType() {
		return stkType;
	}

	public void setStkType(Integer stkType) {
		this.stkType = stkType;
	}

}
