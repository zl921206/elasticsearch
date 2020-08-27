package com.kamluen.elasticsearch.entity.ElasticsearchVO;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhanglei
 *  新闻资讯信息ES检索实体类
 * @date 2018-11-02
 */
public class EsNewsInfo implements Serializable {

    private static final long serialVersionUID = 9010801195531764645L;

    private Integer labelNewsId;
    private String title;
    private Date issueTime;
    private String gp;
    private String imgUrl;
    private Integer infotreeid;
    private String newsSource;
    private Integer tag;
    private String stkName;

    private String spell;
    private String spellAbbr;

    public Integer getLabelNewsId() {
        return labelNewsId;
    }

    public void setLabelNewsId(Integer labelNewsId) {
        this.labelNewsId = labelNewsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    public String getGp() {
        return gp;
    }

    public void setGp(String gp) {
        this.gp = gp;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getInfotreeid() {
        return infotreeid;
    }

    public void setInfotreeid(Integer infotreeid) {
        this.infotreeid = infotreeid;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getSpellAbbr() {
        return spellAbbr;
    }

    public void setSpellAbbr(String spellAbbr) {
        this.spellAbbr = spellAbbr;
    }

    public String getStkName() {
        return stkName;
    }

    public void setStkName(String stkName) {
        this.stkName = stkName;
    }
}
