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
	  			密码: <input type="password" size="30" name="password" id="password" class="medium">
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
