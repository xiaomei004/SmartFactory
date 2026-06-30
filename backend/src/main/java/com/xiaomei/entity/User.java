package com.xiaomei.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {
    private Integer id;
    private Integer flag;
    private Date createTime;
    private Integer createUserid;
    private Date updateTime;
    private Integer updateUserid;
    private Integer userStatus;
    private String userName;
    private String userRealName;
    private String userPasswd;
    private String userJobNum;
    private String userPhoneNum;
    private String userEmail;
    private Integer roleId;
    private Integer factoryId;
}
