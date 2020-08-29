package com.kamluen.elasticsearch;

import lombok.Data;

/**
 * 用户信息实体对象
 */

@Data
public class UserInfo {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户英文姓名
     */
    private String userNameEn;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户年龄
     */
    private String age;

    /**
     * 用户性别
     */
    private String sex;
}
