package com.xiaomei.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Equipment implements Serializable {
    private Integer id;
    private Integer flag;
    private Date createTime;
    private Integer createUserid;
    private Date updateTime;
    private Integer updateUserid;
    private String equipmentSeq;
    private String equipmentName;
    private String equipmentImgUrl;
    private Integer equipmentStatus;
    private Integer factoryId;
}
