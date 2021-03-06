<%@ page language="java" contentType="text/html; charset=windows-31j"
	pageEncoding="windows-31j"%>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title data-localize="title.content">flappy | Stress-less
	communication by message automatically disappear</title>

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
<script>
	(function(i, s, o, g, r, a, m) {
		i['GoogleAnalyticsObject'] = r;
		i[r] = i[r] || function() {
			(i[r].q = i[r].q || []).push(arguments)
		}, i[r].l = 1 * new Date();
		a = s.createElement(o), m = s.getElementsByTagName(o)[0];
		a.async = 1;
		a.src = g;
		m.parentNode.insertBefore(a, m)
	})(window, document, 'script', '//www.google-analytics.com/analytics.js',
			'ga');

	ga('create', 'UA-48246180-2', 'auto');
	ga('require', 'displayfeatures');
	ga('send', 'pageview');
</script>
</head>
<body>
	<script type=�htext/javascript�h>
	if (location.protocol == "http:") {
		location.href="https://flappy-communication.appspot.com/contact.jsp";
	}
</script>
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
						<a class="navbar-brand" href="index.html"
							onclick="ga('send', 'event', 'Contact', 'Header', 'Top');">flappy</a>
					</div>

					<div class="collapse navbar-collapse"
						id="bs-example-navbar-collapse-1">
						<ul class="nav navbar-nav">
							<li><a href="overview.html" data-localize="header.about"
								onclick="ga('send', 'event', 'Contact', 'Header', 'Overview');">About
									flappy</a></li>
							<li><a href="privacypolicy.html"
								data-localize="header.privacy"
								onclick="ga('send', 'event', 'Contact', 'Header', 'Privacy');">Privacy
									policy</a></li>
							<li><a href="tos.html" data-localize="header.tos"
								onclick="ga('send', 'event', 'Contact', 'Header', 'TOS');">Terms of
									use</a></li>
							<li><a
								href="https://flappy-communication.appspot.com/contact.jsp"
								data-localize="header.contact"
								onclick="ga('send', 'event', 'Contact', 'Header', 'Contact');">Contact</a></li>
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
							<li><a href="http://mame0112.hatenablog.com/"
								data-localize="header.blog"
								onclick="ga('send', 'event', 'Contact', 'Header', 'Blog');">Blog</a></li>
						</ul>
					</div>
				</div>
			</nav>
			<div class="page-header">
				<h1 data-localize="contact.title">Contact</h1>
				<p class="lead" data-localize="contact.subtitle">If you have any
					question or comment on this service, please contact to us from
					below form.</p>
			</div>
			<%
				if (session.getAttribute("result") != null) {
					String tmp = (String) session.getAttribute("result");
					if (tmp != null && !tmp.equals("null")) {
						int result = 0;
						try {
							result = Integer.valueOf(tmp);
						} catch (NumberFormatException e) {
							result = 10;
						}

						switch (result) {
							case 0 : //result OK
			%>
			<div class="alert alert-success"
				data-localize="contact.result_success">Thank you for
				submitting. Successfully sent out e-mail.</div>
			<%
				break;
							case 1 :
							default : // Default is used when cached data remains
								//Nothing to show
								break;
							case 2 :
							case 3 :
							case 4 :
							case 5 :
			%>
			<div class="alert alert-danger" data-localize="contact.result_failed">Failed
				to send inquery message</div>
			<%
				break;

						}

						//Remove session
						session.removeAttribute("result");

					}
				}
			%>
			<form method="post" name="inquery_form" action="servlet/inquery"
				accept-charset="UTF-8">
				<div class="row">
					<select name="servlet_inquery_category" id="visibleValue">
						<option value="Not selected"
							data-localize="contact.category_title" selected>Select
							category</option>
						<option value="For apply" data-localize="contact.category_apply">For
							apply</option>
						<option value="For how to use"
							data-localize="contact.category_how_to_use">For how to
							use</option>
						<option value="For bug / problem"
							data-localize="contact.category_bug">For bug / problem</option>
						<option value="For improvement request"
							data-localize="contact.category_improve_request">For
							improvement request</option>
						<option value="For ads, promotion and interviews"
							data-localize="contact.category_ads">For ads, promotion
							and interviews</option>
						<option value="For others" data-localize="contact.category_others">For
							others</option>
					</select>
				</div>

				<div class="row">
					<h5 data-localize="contact.form_name">
						Name <small data-localize="contact.form_mandatory">(Mandatory)</small>
					</h5>
					<div class="input-group">
						<input type="text" name="servlet_user_name" class="form-control">
					</div>
				</div>
				<div class="row">
					<h5 data-localize="contact.form_mail">
						Mail address <small data-localize="contact.form_mandatory">(Mandatory)</small>
					</h5>
					<div class="input-group">
						<input type="text" name="servet_mailAddress" class="form-control">
					</div>
				</div>
				<div class="row">
					<h5 data-localize="contact.form_message">Message</h5>
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
							class="btn btn-primary btn-lg"
							onclick="ga('send', 'event', 'Contact', 'Inquiry', 'SendButton');" />
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
				<li><a href="http://flappy-communication.appspot.com/"
					data-localize="footer.home"
					onclick="ga('send', 'event', 'Contact', 'Footer', 'Top');">Home</a></li>
				<li><a href="overview.html" data-localize="footer.about"
					onclick="ga('send', 'event', 'Contact', 'Footer', 'Overview');">About
						flappy</a></li>
				<li class="span3"><a href="privacypolicy.html"
					data-localize="footer.privacy"
					onclick="ga('send', 'event', 'Contact', 'Footer', 'Privacy');">Privacy
						policy</a></li>
				<li class="span4"><a href="tos.html" data-localize="footer.tos"
					onclick="ga('send', 'event', 'Contact', 'Footer', 'TOS');">Terms of use</a></li>
				<li class="span5"><a
					href="https://flappy-communication.appspot.com/contact.jsp"
					data-localize="footer.contact"
					onclick="ga('send', 'event', 'Contact', 'Footer', 'Contact');">Contact</a></li>
				<li class="span6"><a href="http://mame0112.hatenablog.com/"
					target="blank" data-localize="footer.blog"
					onclick="ga('send', 'event', 'Contact', 'Footer', 'Blog');">Blog</a></li>
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
