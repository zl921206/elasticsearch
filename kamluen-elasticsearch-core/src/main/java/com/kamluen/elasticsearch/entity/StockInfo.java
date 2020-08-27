package com.kamluen.elasticsearch.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhanglei
 * @since 2018-11-01
 */
@TableName("mktinfo.asset_info")
public class StockInfo extends Model<StockInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 资产ID
     */
    @TableId("asset_id")
    private String assetId;
    /**
     * 股票代码
     */
    @TableField("stk_code")
    private String stkCode;
    /**
     * (香港/A股)映射股票代码
     */
    @TableField("map_stk_code")
    private String mapStkCode;
    /**
     * 市场
     */
    @TableField("mkt_code")
    private String mktCode;
    /**
     * 证券类别
     */
    @TableField("sec_type")
    private Integer secType;
    /**
     * 细分类别
     */
    @TableField("sec_stype")
    private Integer secStype;
    /**
     * 公司ID
     */
    @TableField("corp_id")
    private Integer corpId;
    /**
     * 股票简称
     */
    @TableField("stk_name")
    private String stkName;
    /**
     * 股票全称
     */
    @TableField("stk_name_long")
    private String stkNameLong;
    /**
     * 拼音简称
     */
    @TableField("spelling_abbr")
    private String spellingAbbr;
    /**
     * 拼音全称
     */
    private String spelling;
    /**
     * 英文名称
     */
    @TableField("eng_name")
    private String engName;
    /**
     * 板块代码
     */
    @TableField("board_code")
    private Integer boardCode;
    /**
     * 每手股数
     */
    @TableField("lot_size")
    private Integer lotSize;
    /**
     * 涨跌幅限制
     */
    @TableField("change_limit")
    private BigDecimal changeLimit;
    /**
     * 上市日期
     */
    @TableField("listing_date")
    private Date listingDate;
    /**
     * 退市日期
     */
    @TableField("delist_date")
    private Date delistDate;
    /**
     * 币种代码
     */
    @TableField("ccy_type")
    private String ccyType;
    /**
     * 最新版本号
     */
    private Integer version;
    /**
     * 记录创建版本号
     */
    @TableField("add_version")
    private BigInteger addVersion;
    /**
     * 记录股票是否可以交易的种类
     */
    @TableField("is_invest")
    private Integer isInvest;
    /**
     * 是否支持模拟交易
     */
    @TableField("is_simu_invest")
    private Integer isSimuInvest;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 记录外部系统时间
     */
    @TableField("ext_time")
    private Date extTime;
    /**
     * 记录状态
     */
    @TableField("is_status")
    private Integer isStatus;
    /**
     * 是否确认
     */
    @TableField("is_confirm")
    private Integer isConfirm;
    /**
     * 是否修改
     */
    @TableField("is_revise")
    private Integer isRevise;
    /**
     * 上市发行价
     */
    @TableField("issue_price")
    private BigDecimal issuePrice;
    /**
     * 美股交易市场
     */
    private String exchange;


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

    public String getMapStkCode() {
        return mapStkCode;
    }

    public void setMapStkCode(String mapStkCode) {
        this.mapStkCode = mapStkCode;
    }

    public String getMktCode() {
        return mktCode;
    }

    public void setMktCode(String mktCode) {
        this.mktCode = mktCode;
    }

    public Integer getSecType() {
        return secType;
    }

    public void setSecType(Integer secType) {
        this.secType = secType;
    }

    public Integer getSecStype() {
        return secStype;
    }

    public void setSecStype(Integer secStype) {
        this.secStype = secStype;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public String getStkName() {
        return stkName;
    }

    public void setStkName(String stkName) {
        this.stkName = stkName;
    }

    public String getStkNameLong() {
        return stkNameLong;
    }

    public void setStkNameLong(String stkNameLong) {
        this.stkNameLong = stkNameLong;
    }

    public String getSpellingAbbr() {
        return spellingAbbr;
    }

    public void setSpellingAbbr(String spellingAbbr) {
        this.spellingAbbr = spellingAbbr;
    }

    public String getSpelling() {
        return spelling;
    }

    public void setSpelling(String spelling) {
        this.spelling = spelling;
    }

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName;
    }

    public Integer getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(Integer boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getLotSize() {
        return lotSize;
    }

    public void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    public BigDecimal getChangeLimit() {
        return changeLimit;
    }

    public void setChangeLimit(BigDecimal changeLimit) {
        this.changeLimit = changeLimit;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public Date getDelistDate() {
        return delistDate;
    }

    public void setDelistDate(Date delistDate) {
        this.delistDate = delistDate;
    }

    public String getCcyType() {
        return ccyType;
    }

    public void setCcyType(String ccyType) {
        this.ccyType = ccyType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BigInteger getAddVersion() {
        return addVersion;
    }

    public void setAddVersion(BigInteger addVersion) {
        this.addVersion = addVersion;
    }

    public Integer getIsInvest() {
        return isInvest;
    }

    public void setIsInvest(Integer isInvest) {
        this.isInvest = isInvest;
    }

    public Integer getIsSimuInvest() {
        return isSimuInvest;
    }

    public void setIsSimuInvest(Integer isSimuInvest) {
        this.isSimuInvest = isSimuInvest;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getExtTime() {
        return extTime;
    }

    public void setExtTime(Date extTime) {
        this.extTime = extTime;
    }

    public Integer getIsStatus() {
        return isStatus;
    }

    public void setIsStatus(Integer isStatus) {
        this.isStatus = isStatus;
    }

    public Integer getIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(Integer isConfirm) {
        this.isConfirm = isConfirm;
    }

    public Integer getIsRevise() {
        return isRevise;
    }

    public void setIsRevise(Integer isRevise) {
        this.isRevise = isRevise;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    protected Serializable pkVal() {
        return this.assetId;
    }

    @Override
    public String toString() {
        return "AssetInfo{" +
        ", assetId=" + assetId +
        ", stkCode=" + stkCode +
        ", mapStkCode=" + mapStkCode +
        ", mktCode=" + mktCode +
        ", secType=" + secType +
        ", secStype=" + secStype +
        ", corpId=" + corpId +
        ", stkName=" + stkName +
        ", stkNameLong=" + stkNameLong +
        ", spellingAbbr=" + spellingAbbr +
        ", spelling=" + spelling +
        ", engName=" + engName +
        ", boardCode=" + boardCode +
        ", lotSize=" + lotSize +
        ", changeLimit=" + changeLimit +
        ", listingDate=" + listingDate +
        ", delistDate=" + delistDate +
        ", ccyType=" + ccyType +
        ", version=" + version +
        ", addVersion=" + addVersion +
        ", isInvest=" + isInvest +
        ", isSimuInvest=" + isSimuInvest +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", extTime=" + extTime +
        ", isStatus=" + isStatus +
        ", isConfirm=" + isConfirm +
        ", isRevise=" + isRevise +
        ", issuePrice=" + issuePrice +
        ", exchange=" + exchange +
        "}";
    }
}
