# Mybatis Plugin

## page

**通过拦截器实现分页**

1. 引入配置类  
   通过继承或指明ComponentScan或Spring.factories等 各种方式

   ```
    1. class MyConfig extends com.duskbat.mybatis.plugin.page.config.MybatisPageConfig
    或
    2. @ComponentScan(basePackageClasses = {com.duskbat.mybatis.plugin.page.config.MybatisPageConfig.class})
    ```
2. 添加方言配置  
   配置文件中添加所需的 dialect

   > 可以自己新增不同SQL的 dialect
    ```yaml
    # DialectEnum
    page-mybatis.dialectType: PostgreSQL
    ```
3. 在DAO的方法参数中加入分页参数
    ```Java
    // PageCondition
    // 例如
    public class MyDao {
        List<Object> pageSearch(@Param("condition") PageCondition pageCondition, @Param("myParam") String myParam);
    }
    ```
4. query SQL need "order by"