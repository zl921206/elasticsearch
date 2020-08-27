package com.kamluen.elasticsearch.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * 包: com.kamluen.elasticsearch.entity
 * 开发者: LQW
 * 开发时间: 2019/5/21
 * 功能： 用户信息实体类
 */
@TableName("kamluen.user_info")
public class UserInfo extends Model<UserInfo> {

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;
    @TableField("nick_name")
    private String nickName;
    private String signature;
    private Integer gender;
    @TableField("user_icon")
    private String userIcon;
    @TableField("user_source_channel_id")
    private String userSourceChannelId;
    @TableField("inv_user_id")
    private Integer invUserId;
    @TableField("vocation_id")
    private Integer vocationId;
    @TableField("user_type")
    private Integer userType;
    @TableField("adviser_type")
    private Integer adviserType;
    @TableField("user_status")
    private Integer userStatus;
    private String password;
    private String privacy;
    @TableField("friend_limit")
    private Integer friendLimit;
    @TableField("adviser_limit")
    private Integer adviserLimit;
    @TableField("ptf_fav_limit")
    private Integer ptfFavLimit;
    @TableField("group_limit")
    private Integer groupLimit;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("last_login_time")
    private Date lastLoginTime;
    @TableField("last_login_ip")
    private String lastLoginIp;
    @TableField("last_city_id")
    private Integer lastCityId;
    @TableField("login_count")
    private Integer loginCount;
    @TableField("im_id")
    private String imId;
    @TableField("im_pwd")
    private String imPwd;
    @TableField("pwd_error_count")
    private Integer pwdErrorCount;
    @TableField("lock_time")
    private Date lockTime;
    @TableField("lock_version")
    private Integer lockVersion;
    @TableField("trade_pwd")
    private String tradePwd;
    @TableField("trade_pwd_err_count")
    private Integer tradePwdErrCount;
    @TableField("cell_phone")
    private String cellPhone;
    @TableField("jf_group")
    private Integer jfGroup;
    @TableField("gesture_pwd")
    private String gesturePwd;
    @TableField("getsture_show_time")
    private Integer getstureShowTime;
    @TableField("area_code")
    private String areaCode;
    @TableField("reg_source_type")
    private String regSourceType;
    @TableField("reg_source")
    private String regSource;
    @TableField("area_no")
    private String areaNo;
    @TableField("fund_account")
    private String fundAccount;
    @TableField("account_type")
    private Integer accountType;
    @TableField("login_source")
    private String loginSource;
    @TableField("original_fund_account")
    private String originalFundAccount;
    @TableField("init_trans_pwd_sign")
    private Integer initTransPwdSign;
    @TableField("partner_type")
    private Integer partnerType;
    private String platform;
    @TableField("user_sign")
    private Integer userSign;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserSourceChannelId() {
        return userSourceChannelId;
    }

    public void setUserSourceChannelId(String userSourceChannelId) {
        this.userSourceChannelId = userSourceChannelId;
    }

    public Integer getInvUserId() {
        return invUserId;
    }

    public void setInvUserId(Integer invUserId) {
        this.invUserId = invUserId;
    }

    public Integer getVocationId() {
        return vocationId;
    }

    public void setVocationId(Integer vocationId) {
        this.vocationId = vocationId;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getAdviserType() {
        return adviserType;
    }

    public void setAdviserType(Integer adviserType) {
        this.adviserType = adviserType;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public Integer getFriendLimit() {
        return friendLimit;
    }

    public void setFriendLimit(Integer friendLimit) {
        this.friendLimit = friendLimit;
    }

    public Integer getAdviserLimit() {
        return adviserLimit;
    }

    public void setAdviserLimit(Integer adviserLimit) {
        this.adviserLimit = adviserLimit;
    }

    public Integer getPtfFavLimit() {
        return ptfFavLimit;
    }

    public void setPtfFavLimit(Integer ptfFavLimit) {
        this.ptfFavLimit = ptfFavLimit;
    }

    public Integer getGroupLimit() {
        return groupLimit;
    }

    public void setGroupLimit(Integer groupLimit) {
        this.groupLimit = groupLimit;
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

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Integer getLastCityId() {
        return lastCityId;
    }

    public void setLastCityId(Integer lastCityId) {
        this.lastCityId = lastCityId;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public String getImId() {
        return imId;
    }

    public void setImId(String imId) {
        this.imId = imId;
    }

    public String getImPwd() {
        return imPwd;
    }

    public void setImPwd(String imPwd) {
        this.imPwd = imPwd;
    }

    public Integer getPwdErrorCount() {
        return pwdErrorCount;
    }

    public void setPwdErrorCount(Integer pwdErrorCount) {
        this.pwdErrorCount = pwdErrorCount;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public Integer getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(Integer lockVersion) {
        this.lockVersion = lockVersion;
    }

    public String getTradePwd() {
        return tradePwd;
    }

    public void setTradePwd(String tradePwd) {
        this.tradePwd = tradePwd;
    }

    public Integer getTradePwdErrCount() {
        return tradePwdErrCount;
    }

    public void setTradePwdErrCount(Integer tradePwdErrCount) {
        this.tradePwdErrCount = tradePwdErrCount;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public Integer getJfGroup() {
        return jfGroup;
    }

    public void setJfGroup(Integer jfGroup) {
        this.jfGroup = jfGroup;
    }

    public String getGesturePwd() {
        return gesturePwd;
    }

    public void setGesturePwd(String gesturePwd) {
        this.gesturePwd = gesturePwd;
    }

    public Integer getGetstureShowTime() {
        return getstureShowTime;
    }

    public void setGetstureShowTime(Integer getstureShowTime) {
        this.getstureShowTime = getstureShowTime;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getRegSourceType() {
        return regSourceType;
    }

    public void setRegSourceType(String regSourceType) {
        this.regSourceType = regSourceType;
    }

    public String getRegSource() {
        return regSource;
    }

    public void setRegSource(String regSource) {
        this.regSource = regSource;
    }

    public String getAreaNo() {
        return areaNo;
    }

    public void setAreaNo(String areaNo) {
        this.areaNo = areaNo;
    }

    public String getFundAccount() {
        return fundAccount;
    }

    public void setFundAccount(String fundAccount) {
        this.fundAccount = fundAccount;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getLoginSource() {
        return loginSource;
    }

    public void setLoginSource(String loginSource) {
        this.loginSource = loginSource;
    }

    public String getOriginalFundAccount() {
        return originalFundAccount;
    }

    public void setOriginalFundAccount(String originalFundAccount) {
        this.originalFundAccount = originalFundAccount;
    }

    public Integer getInitTransPwdSign() {
        return initTransPwdSign;
    }

    public void setInitTransPwdSign(Integer initTransPwdSign) {
        this.initTransPwdSign = initTransPwdSign;
    }

    public Integer getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(Integer partnerType) {
        this.partnerType = partnerType;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Integer getUserSign() {
        return userSign;
    }

    public void setUserSign(Integer userSign) {
        this.userSign = userSign;
    }

    @Override
    protected Serializable pkVal() {
        return this.userId;
    }
}
