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
            <td class="span12"><a href="/item/1.htm">兔子1</a></td>
            <td>说明</td>
          </tr>
        </tbody>
      </table>

      <hr>
      ${widget("/petstore/widgets/footer")}
    </div>

  </body>
</html>
