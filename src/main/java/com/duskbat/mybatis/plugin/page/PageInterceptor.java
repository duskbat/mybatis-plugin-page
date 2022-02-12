package com.duskbat.mybatis.plugin.page;


import com.duskbat.mybatis.plugin.page.dialect.Dialect;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author muweiye
 */
@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
)})
public class PageInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageInterceptor.class);

    private static final String BOUND_SQL_KEY = "delegate.boundSql.sql";
    private static final String MAPPED_STATEMENT_KEY = "delegate.mappedStatement";

    @Autowired
    private MybatisPageProperties mybatisPageProperties;
    @Autowired
    private Set<Dialect> dialectSet;

    private Map<String, Dialect> dialectMap;

    @PostConstruct
    public void init() {
        dialectMap = dialectSet.stream().collect(Collectors.toMap(Dialect::channel, o -> o));
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        LOGGER.debug("PageInterceptor intercept");
        PageCondition condition = null;
        // 找到SQL parameter里的pageCondition
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        Object parameter = statementHandler.getBoundSql().getParameterObject();
        if (parameter instanceof Map<?, ?>) {
            Map<?, ?> parameterMap = (Map<?, ?>) parameter;
            for (Object o : parameterMap.values()) {
                if (o instanceof PageCondition) {
                    condition = (PageCondition) o;
                    break;
                }
            }
        } else if (parameter instanceof PageCondition) {
            condition = (PageCondition) parameter;
        }
        // 是否需要分页
        if (condition != null && !condition.isDisablePagePlugin()) {
            Dialect dbDialect = this.findSQLDialect();

            String originalSql = statementHandler.getBoundSql().getSql();
            LOGGER.debug("origin sql: " + originalSql);

            String pagedSQL = dbDialect.getPagedSQL(originalSql, condition);
            LOGGER.debug("paged sql: " + pagedSQL);

            MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
            metaObject.setValue(BOUND_SQL_KEY, pagedSQL);

            this.setTotalCount(invocation, statementHandler, metaObject, dbDialect, condition, originalSql);
        } else {
            LOGGER.debug("PageCondition parameter is null, exit");
        }
        return invocation.proceed();
    }

    /**
     * 复制的param, 重构SQL
     */
    private void setTotalCount(Invocation invocation, StatementHandler statementHandler, MetaObject metaStatementHandler, Dialect dbDialect, PageCondition condition, String originalSql) throws SQLException {
        String countSql = dbDialect.getCountSQL(originalSql);
        LOGGER.debug("count sql: " + countSql);
        Connection connection = (Connection) invocation.getArgs()[0];
        BoundSql boundSql = statementHandler.getBoundSql();
        // 预编译的SQL statement; This object can then be used to efficiently execute this statement multiple times.代理对象
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            statement = connection.prepareStatement(countSql);
            // mapping DAO-->Mapper
            MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue(MAPPED_STATEMENT_KEY);
            ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            rs = statement.executeQuery();
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            condition.setTotalCount(totalCount);

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    private Dialect findSQLDialect() {
        String dialectType = mybatisPageProperties.getDialectType();
        Dialect dialect = dialectMap.get(dialectType);
        if (dialect == null) {
            throw new RuntimeException("can not find \"pageMybatis.dialectType\" in mybatis config file");
        } else {
            return dialect;
        }
    }


}
