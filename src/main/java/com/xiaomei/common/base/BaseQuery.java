package com.xiaomei.common.base;

import lombok.Data;

@Data
public class BaseQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Integer factoryId;
}
