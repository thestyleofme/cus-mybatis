 ### 1. Mybatis动态sql是做什么的？都有哪些动态sql？简述一下动态sql的执行原理？

- #### 动态sql的概念

动态sql是指进行sql操作的时候，根据传入的参数对象或者参数值，根据设置的条件，
如判断是否为空，循环，拼接等情况，最后会执行不同的sql

- #### 动态sql有哪些

>  <if>
判断传入的参数值是否符合条件，如字符串判断是否为空以及空串

```
<if test="name != null and name != ''">
 and name = #{name}
</if>

<where>
作为动态拼接查询条件，与<if>标签配合使用时，可不用显示声明where 1=1来防止多出的and
<where>
    <if test="name != null and name != ''">
     and name = #{name}
    </if>
</where>
```
> <include>
可以将重复的sql内容提起出来，使用<include>引用即可，减少重复编写

```
<sql>
    id, name
</sql>
<select id="findAll"  resultType="user">
  select 
  <include refid="userInfo"/> 
  from user
</select>
```
> <choose>、<when>、<otherwise>

相当于if/else，可走不用的条件进行拼接sql

```
<where>
<choose>
  <when test="name != null and name != ''">
    AND name = #{name}
  </when>
  <otherwise>
    AND id = #{id}
  </otherwise>
</choose>
</where>
```
> <foreach>

foreach标签可以把传入的集合对象进行遍历，然后把每一项的内容作为参数传到sql语句中，里面涉及到 item(具体的每一个对象), index(序号), open(开始符), close(结束符), separator(分隔符)

```
<insert id="insertBatch">
  insert into user (id, name)
  values
  <foreach collection="list" item="user" separator="," >
    (#{user.id}, #{user.name})
  </foreach>
</insert>

<select id="selectList" resultType="user">
  SELECT * from user WHERE id in
  <foreach collection="list" item="id" open="(" close=")" separator="," >
    #{id}
  </foreach>
</select>
```
> <set>

作为更新语句使用

```
<update id="updatet" parameterType="user">
  update user
  <set>
    <if test="name != null and name != ''">
      name = #{name}
    </if>
  </set>
  where id = #{id}
</update>
```
> <bind>

字符串拼接，类似mysql中的concat

```
<if test="name != null and name != ''">
    <bind name="nameLike" value='"%"+name+"%"'/>
    AND name like #{nameLike}
</if>
```
> <trim>

一般使用很少，mybatis的trim标签一般用于去除sql语句中多余的and关键字，逗号，或者给sql语句前拼接 “where“、“set“以及“values(“ 等前缀，或者添加“)“等后缀，可用于选择性插入、更新、删除或者条件查询等操作。
<trim>有四个属性

属性<div style="width: 100pt"> | 描述
:-:|:-:
prefix | 给sql语句拼接的前缀
suffix | 给sql语句拼接的后缀
prefixOverrides | 去除sql语句前面的关键字或者字符，该关键字或者字符由prefixOverrides属性指定，假设该属性指定为"AND"，当sql语句的开头为"AND"，trim标签将会去除该"AND"
suffixOverrides | 去除sql语句后面的关键字或者字符，该关键字或者字符由suffixOverrides属性指定

```
<select id="findUser" resultType="user">
  select * from user
  <trim prefix="where" suffix="order by id" 
      prefixOverrides="and | or" suffixOverrides=",">
    <if test="name != null and name != ''">
      and name = #{name}
    </if>
    <if test="id != null">
      and id = #{id}
    </if>
  </trim>
</select>
```

- ##### 动态sql的执行原理

第一部分：解析
在启动加载解析xml配置文件的时候进行解析，根据标签使用对应的handler处理对象，
返回sqlSource对象封装在configuration里的mappedStatement中

调用流程：
1. SqlSessionFactoryBuilder对builder对象的时候，调用XMLConfigBuilder
解析sqlMapConfig.xml配置文件，在解析过程中使用
XMLConfigBuilder.parseConfiguration()，
方法里调用mapperElement(XNode parent)方法扫描package，获取所有的mapper

2. mapperElement(XNode parent)里会调用mapperParser.parse()方法，
mapperParser.parse()又调用configurationElement(XNode context)，
最终会执行buildStatementFromContext方法,
使用XMLStatementBuilder.parseStatementNode()解析mapper.xml里面的sql

3. 方法里使用SqlSource sqlSource = langDriver.createSqlSource(...)，
创建XMLScriptBuilder对象，最后调用parseScriptNode方法，
对不同标签使用不用的handler相应处理，生成DynamicSqlSource返回，
调用builderAssistant.addMappedStatement()将sqlSource
封装到configuration里的mappedStatement中

第二部分：执行
在执行过程中从configuration中获取mappedStatement，从中又获取到
对应的sqlSource，从中再获取bondSql对象，执行相应的标签handler

举例：如调用查询执行到BaseExecutor的query方法时候会调用
MappedStatement.getBoundSql方法，重新new BoundSql对象，
设置sql（就是前面sqlSource里的boundSql对象里的sql）
以及参数进行返回，最后调用StatementHandler.query进行处理

### 2. Mybatis是否支持延迟加载？如果支持，它的实现原理是什么？
[Mybatis学习总结（八）——延迟加载](http://https://www.cnblogs.com/xiaoxi/p/6640045.html)

- #### 什么是延迟加载

resultMap可以实现高级映射（使用association、collection实现一对一及一对多映射），
association、collection具备延迟加载功能。

需求：如果查询订单并且关联查询用户信息。如果先查询订单信息即可满足要求，
当我们需要查询用户信息时再查询用户信息。把对用户信息的按需去查询就是延迟加载。

延迟加载：先从单表查询、需要时再从关联表去关联查询，大大提高数据库性能，
因为查询单表要比关联查询多张表速度要快。

- #### 延迟加载的原理

它的原理是，使用CGLIB创建目标对象的代理对象，当调用目标方法时，进入拦截器方法，比如调用a.getB().getName()，拦截器invoke()方法发现a.getB()是null值，那么就会单独发送事先保存好的查询关联B对象的sql，把B查询上来，然后调用a.setB(b)，于是a的对象b属性就有值了，接着完成a.getB().getName()方法的调用。这就是延迟加载的基本原理。

### 3. Mybatis都有哪些Executor执行器？它们之间的区别是什么？

> SimpleExecutor

每执行一次update或select，就开启一个Statement对象，用完立刻关闭Statement对象

> ReuseExecutor

执行update或select，以sql作为key查找Statement对象，存在就使用，不存在就创建，用完后，不关闭Statement对象，而是放置于Map内，供下一次使用。简言之，就是重复使用Statement对象

> BatchExecutor

执行update（没有select，JDBC批处理不支持select），将所有sql都添加到批处理中（addBatch()），等待统一执行（executeBatch()），它缓存了多个Statement对象，每个Statement对象都是addBatch()完毕后，等待逐一执行executeBatch()批处理。与JDBC批处理相同

作用范围，Executor的这些特点，都严格限制在SqlSession生命周期范围内

默认是SimplExcutor，可配置，在Mybatis配置文件中，可以指定默认的ExecutorType执行器类型，也可以手动给DefaultSqlSessionFactory的创建SqlSession的方法传递ExecutorType类型参数

### 4. 简述下Mybatis的一级、二级缓存（分别从储存结构、范围、失效场景这三个方卖弄来作答）？

> 一级缓存

Mybatis的一级缓存是指SqlSession级别的，作用域是SqlSession，Mybatis默认开启一级缓存，在同一个SqlSession中，相同的Sql查询的时候，第一次查询的时候，就会从缓存中取，如果发现没有数据，那么就从数据库查询，并且缓存到HashMap中，如果下次还是相同的查询，就直接从缓存中查询，就不在去查询数据库，对应的就不在去执行SQL语句。当查询到的数据，进行增删改的操作的时候（执行commit操作），缓存将会清空失效，避免脏读。在spring容器管理中每次查询都是创建一个新的sqlSession，所以在分布式环境中不会出现数据不一致的问题。

> 二级缓存 

二级缓存是mapper级别的缓存，多个SqlSession去操作同一个mapper的sql语句，多个SqlSession可以共用二级缓存，二级缓存是跨SqlSession。第一次调用mapper下的sql 的时候去查询信息，查询到的信息会存放到该mapper对应的二级缓存区域，第二次调用namespace下的mapper映射文件中，相同的SQL去查询，会去对应的二级缓存内取结果。若执行了增删改的操作（执行commit操作），二级缓存将会清空失效，避免脏读。

二级缓存默认不开启，需手动开启，配置setting的cacheEnabled为true，使用时需开启cache标签。也可指定查询开启二级缓存，如在select上添加useCache属性为true，在更新和删除时候默认刷新缓存，即flushCache默认为true，一般执行完commit，都会清空缓存。

### 5. 简述Mybatis的插件运行原理，以及如何编写一个插件？

> mybatis插件介绍

Mybatis作为一个应用广泛的优秀的ORM开源框架,这个框架具有强大的灵活性,在四大组件(Executor, StatementHandler, ParameterHandler, ResultSetHandler)处提供了简单易用的插件扩展机制。Mybatis对持久层的操作就是借助于四大核心对象。MyBatis支持用插件对四大核心对象进行拦截,对mybatis来说插件就是拦截器,用来增强核心对象的功能,增强功能本质上是借助于底层的动态代理实现的,换句话说, MyBatis中的四大对象都是代理对象

> mybatis插件原理

在四大对象创建的时候
1. 每个创建出来的对象不是直接返回的,而是interceptorChain.pluginAll(parameterHandler);
2. 获取到所有的Interceptor (拦截器) (插件需要实现的接口) ;调用interceptor.plugin(target);返回target包装后的对象3、插件机制,我们可以使用插件为目标对象创建一个代理对象; AOP (面向切面)我们的插件可以为四大对象创建出代理对象,代理对象就可以拦截到四大对象的每一个执行;
3. 插件机制,我们可以使用插件为目标对象创建一个代理对象; AOP (面向切面)我们的插件可以为四大对象创建出代理对象,代理对象就可以拦截到四大对象的每一个执行

拦截

插件具体是如何拦截并附加额外的功能的呢?以ParameterHandler来说

```
# 在类Configuration中
public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
    ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
    parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
    return parameterHandler;
  }

# 在类InterceptorChain中
public Object pluginAll(Object target) {
    for (Interceptor interceptor : interceptors) {
      target = interceptor.plugin(target);
    }
    return target;
  }
```

interceptorChain保存了所有的拦截器(interceptors),是mybatis初始化的时候创建的。调用拦截器链中的拦截器依次的对目标进行拦截或增强。interceptor.plugin(target)中的target就可以理解为mybatis中的四大对象。返回的target是被重重代理后的对象

> 自定义插件

如果我们想要拦截Executor的query方法,那么可以这样定义插件:

- 定义插件，实现mybatis的Interceptor接口
```
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
 
import java.lang.reflect.Method;
import java.util.Properties;
 
/**
 * 注解声明mybatis当前插件拦截哪个对象的哪个方法
 * <p>
 * type表示要拦截的目标对象 Executor.class StatementHandler.class  ParameterHandler.class ResultSetHandler.class
 * method表示要拦截的方法，
 * args表示要拦截方法的参数
 *
 */
@Intercepts({
        @Signature(type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class ExamplePlugin implements Interceptor {
 
    /**
     * 拦截目标对象的目标方法执行
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 被代理对象
        Object target = invocation.getTarget();
        // 代理方法
        Method method = invocation.getMethod();
        // 方法参数
        Object[] args = invocation.getArgs();
        // do something ...... 方法拦截前执行代码块
        // 执行原来方法
        Object result = invocation.proceed();
        // do something .......方法拦截后执行代码块
        return result;
    }
 
    /**
     * 包装目标对象：为目标对象创建代理对象
     *
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {
        // this表示当前拦截器，target表示目标对象，wrap方法利用mybatis封装的方法为目标对象创建代理对象（没有拦截的对象会直接返回，不会创建代理对象）
        Object wrap = Plugin.wrap(target, this);
        return wrap;
    }
 
    /**
     * 设置插件在配置文件中配置的参数值
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        
    }
}
```
- 引入定义的插件

```
<plugins>
    <plugin interceptor="com.github.codingdebugallday.ExamplePlugin">
        <property name="name" value="value"/>
    </plugin>
</plugins>
```

这样MyBatis在启动时可以加载插件,并保存插件实例到相关对象(InterceptorChain,拦截器链)中。待准备工作做完后, MyBatis处于就绪状态。我们在执行SQL时,需要先通过DefaultSglSessionFactory创建SqlSession, Executor实例会在创建SqlSession的过程中被创建,Executor实例创建完毕后, MyBatis会通过JDK动态代理为实例生成代理类。这样,插件逻辑即可在Executor相关方法被调用前执行。

以上就是MyBatis插件机制的基本原理