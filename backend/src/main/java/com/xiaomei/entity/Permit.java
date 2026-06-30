package com.xiaomei.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class Permit implements Serializable {
    private Integer id;
    private Integer flag;
    private Integer status;
    private Integer parentId;
    private String permitName;
    private String permitDesc;
    private String permitPath;
}
