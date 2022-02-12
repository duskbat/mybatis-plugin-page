package com.duskbat.mybatis.plugin.page.dialect;

import com.duskbat.mybatis.plugin.page.PageCondition;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 分页sql语句 Mysql语法支持
 *
 * @author muweiye
 */
@Component
public class MySQLDialect implements Dialect {
    private static final Pattern SELECT_PATTERN = Pattern.compile("^select\\s+(\\b[\\s\\S]+?)\\s*\\bfrom\\b([\\S\\s]+?)(\\border\\s+by[\\s\\S]+)$", Pattern.CASE_INSENSITIVE);

    @Override
    public String getPagedSQL(String originalSQL, PageCondition condition) {
        originalSQL = originalSQL.trim();
        if (originalSQL.endsWith(SQL_END_DELIMITER)) {
            originalSQL = originalSQL.substring(0, originalSQL.length() - 1);
        }
        int start = condition.getPageSize() * (condition.getPageNumber() - 1);
        originalSQL += String.format(" limit %d, %d", start, condition.getPageSize());
        return originalSQL;
    }

    @Override
    public String getCountSQL(String originalSQL) {
        Matcher matcher = SELECT_PATTERN.matcher(originalSQL);
        boolean found = matcher.find();
        if (found) {
            String fields = matcher.group(1);
            String fromExpression = matcher.group(2);

            StringBuilder sql = new StringBuilder(originalSQL.length() + 20);
            sql.append("select ");
            fields = fields.trim();
            if (fields.length() > DISTINCT.length()) {
                String maybeDistinct = fields.substring(0, DISTINCT.length());
                if (DISTINCT.compareToIgnoreCase(maybeDistinct) == 0) {
                    sql.append("distinct ");
                }
            }
            sql.append("COUNT(*) as cn");
            sql.append(" from ");
            sql.append(fromExpression);
            return sql.toString();
        } else {
            throw new RuntimeException("mybatis can not generate count sql");
        }

    }

    @Override
    public String channel() {
        return DialectEnum.MySQL.name();
    }

}