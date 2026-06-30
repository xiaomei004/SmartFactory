package com.xiaomei.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class ProductPlan implements Serializable {
    private Integer id;
    private Integer flag;
    private Date createTime;
    private Integer createUserid;
    private Date updateTime;
    private Integer updateUserid;
    private String planSeq;
    private Integer orderId;
    private Integer productId;
    private Integer planCount;
    private Date deliveryDate;
    private Date planStartDate;
    private Date planEndDate;
    private Integer planStatus;
    private Integer factoryId;
}
