package com.kamluen.elasticsearch.entity.ElasticsearchVO;

import java.io.Serializable;

/**
 * @author zhanglei
 *  股票基本信息ES检索实体类
 * @date 2018-11-02
 */
public class EsStockInfo implements Serializable {

	/** [股票基本信息检索] */
	private static final long serialVersionUID = -1256242655693685993L;

	private String assetId;//资产ID

	private String stkCode;//股票代码

	private String stkName;//股票名称

	private String spelling;//拼音

	private String spellingAbbr;//拼音简称
	/**
	 * 股票详细分类
	 */
	private Integer stkType;

	/**
	 * 市场分类：HK、US、SH、SZ
	 */
	private String mkt;

	/**
	 * 股票分类（1：港股 沪深：{SZ：2，SH：3} 美股：4）
	 * 用于ES检索排序使用
	 */
	private Integer mktType;

	/**
	 * 证券类别（1：股票(正股)，2：债券，3：基金，4：权证(涡轮·牛熊)，5：指数）
	 */
	private Integer secType;

	/**
	 * 查询检索条件
	 */
	private String  condition;
	
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

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Integer getMktType() {
		return mktType;
	}

	public void setMktType(Integer mktType) {
		this.mktType = mktType;
	}

	public Integer getSecType() {
		return secType;
	}

	public void setSecType(Integer secType) {
		this.secType = secType;
	}
}
