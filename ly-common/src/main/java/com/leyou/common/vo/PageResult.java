package com.leyou.common.vo;

import lombok.Data;

import java.util.List;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/4 17:23
 * @description TODO
 **/
@Data
public class PageResult<T> {
    private Long total;
    private Integer totalPage;
    private List<T> items;

    public PageResult() {
    }

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(Long total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }
}
