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

<link rel="shortcut icon" href="flappy.ico">

<script type="text/javascript" src="jquery.js" charset="utf-8"></script>
<script type="text/javascript" src="jquery.localize.js" charset="utf-8"></script>

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="js/bootstrap.min.js"></script>

	<div id="wrap">
		<div class="container">
			<%
				if (session.getAttribute("result") != null) {
					String result = (String) session.getAttribute("result");
					if (result != null && !result.equals("null")) {
			%>result:
			<%=result%>
			<%
				}
				}
			%>
			<form method="get" action="servlet/cipher_debug"
				accept-charset="UTF-8">
				<div class="row">
					<h5>Value</h5>
					<div class="input-group">
						<input type="text" name="servlet_cipher_encrypt"
							class="form-control">
					</div>
				</div>
				<div class="row">
					<h5>Value</h5>
					<div class="input-group">
						<input type="text" name="servlet_cipher_decrypt"
							class="form-control">
					</div>
				</div>
				<input type="hidden" value="debug">
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
				<li><a href="http://loosecommunication.appspot.com/"
					data-localize="footer.home">Home</a></li>
				<li><a href="overview.html" data-localize="footer.about">About
						flappy</a></li>
				<li class="span3"><a href="privacypolicy.html"
					data-localize="footer.privacy">Privacy policy</a></li>
				<li class="span4"><a href="tos.html" data-localize="footer.tos">Terms
						of use</a></li>
				<li class="span5"><a
					href="https://1-dot-loosecommunication.appspot.com/contact.jsp"
					data-localize="footer.contact">Contact</a></li>
				<li class="span6"><a href="http://mame0112.hatenablog.com/"
					target="blank" data-localize="footer.blog">Blog</a></li>
			</ul>
			<address>
				<strong>flappy</strong> flappy.communication@gmail.com
			</address>
		</div>
	</div>
	<script>
		function browserLanguage() {
			try {
				return (navigator.browserLanguage || navigator.language || navigator.userLanguage)
						.substr(0, 2)
			} catch (e) {
				return undefined;
			}
		}

		var lang = browserLanguage();
		if (lang == undefined) {
			lang = "en";
		} else {
		}
		var opts = {
			language : lang,
			skipLanguage : "en"
		};

		$("[data-localize]").localize("test", opts);
	</script>
</body>

</html>
