package com.duskbat.mybatis.plugin.page;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * @author muweiye
 */
@Getter
public class Page<T> implements Serializable {
    private static final long serialVersionUID = -4724602471930683166L;

    private final int pageSize;
    private final int pageNumber;
    private int totalCount;
    private List<T> items;

    public Page(int pageSize, int pageNumber) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

}
