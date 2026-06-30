package com.xiaomei.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Factory implements Serializable {
    private Integer id;
    private Integer flag;
    private Date createTime;
    private Integer createUserid;
    private Date updateTime;
    private Integer updateUserid;
    private String bak;
    private String factoryName;
    private String factoryImgUrl;
    private String factoryAddr;
    private String factoryUrl;
    private Integer factoryWorker;
    private Integer factoryStatus;
}
