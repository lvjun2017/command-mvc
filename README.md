### 1. init

* web.xml
```
<!-- Spring -->
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext.xml</param-value>
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>

<!-- core Command Servlet -->
<servlet>
    <servlet-name>CommandServlet</servlet-name>
    <servlet-class>com.wangyanrui.common.command.core.CommandServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>CommandServlet</servlet-name>
    <url-pattern>/*</url-pattern>
</servlet-mapping>
```

* applicationContext.xml

```
<bean id="commandService" class="com.wangyanrui.common.command.core.CommandService">
    <property name="handlerList">
        <list>
            <!-- 
                You business component and method must annotation @com.wangyanrui.common.command.annotation.Command
                
                @com.wangyanrui.common.command.annotation.Command has a field (`value`), 
                you can customize the mapping relationships through this property
                
                The method who annotation  @com.wangyanrui.common.command.annotation.Command must have two parameter,
                first  :  com.wangyanrui.common.command.CommandRequest
                second :  com.wangyanrui.common.command.CommandResponse
            -->
            <bean class="com.wangyanrui.demo.DemoCommand"/>
        </list>
    </property>
    <property name="filters">
        <list>
            <!-- 过滤器(implement interface: com.wangyanrui.common.command.core.CommandFilter) -->
        </list>
    </property>
</bean>
```


### 2. interactive
    
    You have to pass a parameter who named 'action(handlerKey.methodName)' by generic type or json 
    Eg. action=user.save

### 3. demo

    1. web.xml    
    2. applicationContext.xml
    3. com.wangyanrui.demo

### 4. TIP
    Current framework dependency spring-context and spring-web (5.0.4)
    You must manually exclude spring when the spring version is conflicting
