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
