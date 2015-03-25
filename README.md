# summercool
Automatically exported from code.google.com/p/summercool

http://dragonsoar.iteye.com/blog/1454095
http://dragonsoar.iteye.com/blog/1461614
http://dragonsoar.iteye.com/blog/1461986

说明：此框架要用到spring-tools文件夹中的security文件夹中的文件，使用此框架的人员请将security文件夹的内容替换到JDK中的security文件夹中
 
 
一、web.xml配置
 
Xml代码  收藏代码
<?xml version="1.0" encoding="UTF-8"?>  
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
    xmlns="http://java.sun.com/xml/ns/javaee" xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"  
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"  
    id="WebApp_ID" version="2.5">  
    <display-name>summercool-petstore</display-name>  
    <!-- Encoding Filter -->  
    <filter>  
        <filter-name>Set Character Encoding</filter-name>  
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>  
        <init-param>  
            <param-name>encoding</param-name>  
            <param-value>UTF-8</param-value>  
        </init-param>  
    </filter>  
    <filter-mapping>  
        <filter-name>Set Character Encoding</filter-name>  
        <url-pattern>/*</url-pattern>  
    </filter-mapping>  
  
    <filter>  
        <filter-name>summercool</filter-name>  
        <filter-class>org.summercool.web.servlet.DispatcherFilter</filter-class>  
    </filter>  
    <filter-mapping>  
        <filter-name>summercool</filter-name>  
        <url-pattern>/*</url-pattern>  
    </filter-mapping>  
  
    <welcome-file-list>  
        <welcome-file>index.htm</welcome-file>  
    </welcome-file-list>  
</web-app>  
 说明：
           1. Set Character Encoding：字符编码过滤器
           2. summercool：summercool框架过滤器
           3. 只要配置上面两个filter即完成了应用框架的启动配置
 
二、summercool相关配置文件加载及说明
1. 笔者在设计框架的时候，想到了OSGI，但是一直也没有学习明白OSGI的好处或是说优势能给框架带来的什么提升。但是，笔者想实现一种不需要Spring配置文件的注入就可以动态扫描相关的配置文件，实现Spring相关功能类加载。
2. 因为上述的考虑，所以笔者用一种没有技术含又自认为比较方便的方式实现上述功能，如summercool框架会自动扫描：/summercool/spring/*.xml文件夹下面的所有的Spring配置文件。
xml配置文件加载
说明：上图中的/summercool/spring文件夹下面的Spring配置文件全部都会自动加载，包括依赖的项目或是jar里包括的/summercool/spring的文夹下面的配置文件。
3. 在petstore应用里面，笔者一共预先建立了下面几个配置文件：
    1) applicationContext.xml
    2) cache-init-config.xml
    3) cookie-configurer.xml
    4) exception-configurer.xml
    5) monitor-configurer.xml
    6) petstore-configurer.xml
    7) url-configurer.xml
    说明：在上面几个配置文件中，要想要petstore应用跑里面，上面1) applicationContext.xml 和 6) petstore-configurer.xml 这两个配置文件是不可缺少的。
               A. applicationContextml里面配置的是petstore页面模版信息和一些Spring MVC框架的配置信息。
               B. petstore-configurer.xml里面配置的是信息是summercool的一些请求映射信息等。是整个summercool框架的核心配置文件之一；下面会详细讲解。
 
三、核心配置文件讲解
1. applicationContext.xml配置文件说明
 
Xml代码  收藏代码
<?xml version="1.0" encoding="UTF-8" ?>  
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
    xmlns:context="http://www.springframework.org/schema/context" xmlns:util="http://www.springframework.org/schema/util"  
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
        http://www.springframework.org/schema/beans/spring-beans-3.0.xsd  
        http://www.springframework.org/schema/context  
        http://www.springframework.org/schema/context/spring-context-3.0.xsd  
        http://www.springframework.org/schema/util  
        http://www.springframework.org/schema/util/spring-util-3.0.xsd">  
  
    <bean id="propertyConfigurer" class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">  
        <property name="locations">  
            <list>  
                <value>classpath:config/config.properties</value>  
            </list>  
        </property>  
    </bean>  
  
    <!-- 设置 MultipartResolver -->  
    <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">  
        <property name="defaultEncoding" value="UTF-8" />  
        <property name="maxUploadSize" value="20000000" />  
        <property name="maxInMemorySize" value="30720" />  
    </bean>  
  
    <!-- 设置 HandlerExceptionResolver -->  
    <bean id="handlerExceptionResolver" class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">  
        <property name="defaultErrorView" value="/petstore/views/error" />  
        <property name="defaultStatusCode" value="500" />  
    </bean>  
    <!-- #################################### -->  
  
    <!-- 设置 ViewResolver -->  
  
    <bean name="defaultResourceLoader" class="org.springframework.core.io.DefaultResourceLoader" />  
    <bean name="springTemplateLoader" class="org.summercool.view.freemarker.FreeMarkerTemplateLoader">  
        <constructor-arg index="0" ref="defaultResourceLoader" />  
        <constructor-arg index="1" value="${org.summercool.petstore.template.templateLoaderPath}" />  
    </bean>  
  
    <bean id="freemarkerConfiguration" class="org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean">  
        <property name="templateLoaderPath" value="${org.summercool.petstore.template.templateLoaderPath}" />  
        <property name="postTemplateLoaders">  
            <array>  
                <ref bean="springTemplateLoader"></ref>  
            </array>  
        </property>  
        <property name="freemarkerSettings">  
            <props>  
                <prop key="default_encoding">UTF-8</prop>  
                <prop key="number_format">#</prop>  
                <prop key="template_update_delay">${org.summercool.petstore.template.update.delay}</prop>  
                <prop key="classic_compatible">true</prop>  
                <prop key="auto_import">/macro/macros.ftl as spring</prop>  
                <prop key="url_escaping_charset">UTF-8</prop>  
            </props>  
        </property>  
    </bean>  
  
    <bean id="freemarkerConfigurer" class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer">  
        <property name="configuration" ref="freemarkerConfiguration" />  
    </bean>  
  
    <!-- Spring MVC页面层FreeMarker的处理类 -->  
  
    <util:map id="uriModuleConstants">  
    </util:map>  
  
    <bean id="freemarkerResolver" class="org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver">  
        <property name="prefix" value="" />  
        <property name="suffix" value=".ftl" />  
        <property name="contentType" value="text/html; charset=UTF-8" />  
        <property name="attributes" ref="uriModuleConstants" />  
    </bean>  
    <!-- #################################### -->  
</beans>  
说明：
        1. propertyConfigurer：该配置是用来配置Spring注入的一些变量信息。目前里面只是有FreeMarker的模版路径和模版缓存时间。
        2. MultipartResolver：如果你的Spring MVC中需要使用到文件上传的功能，则需要配置这个类。默认Spring MVC是不支持文件上传的，只有配了这个类才会支持多文件上传；否则不起到作用。具体的用法，有时间笔者会给出例子，因为是标准的Spring MVC多文件上传，在这里不多作说明。
        3. HandlerExceptionResolver：全局错误处理器；在Spring MVC在系统发生异常的情况，会交给这个统一错误处理类来处理。比如说，上面配置中“defaultErrorView”属性可以告诉如果系统发生异常，则要显示的默认处理页面（注：支持【 redirect: 】等这样的Spring MVC框架中View对象的语法）。
            补充：笔者其实是比较喜欢这个设计的，但是在真正的使用的时候，发现有些需求的情况是无法解决的。比如说，UserException中有一个errorCode属性，不同的errorCode属性会有不同的错误异常类型；可以跟据不同的errorCode可以跳转到不同的业页等。再比如，HandlerExceptionResolver处理类无法拦截“页面层次的异常” 。所以，笔者才设计出了ExceptionPipeline接口，该接口的具体使用，笔记会在summercool-petstore应用中给出示例说明。
        4. freemarkerConfiguration：设置的是Spring MVC中freemarker页面模版的信息。在这里作者做了一点更改，支持classpath:下面的freemarker模版的加载，别的没有做任何的变动。具体的freemarker模版设置网上有很多的资类可以参数，笔都在这里不多做介绍了。
            补充：为什么要做一个freemarker的classpath下面的模版加载类呢，是因为有时候在不同的jar包或是项目中，支持把模版文件以工程模块划分，所以支持放在classpath来解决这个需求。
                     笔者认为上面的这些配置信息已经足够，一般不需要更多的配置了。具本上面每个配置的含义，大家可以网上查查，笔者也会有时间会在summercool-petstore应用中补充进去的。
        5. uriModuleConstants：这个配置比较关键，是配置freemarker页面中的全局参数的一个map。比如说，我想让freemarker页面加入一个全局变量"AppName : Petstore"，则可以配置在此map中。使用的时候只要在freemarker的任何页面中使用${AppName}即可显示。同理，如果想在freemarker中加入全局的内置函数，当然也是通过此方法实现。
        6. freemarkerResolver：Spring MVC中真正使用的freemarker页面处理类，具体的实现和使用其实全部都是通过此类完成，具体该类的细节笔者在此不做细的讲解。
 
2. petstore-module.xml配置文件说明
 
Xml代码  收藏代码
<?xml version="1.0" encoding="UTF-8" ?>  
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
    xmlns:context="http://www.springframework.org/schema/context" xmlns:util="http://www.springframework.org/schema/util"  
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
        http://www.springframework.org/schema/beans/spring-beans-3.0.xsd  
        http://www.springframework.org/schema/context  
        http://www.springframework.org/schema/context/spring-context-3.0.xsd  
        http://www.springframework.org/schema/util  
        http://www.springframework.org/schema/util/spring-util-3.0.xsd">  
  
    <bean name="petstore" class="org.summercool.web.module.WebModuleConfigurer">  
        <property name="moduleName" value="petstore" />  
        <property name="uriExtension" value=".htm" />  
        <property name="moduleBasePackage" value="org.summercool.platform.web.module" />  
        <property name="context" value="/" />  
        <property name="contextPackage" value="/petstore/" />  
    </bean>  
      
    <bean class="org.summercool.web.module.WebModuleUriExtensionConfigurer">  
        <property name="uriExtensions">  
            <util:list>  
                <value>.htm</value>  
            </util:list>  
        </property>  
    </bean>  
      
<!-- 
    <import resource="classpath*:spring/petstore-service.xml"/> 
-->  
</beans>  
 
说明：
        1. 该类是笔者整个summercool框架最核心的配置了。
        2. org.summercool.web.module.WebModuleConfigurer：summercool框架会自动加载此类并且分析该类加载的一些配置信息。
            在此应用中，有如下几个信息是非常重要的：
            1) moduleName：设置当前应用模块的名称（没有关键作用，其实框架也用不到，只是标识一下而已）
            2) moduleBasePackage：该模块包的根路径，主要是扫描Spring MVC的Controller和Widget类，这两个类的具体使用，笔者会在下一篇中做详细的讲解，这里只介绍一下基本规则。
            3) uriExtension： summercool框架扫描到该模块的Controller之后，生成的UrlMapping时的映射地址使用的扩展名是什么。比如说：/IndexController.java --> /index.htm 或 /user/ModifyUserController.java --> /user/modify_user.htm 这样的url对应关系。【驼峰命名法的类，对应使用下划线的URL方式】
            4) context：对应该模块的上下文。比如：context = "/" --> /index.htm 或 context = "petstore" --> /petstore/index.htm
            5) contextPackage：扫描该模块对应的模块目录。
                moduleBasePackage + contextPackage = 完整的该应用模块的招扫描路径
                扫描该完整包路径下面的：controllers和widgets文件夹
                controllers文件夹的扫描规则是：/controllers/IndexController.java --> /context/index.htm
                如果是按照上面的这个配置，则真实的规则是：/controllers/IndexController.java --> /index.htm （因为context = "/"）
            6) 模块配置类可以允许配置多个，也就是说可以配置多个应和模块，只要context不冲突即可。
        3. org.summercool.web.module.WebModuleUriExtensionConfigurer：summercool应用只允许有一个该配置类。该配置类主要是配置哪些过来的后缀名的url请求会允许交给summercool应用处理。
 
重要：到目前为止，所有的配置即已经完成了，可以运行应用。

=============

一、summercool-petstore应用结构
        应用结构
        1. petstore-module.xml配置文件说明
Java代码  收藏代码
<bean name="petstore" class="org.summercool.web.module.WebModuleConfigurer">  
    <property name="moduleName" value="petstore" />  
    <property name="uriExtension" value=".htm" />  
    <property name="moduleBasePackage" value="org.summercool.platform.web.module" />  
    <property name="context" value="/" />  
    <property name="contextPackage" value="/petstore/" />  
</bean>  
  
<bean class="org.summercool.web.module.WebModuleUriExtensionConfigurer">  
    <property name="uriExtensions">  
        <util:list>  
            <value>.htm</value>  
        </util:list>  
    </property>  
</bean>  
        说明：1) 该配置项的详细说明，笔者已经在上一篇进行了详细说明，地址是：http://dragonsoar.iteye.com/blog/1454095
                 2) 现在笔者就拿summercool-petstore应用来进行详细的说明：
A. 应用模块扫描
    moduleBasePackage + contextPackage = 完整的该应用模块的招扫描路径
       扫描该完整包路径下面的：controllers和widgets文件夹
      比如说：summercool-petstore应用下面的controllers的文件夹下面的IndexController.java --> /index.htm
规则如下：
    1. moduleBasePackage + contextPackage =  org.summercool.platform.web.module.petstore
    2. context = /
    3. org.summercool.platform.web.module.petstore.controllers = /
    4. uriExtension = .htm
所以：
    1. org.summercool.platform.web.module.petstore.controllers.IndexController.java = /index.htm
   该问应用地址：http://127.0.0.1:8080 或 http://127.0.0.1:8080/index.htm
 
B. 应用处理的请求
   1. org.summercool.web.module.WebModuleUriExtensionConfigurer --> list --> .htm
   2. 上面这个配置中，配置的是只有url地址扩展名为.htm的才会交给summercool框架处理
   3. 如，我们要是访问： http://127.0.0.1:8080/index.php
 

 
 
二、summercool-petstore的Controller和Widget开发
        1. Controller开发
            1) Controller的扫描规则是： /IndexController.java --> /index.htm
            2) 扫描的附件条件是，被扫描加载的Controller类，必须是实现Sprng MVC的标准Controller接口和类名必须是以Controller结尾。
        2. 笔者拿/LoginController.java举例
 
Java代码  收藏代码
package org.summercool.platform.web.module.petstore.controllers;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.springframework.web.servlet.ModelAndView;  
import org.springframework.web.servlet.mvc.AbstractController;  
  
public class IndexController extends AbstractController {  
  
    @Override  
    protected ModelAndView handleRequestInternal(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {  
        return new ModelAndView("/petstore/views/index");  
    }  
  
}  
             1) 因为IndexController继承自AbstractController，而该类实现了Controller接口，所以可以被扫描进来
             2) ModelAndView接口不细说了，因为AbstractController是Spring MVC的标准类，所以不做详细介绍了。
             说明：笔者强烈建议应用的请求，如果只是页面展示不涉及到表单提交的请继承AbstractController，AbstractController的处理流程图如下：
 
        3. 笔者再拿/LoginController.java进行表单提交的举例
 
Java代码  收藏代码
package org.summercool.platform.web.module.petstore.controllers;  
  
import javax.servlet.http.HttpServletRequest;  
  
import org.apache.commons.lang.StringUtils;  
import org.springframework.validation.BindException;  
import org.springframework.web.servlet.ModelAndView;  
import org.springframework.web.servlet.mvc.SimpleFormController;  
import org.summercool.platform.web.module.petstore.config.cookie.CookieUtils;  
import org.summercool.platform.web.module.petstore.config.cookie.UserDO;  
import org.summercool.platform.web.module.petstore.formbean.LoginFormBean;  
  
@SuppressWarnings("deprecation")  
public class LoginController extends SimpleFormController {  
  
    public LoginController() {  
        setBindOnNewForm(true);  
        setCommandName("loginFormBean");  
        setCommandClass(LoginFormBean.class);  
    }  
  
    @Override  
    protected void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception {  
        LoginFormBean loginFormBean = (LoginFormBean) command;  
        loginFormBean.setUserName("请输入用户名和密码");loginFormBean.setPassword("");  
    }  
  
    @Override  
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {  
        LoginFormBean loginFormBean = (LoginFormBean) command;  
        //  
        if (StringUtils.isEmpty(loginFormBean.getUserName()) || StringUtils.isEmpty(loginFormBean.getPassword())) {  
            errors.reject("-1", "用户名或密码不能为空");  
            return;  
        }  
        if ("admin".equals(loginFormBean.getUserName()) && "111111".equals(loginFormBean.getPassword())) {  
            UserDO userInfo = new UserDO();  
            userInfo.setId(0L);  
            userInfo.setUserName("admin");  
            userInfo.setPassword("111111");  
  
            CookieUtils.clearCookie(request);  
            CookieUtils.writeCookie(request, userInfo);  
        } else {  
            errors.reject("-2", "用户名或密码错误");  
        }  
    }  
  
    @Override  
    protected ModelAndView onSubmit(Object command) throws Exception {  
        return new ModelAndView("redirect:/index.htm");  
    }  
  
}  
            1) 笔者认为SimpleFormController是Spring MVC中最经典的一个Controller接口的抽象实现。为什么呢？因为SimpleFormController考虑到了很多来应对各种复杂的场景，下面笔者就一一的做出说明。
            2) 比如说，有一种应用场景；在用户登录时要给出一句提示语，如：“请输入用户名和密码”等这样的字眼显示在form表单中的userName的输入字段，但是在password字段则每次登录页面都必须要清空，不能让浏览器记录我们的登录用户名和密码。
                那么，LoginController中下面代码片段可以实现上面的需求：
 
Java代码  收藏代码
       public LoginController() {  
    setBindOnNewForm(true);  
    setCommandName("loginFormBean");  
    setCommandClass(LoginFormBean.class);  
}  
  
@Override  
protected void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception {  
    LoginFormBean loginFormBean = (LoginFormBean) command;  
    loginFormBean.setUserName("请输入用户名和密码");loginFormBean.setPassword("");  
}  
 
Html代码  收藏代码
<!DOCTYPE html>  
<html lang="zh">  
  <head>  
    <meta charset="utf-8">  
    <title>Summercool, Petstore</title>  
    <meta name="viewport" content="width=device-width, initial-scale=1.0">  
    <meta name="description" content="">  
    <meta name="author" content="">  
  
    <!-- Le styles -->  
    <link href="/css/bootstrap.css" rel="stylesheet">  
    <style type="text/css">  
      body {  
        padding-top: 60px;  
        padding-bottom: 40px;  
      }  
      #tb td ,#tb th{  
        border-top: 0px;  
      }  
    </style>  
    <link href="/css/bootstrap-responsive.css" rel="stylesheet">  
  
    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->  
    <!--[if lt IE 9]>  
      <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>  
    <![endif]-->  
  
  </head>  
  
  <body>  
    ${widget("/petstore/widgets/header")}  
  
    <div class="container">  
      
        <form method="post">  
        <fieldset>  
        <@spring.bind "loginFormBean.*"/>  
      
            <div class="input">  
                用户: <input type="text" size="30" name="userName" id="userName" class="medium" value="${(loginFormBean.userName)!}">  
            </div>  
      
            &nbsp;  
      
            <div class="input">  
                密码: <input type="password" size="30" name="password" id="password" class="medium" value="${(loginFormBean.password)!}">  
            </div>  
              
            &nbsp;  
              
            <#if (status.error)!>  
            <#list status.errors.allErrors as error>  
                <div class="alert alert-error">  
                    ${error.defaultMessage}  
                </div>  
            </#list>  
            </#if>  
      
            <div class="actions">  
                <input type="submit" value="提交" class="btn">&nbsp;<button class="btn" type="reset">取消</button>  
            </div>  
      
        </fieldset>  
        </form>  
  
      <hr>  
      ${widget("/petstore/widgets/footer")}  
  
    </div>  
  
  </body>  
</html>  
                说明：setBindOnNewForm(true); 代表，当进入且第一次页面请求显示Login表单时，是否执行onBindOnNewForm()函数。
                         什么是当进入且第一次页面请求显示Login表单呢？意思就是说，当请求一个Login表单时，那么这就是一次请求，如果在请求/login.htm时的请求是get请求，那么Spring MVC会认为这是进入且第一次页面请求。反则，如果已经显示此页面，然后提交form表单，那么这是一次post请求，那么Spring MVC会认为这不是第一次页面请求，则不会在重新返回显示页面的时候再执行onBindOnNewForm()函数了。
                         在/index.ftl的页面模版中，我们可以看出，每次"userName"和"password"输入框都是通过“CommandObject”对象的值拿出来的，所以就可以做到每次第一次进入登录页面的时候，都给出指定的提示信息和密码为空的设置了。
            3) 上面提到了会在Form表单提交后还会返回/login.htm页面，那么会在什么情况下返回到/login.htm页面呢？那当然是在提表交单发生错误的情况下了。笔者认为SimpleFormController在处理错误处理这块也非常的经典；如：在Form 表单提交的时，SimpleFromController会先执行onBindAndValidate()函数，如果该函数在校验输入参数的时候发生异常，则使用errors对象添加错误信息；当该函数返回后，Spring MVC会判断errors对象是否为空，如果为空，则执行onSubmit()函数，如果不为空，则返回到/index.htm页面。
                 SimpleFormController笔者认为真的是非常经典，里面还有两个函数需要介绍：refferenceData()和showForm()函数。
                 refferenceData()：当每次显示form表单页面的时候都执行此函数
                 showForm()：当onSubmit()函数执行完成后想继续返回到Form表单的提交页面，而直接执行此函数即可返回到Form表单的提交页面。
 
SimpleFormController处理流程图如下：

 
        4. Widget函数讲解
            1) 首先，我们还是先看一下/login.ftl页面模版文件，如下：
 
Html代码  收藏代码
<!DOCTYPE html>  
<html lang="zh">  
  <head>  
    <meta charset="utf-8">  
    <title>Summercool, Petstore</title>  
    <meta name="viewport" content="width=device-width, initial-scale=1.0">  
    <meta name="description" content="">  
    <meta name="author" content="">  
  
    <!-- Le styles -->  
    <link href="/css/bootstrap.css" rel="stylesheet">  
    <style type="text/css">  
      body {  
        padding-top: 60px;  
        padding-bottom: 40px;  
      }  
      #tb td ,#tb th{  
        border-top: 0px;  
      }  
    </style>  
    <link href="/css/bootstrap-responsive.css" rel="stylesheet">  
  
    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->  
    <!--[if lt IE 9]>  
      <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>  
    <![endif]-->  
  
  </head>  
  
  <body>  
    ${widget("/petstore/widgets/header")}  
  
    <div class="container">  
      
        <form method="post">  
        <fieldset>  
        <@spring.bind "loginFormBean.*"/>  
      
            <div class="input">  
                用户: <input type="text" size="30" name="userName" id="userName" class="medium" value="${(loginFormBean.userName)!}">  
            </div>  
      
            &nbsp;  
      
            <div class="input">  
                密码: <input type="password" size="30" name="password" id="password" class="medium" value="${(loginFormBean.password)!}">  
            </div>  
              
            &nbsp;  
              
            <#if (status.error)!>  
            <#list status.errors.allErrors as error>  
                <div class="alert alert-error">  
                    ${error.defaultMessage}  
                </div>  
            </#list>  
            </#if>  
      
            <div class="actions">  
                <input type="submit" value="提交" class="btn">&nbsp;<button class="btn" type="reset">取消</button>  
            </div>  
      
        </fieldset>  
        </form>  
  
      <hr>  
      ${widget("/petstore/widgets/footer")}  
  
    </div>  
  
  </body>  
</html>  
            2) 上面有两个${widget()}的freemarker函数笔者请各位码农注意一下：
 
Java代码  收藏代码
${widget("/petstore/widgets/header")}  
${widget("/petstore/widgets/footer")}  
                 说明：通过查看大家可能也知道这两个函数具体是什么意思了；对！是加载局部的ftl页面模版。那么，码农们可能又要怀疑了，freemarker不是有<#include>标签吗？为什么还要再弄一个${widget()}函数呢，是不是多此一举了。答案当然是否定的；因为显示一个局部页面的时候，我们肯定还要在局部页面里面加一些业务逻辑，而<#include>标签是无法做到的，而${widget()}函数则可以。
                 规则：${widget("/petstore/widgets/header")} --先加载--> /petstore/widgets/HeaderWidget.java --再加载--> /petstore/widgets/header.ftl （HeaderWidget.java可选，即如果没有Widget类，则直接加载header.ftl页面）
                          而我们想在显示"header.ftl"局部页面的时候，我们可以将一部分显示的业务逻辑和"header.ftl"局部页面需要显示的页面数据通过此类加载；那么我们查看下"HeaderWidget.java"类，如下：
 
Java代码  收藏代码
package org.summercool.platform.web.module.petstore.widgets;  
  
import java.util.Map;  
  
import javax.servlet.http.HttpServletRequest;  
  
import org.summercool.platform.web.module.petstore.config.cookie.CookieUtils;  
import org.summercool.web.servlet.view.freemarker.FreeMarkerWidget;  
  
public class HeaderWidget implements FreeMarkerWidget {  
  
    public void referenceData(HttpServletRequest request, Map<String, Object> model) {  
        model.put("userName", CookieUtils.getUserName(request));  
    }  
  
}  
                    说明：上面的代码中可以看出，"header.ftl"页面中会显示登录人的用户名信息，那么用户名的信息则就是通过referenceData()函数加载的，model是"header.ftl"的页面变量信息map。
                    
                    ===
                    
                    在介绍summercool框架的几个点的时候，在这里我们再看一下summercool的框架，如下：
                    一、Pipeline介绍
 
    1. AroundPipeline
 
Java代码  收藏代码
package org.summercool.web.servlet.pipeline;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.springframework.core.PriorityOrdered;  
import org.summercool.web.servlet.AroundPipelineChain;  
  
/** 
 *  
 * @author:shaochuan.wangsc 
 * @date:2010-3-10 
 * 
 */  
public interface AroundPipeline extends PriorityOrdered {  
  
    /** 
     *  
     * @author:shaochuan.wangsc 
     * @date:2010-3-10 
     * @param request 
     * @param response 
     * @param aroundPipelineChain 
     * @throws Exception 
     */  
    public void handleAroundInternal(HttpServletRequest request, HttpServletResponse response,  
            AroundPipelineChain aroundPipelineChain) throws Exception;  
  
}  
        说明： 1) 通过总的summercool框架图我们可以看出，AroundPipeline相当于Filter的功能，其实笔者也是这么设计的
                  2) 那有人问为什么这么设计呢，因为之前笔者的summercool是基于servlet的，后来才改成基于filter的；所以这块也就保留了下来，而且笔者也想把整个summercool的框架内部的一些设计接口完整化，所以也没有想去掉。还有一个原因是，因为summercool框架对Response进行了缓冲。
                  3) AroundPipeline会拦截所有的request请求，所以我们就可以做很多东西在这个层面上，比如说，我们可以做一个web应用的请求监控，监控每秒钟应用请求量，如下：
    打开monitor-configurer.xml里面的【RequestMonitor】类的源码：
 
Java代码  收藏代码
package org.summercool.platform.web.module.petstore.config.monitor;  
  
import java.text.SimpleDateFormat;  
import java.util.Date;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
import org.springframework.web.util.UrlPathHelper;  
import org.summercool.platform.utils.NumberStatisticUtil;  
import org.summercool.web.servlet.AroundPipelineChain;  
import org.summercool.web.servlet.pipeline.AroundPipeline;  
  
public class RequestMonitor implements AroundPipeline {  
  
    private static final String N_CHAR = "/";  
      
    private final Logger logger = LoggerFactory.getLogger(RequestMonitor.class);  
  
    private int order;  
  
    private UrlPathHelper urlPathHelper = new UrlPathHelper();  
      
    private NumberStatisticUtil numberStatisticUtil = new NumberStatisticUtil();  
  
    public RequestMonitor() {  
        numberStatisticUtil.setInterval(N_CHAR, 1000L);  
        urlPathHelper.setUrlDecode(false);  
    }  
  
    public void handleAroundInternal(HttpServletRequest request, HttpServletResponse response,  
            AroundPipelineChain aroundPipelineChain) throws Exception {  
        //  
        if (!logger.isDebugEnabled()) {  
            aroundPipelineChain.handleAroundInternal(request, response, aroundPipelineChain);  
            return;  
        }  
  
        //  
        long beginTime = System.currentTimeMillis();  
        //  
        numberStatisticUtil.incrementAndGet(N_CHAR);  
        long threadCount = numberStatisticUtil.getValue(N_CHAR);  
        //  
        try {  
            aroundPipelineChain.handleAroundInternal(request, response, aroundPipelineChain);  
        } finally {  
            if (logger.isInfoEnabled()) {  
                String url = urlPathHelper.getLookupPathForRequest(request);  
                long endTime = System.currentTimeMillis();  
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
                String message = "当前Dispatcher[" + request.getRemoteAddr() + "-->" + url + "]"  
                        + "当执行的时间为:[" + sdf.format(new Date(beginTime)) + "], "   
                        + "当前时间内(秒)执行线程数为:[" + threadCount + "], "  
                        + "请求执行的时间为:[" + (endTime - beginTime) + "毫秒]";  
                logger.info(message);  
            }  
        }  
    }  
      
    public void setOrder(int order) {  
        this.order = order;  
    }  
  
    public int getOrder() {  
        return order;  
    }  
}  
    A. 启动应用，请求应用页面，会打印出如下信息：INFO  o.s.p.w.m.p.c.monitor.RequestMonitor - 当前Dispatcher[127.0.0.1-->/images/tb_pet_2.jpg]当执行的时间为:[2012-03-26 12:08:38], 当前时间内(秒)执行线程数为:[24], 请求执行的时间为:[0毫秒]
    B. 上面的信息的含义是：127.0.0.1这个应用，每秒钟的请求量；如上面的一秒种的请求量为24次，最后一次请求的是“/images/tb_pet_2.jpg”资源（因为summercool是基于filter的，所以静态信息的请求也会被拦截）。
    C. 其实NumberStatisticUtil类是并发安全的记数工具类，是以"key : value"Map结构的工具类，可以跟据时间设置有效期的时间段。
 
    2. PreProcessPipeline
 
Java代码  收藏代码
package org.summercool.web.servlet.pipeline;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.springframework.core.PriorityOrdered;  
import org.springframework.web.servlet.ModelAndView;  
  
/** 
 *  
 * @author:shaochuan.wangsc 
 * @date:2010-3-10 
 *  
 */  
public interface PreProcessPipeline extends PriorityOrdered {  
  
    /** 
     *  
     * @author:shaochuan.wangsc 
     * @date:2010-3-10 
     * @param request 
     * @param response 
     * @return 
     * @throws Exception 
     */  
    public boolean isPermitted(HttpServletRequest request, HttpServletResponse response) throws Exception;  
  
    /** 
     *  
     * @author:shaochuan.wangsc 
     * @date:2010-3-10 
     * @param request 
     * @param response 
     * @return 
     * @throws Exception 
     */  
    public ModelAndView handleProcessInternal(HttpServletRequest request, HttpServletResponse response)  
            throws Exception;  
  
}  
 
    petstore-module.xml
 
Xml代码  收藏代码
<bean class="org.summercool.web.module.WebModuleUriExtensionConfigurer">  
    <property name="uriExtensions">  
        <util:list>  
            <value>.htm</value>  
        </util:list>  
    </property>  
</bean>  
 
    说明：1) 为什么要将上面两段代码都要贴出来的呢？是因为，summercool是基于是filter的，所以会过滤所有的请求并进行处理。但是，有些请求是不需要交给后台处理类处理的，所以需要配置一个规则，要处理哪些请求。
             2) "WebModuleUriExtensionConfigurer"就是配置一个请求过滤规则的扩展名辅助类。
             3) 流程是： requst --> AroundPipeline --> --[请求url扩展名过滤] --> PreProcessPipeline
                              如果在处理[请求url扩展名过滤]时，如果扩展名匹配上则会继续交给PreProcessPipeline处理并继续向下执行；如果在处理[请求url扩展名过滤]时扩展规则时没有匹配上，则会直接返回。
 
     应用举例：
     1. 比如说，我们现在有这样的一个需求：所有访问summercool-petstore的应用，在未登录的时候只允许访问/index.htm、/login.htm和logout.htm请求页面，而其他的页面则必须只有登录用户才可以该访问，如：/item/{id}.htm
     2. 上面的需求已经说的非常清楚了，但是我们在哪里判断用户是否登录才好呢？当然，答案就是用PreProcessPipeline
     3. 我们先写一个LoginSecurity.java处理类，代码如下：
 
Java代码  收藏代码
package org.summercool.platform.web.module.petstore.config.security;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.springframework.web.servlet.ModelAndView;  
import org.summercool.platform.web.module.petstore.config.cookie.CookieUtils;  
import org.summercool.web.servlet.pipeline.PreProcessPipeline;  
  
/** 
 * @Title: LoginSecurity.java 
 * @Package com.gexin.platform.web.module.manager.config.security 
 * @Description: 
 * @author 简道 
 * @date 2011-11-24 下午1:29:27 
 * @version V1.0 
 */  
public class LoginSecurity extends AbstractSecurity implements PreProcessPipeline {  
  
    private int order;  
  
    // set 方法  
    public void setOrder(int order) {  
        this.order = order;  
    }  
  
    public ModelAndView handleProcessInternal(HttpServletRequest request, HttpServletResponse response)  
            throws Exception {  
        return new ModelAndView("redirect:/" + "login.htm");  
    }  
  
    public boolean isPermitted(HttpServletRequest request, HttpServletResponse response) throws Exception {  
  
        if (match(request)) {  
            return true;  
        } else {  
            if (CookieUtils.isLogin(request)) {  
                return true;  
            } else {  
                return false;  
            }  
        }  
    }  
  
    public int getOrder() {  
        return order;  
    }  
  
}  
 
   security-configurer.xml
 
Xml代码  收藏代码
<bean name="loginSecurity" class="org.summercool.platform.web.module.petstore.config.security.LoginSecurity">  
    <property name="order" value="1" />  
    <property name="filterPaths">  
        <util:list>  
            <value>/</value>  
            <value>/index.htm</value>  
            <value>/login.htm</value>  
            <value>/logout.htm</value>  
            <value>/helper.htm</value>  
        </util:list>  
    </property>  
</bean>  
 
    说明：首先，在Spring的配置里面配置一下面PreProcessPipeline的实现类，summercool框架会自动将其扫描到容器中并加载成单例的。
             再次，所有的请求都会按PreProcessPipeline的序列进行请求处理，比如说summercool-petstore应用里面只有LoginSecurity权限处理类。
             然后，request ---> PreProcessPipeline  ( isPermitted() -- [true|false] --> handleProcessInternal() )
                      如果isPermitted()函数返回为true，则不会执行handleProcessInternal()函数，请求会继续交给下一个PreProcessPipeline或是Controller继续向下面执行
                      如果isPermitted()函数返回为false，则会执行handleProcessInternal()函数，并返回ModelAndView接口并直接返回，不再继续交给下面的请求处理类，而是直接处理ModelAndView接口直接返回给客户端。
             在上面的这个LoginSecurity处理类中，所有的请求都经过isPermitted()函数判断用户是否登录，如果登录则返回true；如果不登录则返回false，然后执行handleProcessInternal()函数，handleProcessInternal()函数将用户重定向到登录页面。
             在LoginSecurity处理类中，用户是否登录是通过Cookies进行判断的。而不需要权限过滤的请求url是通过AbstractSecurity类里面的match()函数实现，通过xml配置完成。
 
    3. PostProcessPipeline
 
Java代码  收藏代码
package org.summercool.web.servlet.pipeline;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.springframework.core.PriorityOrdered;  
import org.springframework.web.servlet.ModelAndView;  
  
/** 
 *  
 * @author:shaochuan.wangsc 
 * @date:2010-3-10 
 *  
 */  
public interface PostProcessPipeline extends PriorityOrdered {  
  
    /** 
     *  
     * @author:shaochuan.wangsc 
     * @date:2010-3-10 
     * @param request 
     * @param response 
     * @param modelAndView 
     * @return 
     * @throws Exception 
     */  
    public boolean isPermitted(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView)  
            throws Exception;  
  
    public ModelAndView handleProcessInternal(HttpServletRequest request, HttpServletResponse response)  
            throws Exception;  
  
}  
    说明：1) PostProcessPipeline其实只是在isPermitted()函数中，多了一个ModelAndView对象；其余的用法和PreProcessPipeline是一样的。
             2) 在Controller请求处理完成之后，在渲染页面之前会执行PostProcessPipeline这个接口的实现类。
             3) 为什么要多一个ModelAndView接口呢？因为，我们在这里可以做很多的文章，如这样的一个需求：
    我们为了web应用的安全考虑，我们肯定希望在一个请求在处理外部重定向的时候，我们需要肯定一些特定的参数进行重定向到一个页面。如：淘宝应用的在访问“已买到的宝贝”的时候，需要登录才能查看，所以在重定向到登录页面的时候，url后面会带一个redirectURL参数来让用户登录后继续回去上一次访问的页面。那么，这个时候问题来了！
    A. redirectURL后面是一个安全的淘宝内部的url地址，那么在应用登录后重定向到goto指定的地址是没有问题的。
    B. 如果要是被黑客给黑了，redirectURL的地址是一个不安全的地址，那么要怎么处理呢？
    C. 我们是否有一个统一的办法来处理这样的情况呢？
    D. 当然是PostProcessPipeline了；所有的Controller执行完成后，在跳转到页面之前都会执行PostProcessPipeline，所以我们可以通过ModelAndView参数来查看是否View中的地址是否在我们应用允许跳转的白名单之中，如果允许则通过；如果不允许则做另一处理。（在summercool-petstore应用中，笔者没有给出这个接口的实现）
 
    4. ExceptionPipeline
 
Java代码  收藏代码
package org.summercool.web.servlet.pipeline;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.springframework.core.PriorityOrdered;  
import org.springframework.web.servlet.ModelAndView;  
  
/** 
 *  
 * @author:shaochuan.wangsc 
 * @date:2010-3-10 
 *  
 */  
public interface ExceptionPipeline extends PriorityOrdered {  
  
    /** 
     *  
     * @author:shaochuan.wangsc 
     * @date:2010-3-10 
     * @param request 
     * @param response 
     * @param modelAndView 
     * @param throwable 
     * @throws Exception 
     */  
    public void handleExceptionInternal(HttpServletRequest request, HttpServletResponse response,  
            ModelAndView modelAndView, Throwable throwable) throws Exception;  
}  
 
    说明：Spring MVC在处理Controller抛出的错误信息的时候可以拦截，交给ExceptionResolver处理，但是如果vm、jsp和ftl这样的页面出错的时候，Spring MVC的ExceptionResolver是无法处理的；直接抛出给Response并且示给客户端。
             Spring MVC只能定制在异常信息类型进行统一错误信息进行处理，如： UserException --> /user/error
             而我们经常会遇到跟据UserException中的某一属性，如errorCode属性的不同值来渲染不出的页面。
             如果我们有上面的需求，那么我们怎么处理呢？那么我们就要用ExceptionPipeline了，因为该类会拦截所有的异常信息，并且交给handExceptionInternal()函数处理；
expception-configurer.xml里面有一个默认的ExceptionPipeline实现，如下：
 
Java代码  收藏代码
package org.summercool.platform.web.module.petstore.config.exception;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
import org.springframework.web.servlet.ModelAndView;  
import org.summercool.util.StackTraceUtil;  
import org.summercool.web.servlet.pipeline.ExceptionPipeline;  
  
public class DefaultExceptionHandler implements ExceptionPipeline {  
    private Logger logger = LoggerFactory.getLogger(getClass());  
    private int order;  
  
    public void handleExceptionInternal(HttpServletRequest request, HttpServletResponse response,  
            ModelAndView mv, Throwable throwable) throws Exception {  
        // 打印错误信息  
        String stackTrace = StackTraceUtil.getStackTrace(throwable);  
        // 记录错误信息  
        logger.error(stackTrace);  
  
        if (mv != null) {  
            mv.setViewName("redirect:/index.htm");  
        } else {  
            throwable.printStackTrace();  
        }  
    }  
  
    public void setOrder(int order) {  
        this.order = order;  
    }  
  
    public int getOrder() {  
        return order;  
    }  
  
}  
    说明：上面的代码的意思是，如果发生异常；handleExceptionInternal()函数会先获取异常信息并打印到日志中，然后通过ModelAndView对象来跟据业务的需要来自定义要跳转的页面。
              上面的代码就是如果发生任何的异常，则跳转到/index.htm页面
 
 
二、summercool框架对Cookies封装
 
    1. cookie-configurer.xml
 
Xml代码  收藏代码
<?xml version="1.0" encoding="UTF-8" ?>  
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
    xmlns:context="http://www.springframework.org/schema/context" xmlns:util="http://www.springframework.org/schema/util"  
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
        http://www.springframework.org/schema/beans/spring-beans-3.0.xsd  
        http://www.springframework.org/schema/context  
        http://www.springframework.org/schema/context/spring-context-3.0.xsd   
        http://www.springframework.org/schema/util  
        http://www.springframework.org/schema/util/spring-util-3.0.xsd">  
  
    <bean name="cookieConfigurer" class="org.summercool.web.module.CookieModuleConfigurer">  
        <property name="cryptoKey" value="^#16qweqv8cde729!@#$3450abfg^%" />  
        <property name="cookieConfigurerList">  
            <util:list>  
                <bean name="id" class="org.summercool.web.beans.cookie.CookieConfigurer">  
                    <property name="domain" value="${org.summercool.petstore.domain}" />  
                    <property name="lifeTime" value="-1" />  
                    <property name="name" value="id" />  
                    <property name="clientName" value="__i__" />  
                    <property name="encrypted" value="true" />  
                </bean>  
  
                <bean name="uname" class="org.summercool.web.beans.cookie.CookieConfigurer">  
                    <property name="domain" value="${org.summercool.petstore.domain}" />  
                    <property name="lifeTime" value="-1" />  
                    <property name="name" value="uname" />  
                    <property name="clientName" value="__uli__" />  
                    <property name="encrypted" value="true" />  
                </bean>  
  
                <bean name="password" class="org.summercool.web.beans.cookie.CookieConfigurer">  
                    <property name="domain" value="${org.summercool.petstore.domain}" />  
                    <property name="lifeTime" value="-1" />  
                    <property name="name" value="password" />  
                    <property name="clientName" value="__up__" />  
                    <property name="encrypted" value="true" />  
                </bean>  
  
                <bean name="csrf" class="org.summercool.web.beans.cookie.CookieConfigurer">  
                    <property name="domain" value="${org.summercool.petstore.domain}" />  
                    <property name="lifeTime" value="-1" />  
                    <property name="name" value="csrf" />  
                    <property name="clientName" value="__rf__" />  
                    <property name="encrypted" value="true" />  
                </bean>  
            </util:list>  
        </property>  
    </bean>  
</beans>  
 
在上面这个配置文件中，最重要的是CookieConfigurer类配置，如下：
 
Xml代码  收藏代码
<bean name="id" class="org.summercool.web.beans.cookie.CookieConfigurer">  
    <property name="domain" value="${org.summercool.petstore.domain}" />  
    <property name="lifeTime" value="-1" />  
    <property name="name" value="id" />  
    <property name="clientName" value="__i__" />  
    <property name="encrypted" value="true" />  
</bean>  
    说明：1) domain: cookie存放在的域名（笔者建议设置为一级域名，这样二级域名的应用也可以获取和共享一级域名的cookie）
             2) lifeTime：设置cookie的有效期
             3) name：服务端的cookie名称，因为开发者在使用时，要知道设置了哪个cookie
             4) clientName：相对于服务端的cookie名称，这个是客户端的cookie名称。
                 比如说吧，我们设置了一个cookie的值，是不想让客户端的用户可以通过工具查看到我们设置的cookie名称能猜出我们的含义的，那么我们就要设置clientName，让用户端看到的是不知道何意义的cookie名称，而在服务端，开发人员又可以明确知道cookie的名称name。
             5) encrypted：是否进行加密处理；设置了此属性，CookieConfigurer类会自动对该cookie进行加密和解密。
 
 
Xml代码  收藏代码
<property name="cryptoKey" value="^#16qweqv8cde729!@#$3450abfg^%" />  
    xml中上面这段的配置就是加密混淆串，笔者建议不用的开发者如何使用summercool框架的时候，请更改此加密混淆串。
 
应用举例：CookiesUtils.java
 
Java代码  收藏代码
public static void writeCookie(HttpServletRequest request, UserDO userDO) {  
        if (request == null || userDO == null) {  
            throw new IllegalArgumentException();  
        }  
  
        CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);  
  
        if (jar == null) {  
            throw new NullPointerException();  
        }  
  
        jar.remove(CookieConstants.MANAGER_ID_COOKIE);  
        jar.remove(CookieConstants.MANAGER_PWD_COOKIE);  
        jar.remove(CookieConstants.MANAGER_UNAME_COOKIE);  
  
        jar.set(CookieConstants.MANAGER_ID_COOKIE, userDO.getId().toString());  
        try {  
            jar.set(CookieConstants.MANAGER_UNAME_COOKIE, URLEncoder.encode(userDO.getUserName(), "UTF-8"));  
        } catch (UnsupportedEncodingException e) {  
        }  
        jar.set(CookieConstants.MANAGER_PWD_COOKIE, userDO.getPassword());  
    }  
  
    public static void clearCookie(HttpServletRequest request) {  
        CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);  
        if (jar != null) {  
            jar.remove(CookieConstants.MANAGER_ID_COOKIE);  
            jar.remove(CookieConstants.MANAGER_UNAME_COOKIE);  
            jar.remove(CookieConstants.MANAGER_PWD_COOKIE);  
        }  
    }  
  
    public static Long getUserId(HttpServletRequest request) {  
        if (request == null) {  
            throw new IllegalArgumentException();  
        }  
        CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);  
  
        if (jar == null) {  
            return null;  
        }  
        String idStr = jar.get(CookieConstants.MANAGER_ID_COOKIE);  
        if (StringUtils.isNumeric(idStr)) {  
            return Long.valueOf(idStr);  
        }  
        return null;  
    }  
    说明：summercool框架已经对cookies进行了封装，在配置文件配置完成后就可以直接使用了
             只要通过reqeust对象就可以直接设置和获取cookie的相关信息了。
             笔者认为，所有的用户登录信息最好不用要session实现，因为session还要解决分布式的问题。笔者建议登录信息全部都存放在cookie里面是非常好的，因这样不仅可以实现登录信息的保存而且大大降低了开发难度；相关于变相的实现了无状态登录。
 
三、summercool框架对UrlRewrite封装
      1. 比如说我们有这样的一个需求，所有的页面都要通过/item/1.htm这样的url地址来访问“产品的详细页面”
      2. 像上面这样的配置规则，我不需要多于的代码编写，可以自动生成上面这样风格的url或是自定义的url风格
      3. 上面成生的url风格中，我们还可以提取“1”这样的参数或是url中的参数可以直接提取出来
请看url-configurer.xml的配置：
 
Xml代码  收藏代码
<?xml version="1.0" encoding="UTF-8" ?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"  
    xmlns:util="http://www.springframework.org/schema/util"  
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
        http://www.springframework.org/schema/beans/spring-beans-3.0.xsd  
        http://www.springframework.org/schema/context  
        http://www.springframework.org/schema/context/spring-context-3.0.xsd  
        http://www.springframework.org/schema/util  
        http://www.springframework.org/schema/util/spring-util-3.0.xsd">  
  
  
    <bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">  
        <property name="urlMap">  
            <map>  
                <entry key="/item/*.htm" value="/item/item_detail.htm" />  
            </map>  
        </property>  
    </bean>  
  
    <bean name="urlModuleConfigurer" class="org.summercool.web.module.UrlBuilderModuleConfigurer">  
        <property name="urlBuilderBeanMap">  
            <util:map>  
                <entry>  
                    <key><value>item</value></key>  
                    <bean name="id" class="org.summercool.beans.url.DefaultUrlBuilderBeanDefinition">  
                        <property name="seq" value="1" />  
                        <property name="uriTemplate" value="/item/{id}.htm" />  
                    </bean>  
                </entry>  
            </util:map>  
        </property>  
    </bean>  
  
</beans>  
    说明：1) SimpleUrlHandlerMapping：可以配置/item/*.htm这样的url地址全部都交给/item/item_detail.htm地址对应的Controller(/item/ItemDetailController.java)来处理。
             2) UrlBuilderModuleConfigurer：可以配置以模块为单位的url地址映射规则，并加入序号，如：
 
Xml代码  收藏代码
                           <entry>  
    <key><value>item</value></key>  
    <bean name="id" class="org.summercool.beans.url.DefaultUrlBuilderBeanDefinition">  
        <property name="seq" value="1" />  
        <property name="uriTemplate" value="/item/{id}.htm" />  
    </bean>  
</entry>  
    在上面这段配置中，我们可以配置一个item模块的url地址规则为：/item/{id}.htm
    在freemarker页面中，如果想使用上面规则的地址，则使用url内置函数：${url("item",param("id","1"))}
    在上面这个函数中，item：代表模块名，param是freemarker的内置函数，返回为map类型的数据，key=id, value=1
    如果大家细点一点，会发现上面有一个人seq的一个参数，这个是干什么用的呢？因为有时候，一个item模块可能会有很多的模版规则，如/item/c{category}.htm
           上面这个模版的意思是，查到一个item某类目下面的详细信息，这样的话我们可以将seq设置成2
           页面上可以做这样的使用：${url("item",param("category","1"),"2")} --> 其中的"2"就是seq的值，是让url函数调用url模版的时候，只调用seq=2的那个模版。当然，也可以不写（默认会调用第一个配置的url模版，即seq=1的模版）。
    所以，url地址规则：${url("item",param("id","1"))}  --> /item/1.htm；具体的应用请查看summercool-petstore应用的/index.ftl页面，如下：
 
Html代码  收藏代码
<!DOCTYPE html>  
<html lang="zh">  
  <head>  
    <meta charset="utf-8">  
    <title>Summercool, Petstore</title>  
    <meta name="viewport" content="width=device-width, initial-scale=1.0">  
    <meta name="description" content="">  
    <meta name="author" content="">  
  
    <!-- Le styles -->  
    <link href="/css/bootstrap.css" rel="stylesheet">  
    <style type="text/css">  
      body {  
        padding-top: 60px;  
        padding-bottom: 40px;  
      }  
      #tb td ,#tb th{  
        border-top: 0px;  
      }  
    </style>  
    <link href="/css/bootstrap-responsive.css" rel="stylesheet">  
  
    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->  
    <!--[if lt IE 9]>  
      <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>  
    <![endif]-->  
  
  </head>  
  
  <body>  
    ${widget("/petstore/widgets/header")}  
  
    <div class="container">  
      
     <table id="tb" class="table table-striped">  
        <tbody>  
          <tr>  
            <td class="span1"><img src="/images/tb_pet_1.jpg"/></td>  
            <td class="span12"><a href="${url("item",param("id","1"))}">兔子1</a></td>  
            <td>说明</td>  
          </tr>  
          <tr>  
            <td class="span1"><img src="/images/tb_pet_2.jpg"/></td>  
            <td class="span12">兔子2</td>  
            <td>说明</td>  
          </tr>  
          <tr>  
            <td class="span1"><img src="/images/tb_pet_3.jpg"/></td>  
            <td class="span12">小狗</td>  
            <td>说明</td>  
          </tr>  
        </tbody>  
      </table>  
  
      <hr>  
      ${widget("/petstore/widgets/footer")}  
  
    </div>  
  
  </body>  
</html>  
               3) 提取url中的参数，上面的例子只是提到了规则url地址的生成，但是如何将这些url中的参数提取出来呢？那么我们就要看一下，我们url对应处理的Controller，/item/ItemDetailController.java，如下：
 
Java代码  收藏代码
package org.summercool.platform.web.module.petstore.controllers.item;  
  
import java.util.Map;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.springframework.web.servlet.ModelAndView;  
import org.springframework.web.servlet.mvc.AbstractController;  
import org.summercool.beans.url.UrlBuilderBeanDefinition;  
import org.summercool.web.module.url.UrlBuilderModule;  
  
public class ItemDetailController extends AbstractController {  
  
    @Override  
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)  
            throws Exception {  
        UrlBuilderModule urlBuilderModule = (UrlBuilderModule) request.getAttribute(UrlBuilderModule.URL_BUILDER);  
        UrlBuilderBeanDefinition urlBuilderBean = urlBuilderModule.matchUrlBuilderBean();  
        // 如果在访问detail页面时，没有找查到相对应的url规则直接返回到/login.htm页面  
        if (urlBuilderBean == null || !urlBuilderBean.isMatched()) {  
            return new ModelAndView("redirect:/index.htm");  
        }  
        //  
        Map<String, String> map = urlBuilderBean.getUriTemplateVariables();  
        try {  
            String id = map.get("id");  
            // 在这里可以写一些业务逻辑，比如在DB中查找到item信息收直接显示detail页面，否则返回到/login.htm页面  
            Long.valueOf(id);  
        } catch (Exception e) {  
            return new ModelAndView("redirect:/index.htm");  
        }  
        //  
        return new ModelAndView("/petstore/views/item/itemDetail", map);  
    }  
  
}  
    说明：上面的代码中，有两行代码比较关键，如下：
 
Java代码  收藏代码
UrlBuilderModule urlBuilderModule = (UrlBuilderModule) request.getAttribute(UrlBuilderModule.URL_BUILDER);  
UrlBuilderBeanDefinition urlBuilderBean = urlBuilderModule.matchUrlBuilderBean();  
              这段代码中的两行，是让开发人员可以通过上面的方式来获得url模版所匹配的地址，如果匹配到则可以提取出url模版中所对应的参数，如：
 
Java代码  收藏代码
if (urlBuilderBean == null || !urlBuilderBean.isMatched()) {  
    return new ModelAndView("redirect:/index.htm");  
}  
    上面的代码是，如果匹配不到url对应的模版或是匹配不到其中的具体的规则，则重定向到/index.htm页面。
 
Java代码  收藏代码
Map<String, String> map = urlBuilderBean.getUriTemplateVariables();  
try {  
    String id = map.get("id");  
    // 在这里可以写一些业务逻辑，比如在DB中查找到item信息收直接显示detail页面，否则返回到/login.htm页面  
    Long.valueOf(id);  
} catch (Exception e) {  
    return new ModelAndView("redirect:/index.htm");  
}  
    上面的代码中，如果匹配到的url模版则可以通过上面的方式直接获取模版中url的参数map。
    上面的代码中加入了判断map参数的合法行（笔者也只是示意一下而已）。


===

一、Spring MVC中批量提交的处理
       1) 比如说我们有这样的一个需求，我们想批量更新一组数据信息，每组数据信息内容都相同；如下图：

      2) 比如说，上面这三行数据全部都是从DB中获取，然后批更更新后再提交到DB并返回到当前页面；但是，在Spring MVC框架中要是想实现这样的功能还是比较容易的，如下：
 
    /item/modifyItems.ftl
Html代码  收藏代码
<!DOCTYPE html>  
<html lang="zh">  
  <head>  
    <meta charset="utf-8">  
    <title>Summercool, Petstore</title>  
    <meta name="viewport" content="width=device-width, initial-scale=1.0">  
    <meta name="description" content="">  
    <meta name="author" content="">  
  
    <!-- Le styles -->  
    <link href="/css/bootstrap.css" rel="stylesheet">  
    <style type="text/css">  
      body {  
        padding-top: 60px;  
        padding-bottom: 40px;  
      }  
      #tb td ,#tb th{  
        border-top: 0px;  
      }  
    </style>  
    <link href="/css/bootstrap-responsive.css" rel="stylesheet">  
  
    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->  
    <!--[if lt IE 9]>  
      <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>  
    <![endif]-->  
  
  </head>  
  
  <body>  
    ${widget("/petstore/widgets/header")}  
    <div class="container">  
      
    <form method="post" action="/item/modify_items.htm">  
    <@spring.bind "modifyItemsFormBean.*"/>  
     <table id="tb" class="table table-striped">  
        <tbody>  
          <tr>  
            <td class="span1"><img src="/images/tb_pet_1.jpg"/></td>  
            <td class="span2"><a href="/item/1.htm">兔子1</a></td>  
            <td class="span10">  
                名字：<input type="text" name="pets[0].name" value="${modifyItemsFormBean.pets[0].name}"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  
                年龄：<input type="text" name="pets[0].age" value="${modifyItemsFormBean.pets[0].age}"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  
                ${error(status,"pets[0]")}  
            </td>  
            <td>说明</td>  
             <tr>  
            <td class="span1"><img src="/images/tb_pet_2.jpg"/></td>  
            <td class="span2"><a href="/item/1.htm">兔子2</a></td>  
            <td class="span10">  
                名字：<input type="text" name="pets[1].name" value="${modifyItemsFormBean.pets[1].name}"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  
                年龄：<input type="text" name="pets[1].age" value="${modifyItemsFormBean.pets[1].age}"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  
                ${error(status,"pets[1]")}  
            </td>  
            <td>说明</td>  
             <tr>  
            <td class="span1"><img src="/images/tb_pet_3.jpg"/></td>  
            <td class="span2"><a href="/item/1.htm">小狗1</a></td>  
            <td class="span10">  
                名字：<input type="text" name="pets[2].name" value="${modifyItemsFormBean.pets[2].name}"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  
                年龄：<input type="text" name="pets[2].age" value="${modifyItemsFormBean.pets[2].age}"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  
                ${error(status,"pets[2]")}  
            </td>  
            <td>说明</td>  
          </tr>  
        </tbody>  
      </table>  
            <div class="actions">  
                <input type="submit" value="提交" class="btn">&nbsp;<button class="btn" type="reset">取消</button>  
            </div>  
      </form>  
  
      <hr>  
      ${widget("/petstore/widgets/footer")}  
    </div>  
  
  </body>  
</html>  
 
    /item/ModifyItemsController.java
Java代码  收藏代码
package org.summercool.platform.web.module.petstore.controllers.item;  
  
import java.util.Map;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.springframework.validation.BindException;  
import org.springframework.web.servlet.ModelAndView;  
import org.springframework.web.servlet.mvc.SimpleFormController;  
import org.summercool.platform.web.module.petstore.formbean.ModifyItemsFormBean;  
import org.summercool.platform.web.module.petstore.pojo.Pet;  
  
@SuppressWarnings("deprecation")  
public class ModifyItemsController extends SimpleFormController {  
  
    public ModifyItemsController() {  
        setBindOnNewForm(true);  
        setCommandName("modifyItemsFormBean");  
        setCommandClass(ModifyItemsFormBean.class);  
        setFormView("/petstore/views/item/modifyItems");  
    }  
  
    @Override  
    protected void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception {  
        ModifyItemsFormBean formBean = (ModifyItemsFormBean) command;  
        //  
        Pet pet1 = new Pet();  
        pet1.setName("王");  
        pet1.setAge("25");  
  
        Pet pet2 = new Pet();  
        pet2.setName("少");  
        pet2.setAge("26");  
  
        Pet pet3 = new Pet();  
        pet3.setName("川");  
        pet3.setAge("27");  
        //  
        formBean.getPets().add(pet1);  
        formBean.getPets().add(pet2);  
        formBean.getPets().add(pet3);  
    }  
  
    @Override  
    protected Map<?, ?> referenceData(HttpServletRequest request) throws Exception {  
        return super.referenceData(request);  
    }  
  
    @Override  
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {  
        ModifyItemsFormBean formBean = (ModifyItemsFormBean) command;  
        for (int i = 0; i < formBean.getPets().size(); i++) {  
            Pet pet = formBean.getPets().get(i);  
            if (!"25".equals(pet.getAge())) {  
                errors.reject("pets[" + i + "]", "年龄必须为25!");  
            }  
        }  
    }  
  
    @Override  
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {  
        return showForm(request, response, errors);  
    }  
  
}  
       说明：在上面的批量更新代码中，在CommandObject的FromBean中有pets属性，该属性是List类型的，那么在页面中就可以通过循环列表显示，如果想通过批量提交就可以通过pets[0]这样的型式提交（0：代表List中的index）。
 
二、自定义freemarker内置函数的支持
       1) 虽然通过上面的表单批量提交，但是我们还需检验每一行的数据信息，如下：
 
Java代码  收藏代码
ModifyItemsFormBean formBean = (ModifyItemsFormBean) command;  
for (int i = 0; i < formBean.getPets().size(); i++) {  
    Pet pet = formBean.getPets().get(i);  
    if (!"25".equals(pet.getAge())) {  
        errors.reject("pets[" + i + "]", "年龄必须为25!");  
    }  
}  
 
           说明：表单提交后就可以通过formBean中的pets属性来提取表单信息，进一步完成表单信息的校验。
                     error.reject()函数就可以添加每个pets[i]错误检验信息。
       2) 在页面上，我们需要在每一行显示数据的检验之后的错误信息，如下：
 
Html代码  收藏代码
${error(status,"pets[0]")}  
 
           说明：error()函数就是freemarker自定义的函数，可以跟据每个“pets[i]”显示具体的错误信息
       3) error()的内置函数，信息如下：
 
Java代码  收藏代码
package org.summercool.platform.web.module.petstore.config.freemarker;  
  
import java.util.ArrayList;  
import java.util.List;  
  
import org.springframework.validation.ObjectError;  
import org.springframework.web.servlet.support.BindStatus;  
  
import freemarker.template.TemplateMethodModelEx;  
import freemarker.template.TemplateModel;  
import freemarker.template.TemplateModelException;  
import freemarker.template.utility.DeepUnwrap;  
  
public class FreeMarkerErrorFunction implements TemplateMethodModelEx {  
  
    public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException {  
        // 解包FreeMarker函数的参数  
        List<Object> args = new ArrayList<Object>();  
        for (Object object : arguments) {  
            args.add((object instanceof TemplateModel) ? DeepUnwrap.unwrap((TemplateModel) object) : object);  
        }  
        //   
        BindStatus bindStatus;  
        String errorCode;  
        //  
        if (args.size() != 2) {  
            throw new TemplateModelException("error()函数只支持参数:(BindStatus status, String errorCode)");  
        }  
        if (!(args.get(0) instanceof BindStatus) || !((args.get(1) instanceof String))) {  
            throw new TemplateModelException("error()函数只支持参数:(BindStatus status, String errorCode)");  
        }  
        //  
        bindStatus = (BindStatus) args.get(0);  
        errorCode = (String) args.get(1);  
        //  
        for (ObjectError error : bindStatus.getErrors().getAllErrors()) {  
            String[] codes = error.getCodes();  
            if (codes == null) { continue; }  
            for (String code : codes) {  
                if (code.equals(errorCode)) {  
                    return error.getDefaultMessage();  
                }  
            }  
        }  
        //  
        return null;  
    }  
}  
 
        说明：上面的freemarker内置函数生效，需要注册到freemarker全局工具类中，如（applicationContext.xml）：
 
Xml代码  收藏代码
<!-- Spring MVC页面层FreeMarker的处理类 -->  
<util:map id="uriModuleConstants">  
    <entry>  
        <key><value>error</value></key>  
        <bean class="org.summercool.platform.web.module.petstore.config.freemarker.FreeMarkerErrorFunction"/>  
    </entry>  
</util:map>  
