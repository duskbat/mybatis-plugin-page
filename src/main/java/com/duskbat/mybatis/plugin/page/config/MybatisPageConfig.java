package com.duskbat.mybatis.plugin.page.config;

import com.duskbat.mybatis.plugin.page.MybatisPageProperties;
import com.duskbat.mybatis.plugin.page.PageInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author muweiye
 */
@Configuration
@ComponentScan(basePackages = {"com.duskbat.mybatis.plugin.page.dialect"})
@EnableConfigurationProperties(MybatisPageProperties.class)
public class MybatisPageConfig {

    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

}
