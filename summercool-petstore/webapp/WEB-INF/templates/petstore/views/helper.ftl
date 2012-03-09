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

      <!-- Main hero unit for a primary marketing message or call to action -->
      <div class="hero-unit">
        <h1>Hello, Guys!</h1>
        <p>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			简介：轻量封装Spring MVC<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			因为本人在国内最大的电子商务公司工作期间，深感一个好的Web框架可以大大提高工作效率，而一个不好的Web框架，又可以大大的降低开发效率。所以，在根据笔者在从事电子商务开发的这几年中，对各个应用场景而开发的一个轻量封装Spring MVC的一个Web框架。<br>
			<br>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			笔者工作的这几年之中，总结并开发了如下几个框架：<br>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			summercool-web（Web框架，已经应用于某国内大型网络公司的等重要应用）<br>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			summercool-hsf（基于Netty实现的RPC框架，已经应用国内某移动互联网公司）<br>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			summercool-ddl（基于Mybaits的分表分库框架，已经应用国内某移动互联网公司）<br>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			相继缓存方案、和消息系统解决方案也会慢慢开源。Summercool框架做为笔者的第一个开源框架<br>
			<br>
			框架地址：http://summercool.googlecode.com/svn/trunk/summercool-web <br>
			应用地址：http://summercool.googlecode.com/svn/trunk/summercool-petstore<br> 
			工具地址：http://summercool.googlecode.com/svn/trunk/summercool-tools<br>
			说明：此框架要用到spring-tools文件夹中的security文件夹中的文件，使用此框架的人员请将security文件夹的内容替换到JDK中的security文件夹中
		</p>
        <p><a class="btn btn-primary btn-large" href="http://dragonsoar.iteye.com/blog/1445669">Learn more &raquo;</a></p>
      </div>

<#--
	  <div class="row">
        <div class="span4">
          <h2>Heading</h2>
           <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
          <p><a class="btn" href="#">View details &raquo;</a></p>
        </div>
        <div class="span4">
          <h2>Heading</h2>
           <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
          <p><a class="btn" href="#">View details &raquo;</a></p>
       </div>
        <div class="span4">
          <h2>Heading</h2>
          <p>Donec sed odio dui. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Vestibulum id ligula porta felis euismod semper. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</p>
          <p><a class="btn" href="#">View details &raquo;</a></p>
        </div>
      </div>
-->
      <hr>
      ${widget("/petstore/widgets/footer")}

    </div>

  </body>
</html>
