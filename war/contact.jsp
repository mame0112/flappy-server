<%@ page language="java" contentType="text/html; charset=windows-31j"
	pageEncoding="windows-31j"%>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>flappy</title>

<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="css/jumbotron.css">
<link rel="stylesheet" type="text/css" href="css/original.css">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="js/bootstrap.min.js"></script>
	<script type="text/javascript">
		$(function() {
			$('.dropdown-menu a').click(function() {
				var visibleTag = $(this).parents('ul').attr('visibleTag');
				var hiddenTag = $(this).parents('ul').attr('hiddenTag');
				$(visibleTag).html($(this).attr('value'));
				$(hiddenTag).val($(this).attr('value'));
			})
		})
	</script>

	<div id="wrap">
		<div class="container">
			<nav class="navbar navbar-default" role="navigation">
				<div class="container-fluid">
					<!-- Brand and toggle get grouped for better mobile display -->
					<div class="navbar-header">
						<button type="button" class="navbar-toggle" data-toggle="collapse"
							data-target="#bs-example-navbar-collapse-1">
							<span class="sr-only">Toggle navigation</span> <span
								class="icon-bar"></span> <span class="icon-bar"></span> <span
								class="icon-bar"></span>
						</button>
						<a class="navbar-brand" href="index.html">flappy</a>
					</div>

					<div class="collapse navbar-collapse"
						id="bs-example-navbar-collapse-1">
						<ul class="nav navbar-nav">
							<li><a href="overview.html">About flappy</a></li>
							<li><a href="privacypolicy.html">Privacy policy</a></li>
							<li><a href="tos.html">Terms of use</a></li>
							<li><a href="contact.jsp">Contact</a></li>
						</ul>
						<ul class="nav navbar-nav navbar-right">
							<li><a href="https://twitter.com/share"
								class="twitter-share-button" data-via="flappy_comm"
								data-size="large">Tweet</a> <script>
									!function(d, s, id) {
										var js, fjs = d.getElementsByTagName(s)[0], p = /^http:/
												.test(d.location) ? 'http'
												: 'https';
										if (!d.getElementById(id)) {
											js = d.createElement(s);
											js.id = id;
											js.src = p
													+ '://platform.twitter.com/widgets.js';
											fjs.parentNode
													.insertBefore(js, fjs);
										}
									}(document, 'script', 'twitter-wjs');
								</script></li>
							<li><a href="http://mame0112.hatenablog.com/">Blog</a></li>
					</div>
			</nav>
			<div class="page-header">
				<h1>Contact</h1>
				<p class="lead">If you have any question or comment on this
					service, please contact to us from below form.</p>
			</div>
			<%
				String tmp = (String) session.getAttribute("result");
				int result = Integer.valueOf(tmp);
				switch (result) {
				case 0: //result OK
			%>
			<div class="alert alert-success">Success!</div>
			<%
				break;
				case 1:
					//Nothing to show
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				default:
			%>
			<div class="alert alert-danger">Failed to send inquery message</div>
			<%
				break;

				}
			%>
			<form method="post" name="inquery_form" action="servlet/inquery">
				<div class="row">
					<!-- Single button -->
					<div class="btn-group">
						<button type="button" class="btn">
							<span id="visibleValue">Select category</span>
						</button>
						<button type="button" class="btn dropdown-toggle"
							data-toggle="dropdown">
							<span class="caret"></span>
						</button>
						<ul class="dropdown-menu" role="menu" hiddenTag="#hiddenValue"
							visibleTag="#visibleValue">
							<li><a href="javascript:void(0)" value="apply">For apply</a></li>
							<li><a href="javascript:void(0)"
								value="For how
									to use">For how to use</a></li>
							<li><a href="javascript:void(0)"
								value="For bug /
									problem">For bug / problem</a></li>
							<li><a href="javascript:void(0)"
								value="For
									improvement request">For improvement
									request</a></li>
							<li><a href="javascript:void(0)"
								value="For
									ads, promotion and interviews">For ads,
									promotion and interviews</a></li>
							<li><a href="javascript:void(0)"
								value="For
									others">For others</a></li>
						</ul>
						<input type="hidden" id="hiddenValue"
							name="servlet_inquery_category" value="">
					</div>
				</div>

				<div class="row">
					<h5>
						Name <small>(Mandatory)</small>
					</h5>
					<div class="input-group">
						<input type="text" name="servlet_user_name" class="form-control">
					</div>
				</div>
				<div class="row">
					<h5>
						Mail address <small>(Mandatory)</small>
					</h5>
					<div class="input-group">
						<input type="text" name="servet_mailAddress" class="form-control">
					</div>
				</div>
				<div class="row">
					<h5>Message</h5>
					<div class="input-group">
						<textarea name="servlet_message_body" cols="40" rows="5"
							class="form-control"></textarea>
					</div>
				</div>
				<input type="hidden" id="hiddenValue" value="inquery"> <input
					type="hidden" name="servlet_origin" value="inquery"> <input
					type="hidden" name="servlet_api_level" value="1">
				<div class="row">
					<p>
						<input type="submit" value="Submit" type="button"
							class="btn btn-primary btn-lg" />
					</p>
				</div>
			</form>
		</div>
		<!-- /container -->
	</div>
	<!-- Wrap end -->
	<div id="footer">
		<div class="container">
			<ul class="nav nav-pills">
				<li><a href="http://loosecommunication.appspot.com/">Home</a></li>
				<li><a href="overview.html">About flappy</a></li>
				<li class="span3"><a href="privacypolicy.html">Privacy
						policy</a></li>
				<li class="span4"><a href="tos.html">Terms of use</a></li>
				<li class="span5"><a href="contact.jsp">Contact</a></li>
				<li class="span6"><a href="http://mame0112.hatenablog.com/"
					target="blank">Blog</a></li>
			</ul>
			<address>
				<strong>flappy</strong> flappy.communication@gmail.com
			</address>
		</div>
	</div>
</body>

</html>
