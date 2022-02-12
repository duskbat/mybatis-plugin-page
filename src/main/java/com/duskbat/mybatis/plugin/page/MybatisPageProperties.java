package com.duskbat.mybatis.plugin.page;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置文件中添加: page-mybatis.dialectType: ${DialectEnum#MySQL}<br>
 * 例如: page-mybatis.dialectType: PostgreSQL<br>
 *
 * @author muweiye
 */

@Setter
@Getter
@ConfigurationProperties(prefix = "page-mybatis")
public class MybatisPageProperties {

    private String dialectType;

}
