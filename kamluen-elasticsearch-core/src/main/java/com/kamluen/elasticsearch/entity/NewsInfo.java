package com.kamluen.elasticsearch.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author zhanglei
 * @since 2018-11-01
 */
@TableName("mktinfo.sp_label_news")
public class NewsInfo extends Model<NewsInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 标签新闻ID
     */
    @TableId(value = "label_news_id", type = IdType.AUTO)
    private Integer labelNewsId;
    /**
     * 发布时间
     */
    @TableField("issue_time")
    private Date issueTime;
    /**
     * 新闻来源
     */
    @TableField("news_source")
    private String newsSource;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 来源地址
     */
    private String url;
    /**
     * 数据来源
     */
    @TableField("data_source")
    private String dataSource;
    /**
     * 是否有效
     */
    @TableField("is_status")
    private Integer isStatus;
    /**
     * 创建人
     */
    @TableField("create_opr")
    private Integer createOpr;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 修改人
     */
    @TableField("update_opr")
    private Integer updateOpr;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;
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
     * 是否要闻
     */
    @TableField("is_important")
    private Integer isImportant;
    /**
     * 8-头条 9-热点资讯
     */
    private Integer tag;
    /**
     * 是否直播
     */
    @TableField("is_live")
    private Integer isLive;
    @TableField("ext_id")
    private String extId;
    /**
     * 外部的时间
     */
    @TableField("ext_time")
    private Date extTime;
    /**
     * 错误次数
     */
    @TableField("error_times")
    private Integer errorTimes;
    /**
     * 存储资讯关联股票，用于全文索引搜索自选股资讯
     */
    private String gp;

    /**
     * 存储资讯关联股票名称，用于全文索引搜索自选股资讯
     */
    @TableField("stk_name")
    private String stkName;

    /**
     * 从cjzx表里同步的分类栏目ID
     */
    private Integer infotreeid;
    /**
     * 列表图片url
     */
    @TableField("img_url")
    private String imgUrl;
    /**
     * 纯文本内容
     */
    @TableField("content_text")
    private String contentText;
    /**
     * 摘要
     */
    private String summary;
    /**
     * 公告等文件的url
     */
    @TableField("pdf_url")
    private String pdfUrl;
    /**
     * 公告等文件的大小
     */
    @TableField("pdf_size")
    private Long pdfSize;
    /**
     * 新闻作者
     */
    @TableField("author_name")
    private String authorName;
    /**
     * 作者图像
     */
    @TableField("author_img")
    private String authorImg;


    public Integer getLabelNewsId() {
        return labelNewsId;
    }

    public void setLabelNewsId(Integer labelNewsId) {
        this.labelNewsId = labelNewsId;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Integer getIsStatus() {
        return isStatus;
    }

    public void setIsStatus(Integer isStatus) {
        this.isStatus = isStatus;
    }

    public Integer getCreateOpr() {
        return createOpr;
    }

    public void setCreateOpr(Integer createOpr) {
        this.createOpr = createOpr;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdateOpr() {
        return updateOpr;
    }

    public void setUpdateOpr(Integer updateOpr) {
        this.updateOpr = updateOpr;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    public Integer getIsImportant() {
        return isImportant;
    }

    public void setIsImportant(Integer isImportant) {
        this.isImportant = isImportant;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public Integer getIsLive() {
        return isLive;
    }

    public void setIsLive(Integer isLive) {
        this.isLive = isLive;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public Date getExtTime() {
        return extTime;
    }

    public void setExtTime(Date extTime) {
        this.extTime = extTime;
    }

    public Integer getErrorTimes() {
        return errorTimes;
    }

    public void setErrorTimes(Integer errorTimes) {
        this.errorTimes = errorTimes;
    }

    public String getGp() {
        return gp;
    }

    public void setGp(String gp) {
        this.gp = gp;
    }

    public Integer getInfotreeid() {
        return infotreeid;
    }

    public void setInfotreeid(Integer infotreeid) {
        this.infotreeid = infotreeid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public Long getPdfSize() {
        return pdfSize;
    }

    public void setPdfSize(Long pdfSize) {
        this.pdfSize = pdfSize;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorImg() {
        return authorImg;
    }

    public void setAuthorImg(String authorImg) {
        this.authorImg = authorImg;
    }

    public String getStkName() {
        return stkName;
    }

    public void setStkName(String stkName) {
        this.stkName = stkName;
    }

    @Override
    protected Serializable pkVal() {
        return this.labelNewsId;
    }


    @Override
    public String toString() {
        return "NewsInfo{" +
                "labelNewsId=" + labelNewsId +
                ", issueTime=" + issueTime +
                ", newsSource='" + newsSource + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", isStatus=" + isStatus +
                ", createOpr=" + createOpr +
                ", createTime=" + createTime +
                ", updateOpr=" + updateOpr +
                ", updateTime=" + updateTime +
                ", isConfirm=" + isConfirm +
                ", isRevise=" + isRevise +
                ", isImportant=" + isImportant +
                ", tag=" + tag +
                ", isLive=" + isLive +
                ", extId='" + extId + '\'' +
                ", extTime=" + extTime +
                ", errorTimes=" + errorTimes +
                ", gp='" + gp + '\'' +
                ", stkName='" + stkName + '\'' +
                ", infotreeid=" + infotreeid +
                ", imgUrl='" + imgUrl + '\'' +
                ", contentText='" + contentText + '\'' +
                ", summary='" + summary + '\'' +
                ", pdfUrl='" + pdfUrl + '\'' +
                ", pdfSize=" + pdfSize +
                ", authorName='" + authorName + '\'' +
                ", authorImg='" + authorImg + '\'' +
                '}';
    }
}
