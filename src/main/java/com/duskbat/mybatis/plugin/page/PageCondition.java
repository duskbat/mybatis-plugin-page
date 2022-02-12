package com.duskbat.mybatis.plugin.page;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author muweiye
 */
@Getter
@Setter
public class PageCondition implements Serializable {
    private static final long serialVersionUID = -7723656137942629594L;

    private final int pageSize;
    private final int pageNumber;
    private int totalCount;
    private boolean disablePagePlugin;

    public PageCondition(int pageSize, int pageNumber) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public boolean isDisablePagePlugin() {
        return this.disablePagePlugin;
    }

}