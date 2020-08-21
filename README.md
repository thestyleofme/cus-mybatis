# cus_mybatis
手写mybatis

### 原生jdbc的问题分析
[原生jdbc的问题分析](doc/images/jdbc.png)

### 自定义持久层框架设计思路

[自定义持久层框架设计思路](doc/images/persistence.png)

[JDK动态代理](doc/images/proxy.png)

#### 使用端

1.  引入自定义持久层框架依赖
2.  提供配置信息

> 数据库配置信息

jdbc相关信息，jdbcUrl/driverClassName/username/password

```sqlMapConfig.xml```：存放数据库配置信息，存放mapper.xml的全路径
> sql配置信息

```mapper.xml```: 存放sql配置信息，sql语句、参数类型、返回值类型

#### 持久层框架
本质是对jdbc代码的封装
###### 1. 加载配置文件

根据配置文件的路径，加载配置文件为字节输入流，储存在内存中
创建```Resources```类 方法：```InputStream getResourceAsStream(String path)```
###### 2. 创建两个java bean：（容器对象）

存放的就是对配置文件解析出来的内容

```Configuration```: 核心配置类：存放```sqlMapConfig.xml```解析出来的内容

```MappedStatement```: 映射配置类：存放```mapper.xml```解析出来的内容

###### 3. 解析配置文件 dom4j

创建类：```SqlSessionFacroryBuilder``` 方法：```builder(InpoutStream in)```
使用dom4j解析配置文件，将解析出来的内容封装到容器对象
创建```SqlSessionFacrory```对象，生产```SqlSession```会话对象 （工厂模式）

###### 4. 创建```SqlSession```接口及实现类```DefaultSqlSession```

定义对数据库的curd操作：selectList()/selectOne()/update()/delete()

###### 5. 创建```Executor```接口及实现类```SimpleExecutor```实现类

```query(Configuration, MappedStatement, Object... params)```执行的就是JDBC代码

### 使用示例

详情请看```IPersistence_test/src/test/java/test/IPersistenceTest.java```

