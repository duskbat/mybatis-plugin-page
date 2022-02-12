package com.duskbat.mybatis.plugin.page.dialect;

import com.duskbat.mybatis.plugin.page.PageCondition;

/**
 * SQL Dialect, 需要自己实现对源SQL的编辑操作
 *
 * @author muweiye
 */
public interface Dialect {
    String SQL_END_DELIMITER = ";";
    String DISTINCT = "DISTINCT";

    /**
     * 获取分页SQL
     *
     * @param originalSQL original SQL
     * @param condition   page condition
     * @return SQL
     */
    String getPagedSQL(String originalSQL, PageCondition condition);

    /**
     * 获取count SQL
     *
     * @param originalSQL original SQL
     * @return count SQL
     */
    String getCountSQL(String originalSQL);

    /**
     * strategy reference
     *
     * @return 标签
     * @see DialectEnum
     */
    String channel();

}

