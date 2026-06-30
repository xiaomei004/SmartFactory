package com.xiaomei.common.base;

import lombok.Data;
import java.util.List;

@Data
public class PageObject<T> {
    private List<T> dataList;
    private PageInfo pageInfo;
    private String status = "ok";

    public PageObject() {}

    public PageObject(List<T> dataList, Integer pageNum, Integer pageSize, Long total) {
        this.dataList = dataList;
        this.pageInfo = new PageInfo(pageNum, pageSize, total);
        this.status = "ok";
    }

    public static <T> PageObject<T> error(String msg) {
        PageObject<T> po = new PageObject<>();
        po.setStatus("error");
        po.setPageInfo(new PageInfo(1, 10, 0L));
        po.getPageInfo().setMsg(msg);
        return po;
    }

    @Data
    public static class PageInfo {
        private Integer pageNum;
        private Integer pageSize;
        private Long total;
        private String msg;

        public PageInfo() {}

        public PageInfo(Integer pageNum, Integer pageSize, Long total) {
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.total = total;
        }
    }
}
