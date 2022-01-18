# rsql-mybatis
一款基于rsql的Mybatis查询工具

## 解决什么问题?
我们往往查询中根据需求动态修改查询的对象, 然后根据Mybatis动态sql实现不同查询。

有没有一种简单的方法实现这种功能？rsql-mybatis就可以做到。

## 查询语法
本项目语法解析基于[rsql-parser](https://github.com/jirutka/rsql-parser), 扩展了操作符。

### 操作符

1. 单值
- 等于： ==
- 不等于： !=
- 小于：=lt= 或 <
- 小于或等于：=le= 或 <=
- 大于：=gt= 或 >
- 大于或等于：=ge= 或 >=
- 全模糊：=like=
- 右模糊: =likeRight=
- 排序: =sort=

2. 多值
- 在： =in=
- 不在： =out=
- 在..之间: =between=

3. 连接符

- 并且: **;**
- 或者: **,**

### 语法使用案例

```
name=="王"  //SQL where name = "王"
name=="王";phone=="123xxxx1234" //SQL where name = "王" and phone = "123xxxx1234"
name=like="王";createdTime>=10000 //SQL where name like "%王%" and createdTime >= 10000
name=in=("赵","钱","孙") //SQL where name in ("赵","钱","孙")
price=bewteen=(0, 10000) //SQL where price bewteen 0 and 10000

关于or查询, 只提供最外层语法解析, 嵌套不会解析
比如: name=="王";(title=like="xxx",content=like="xxx") SQL where name = "王" and (title like "%xxx%" or content like "%xxx%")

```

### Maven

```
<dependency>
    <groupId>io.github.gravitymatrix</groupId>
    <artifactId>rsql-mybatis</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 在项目中使用

#### 1. 拦截器配置

- 需要注意拦截器顺序, 如果有分页拦截器请放在最后, 让它最后执行。
```
    @Bean
    public SearchConditionsInterceptor searchConditionsInterceptor() {
        return new SearchConditionsInterceptor();
    }
```

#### 2. 注解

1. 构建查询对象, 在对象中使用 **@SearchCondition** 注解
    ```java
    @Data
    public class AccountEntity{
    
        @SearchCondition(available = {SearchType.IN, SearchType.EQUAL, SearchType.LIKE})
        private String phone;
    
        @SearchCondition(available = SearchType.LIKE)
        private String name;
    
        @SearchCondition(available = {SearchType.BETWEEN, SearchType.GREATER_THAN_OR_EQUAL, SearchType.GREATER_THAN_OR_EQUAL, SearchType.LESS_THAN, SearchType.LESS_THAN_OR_EQUAL}, converter = DateToLongConverter.class)
        private Long time;
    }
    ```
2. 注解 **@SearchCondition**中 **available**为可解析的语法类型

   比如我name字段只想传入name=like="xxx"的时候才解析, 其他语法操作符传入则不会解析成对应的SQL语句
   
3. 不想暴露实体字段名字, 在注解别名字段
   ```
   @SearchCondition(alias = "aliasName", available = SearchType.LIKE)
   private String name;
   ```

4. 自定义转换器

   日期格式默认解析为time=between=(开始毫秒, 结束毫秒)
   
   ```
   @SearchCondition(available = SearchType.BETWEEN)
   private Date time;
   ```
   如何自定义解析规则 比如我想time=between=(2020-1-18, 2020-1-19)
   
   首先自定义解析器
   ```java
    public class MyStringToDateConverter implements SearchConverter<Date> {
        @Override
        public Date convert(String target) {
            //自定义转换规则
        }
    }
   ```
   然后
   ```
      @SearchCondition(available = SearchType.BETWEEN, converter = MyStringToDateConverter.class)
      private Date time;
   ``` 
   这样解析规则就会优先解析自定义规则。
5. 枚举类型

   使用Mybatis-Plus得话, 实体可直接为枚举类型, 使用 **@EnumValue**转换
   
   如果查询条件是枚举中的参数条件, 在枚举对应的参数上添加 **@SearchEnumType**
      
   
#### 3. 在MybatisMapper中传入搜索对象
   Mapper
   ```java
    public interface IAccountMapper{
    
        Page<AccountEntity> search(Page<AccountEntity> page, Searchable<AccountEntity> searchable);
        
    }
   ```
  调用
  ```
     String search = "(phone=like="xxx",name=like="xxx");time=between=(2022-01-08,2022-01-09)";

     accountMapper.search(new Page<>(1, 10), Searcher.builder(AccountEntity.class).search(search).build());

     //SQL
     SELECT * FROM t_account WHERE (phone LIKE ? OR name LIKE ?) AND time BETWEEN ? AND ?
     Parameters: %xxx%(String), %xxx%(String), 1641571200000(Long), 1641657600000(Long)
  ```

#### 4. 获取查询对象
   如何获取查询对象解析结果
   ```
      String search = "(phone=like="xxx",name=like="xxx");time=between=(2022-01-08,2022-01-09)";
      SearchBodyAccessor solve = SqlSearchSolver.solve(AccountEntity.class, search);
      for (SearchBodyAttributeAccessor searchBodyAttributeAccessor : solve) {
          System.out.println(searchBodyAttributeAccessor);
      }
   ```

## License
[Apache License](https://github.com/GravityMatrix/rsql-mybatis/blob/main/LICENSE)