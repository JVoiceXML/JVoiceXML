<%
HttpSession httpSession = request.getSession(false);
if(httpSession != null) {
	httpSession.invalidate();
}
%>
<html>
<head>
<title>Sample "Hello, World" Application</title>
</head>
<body bgcolor=white>

<p><font size="6">Simple Click To Call Demo</font></p>
<form method="GET" action="call">

	<p>To:</p>
	<p><input type="text" name="to" size="20" value="sip:to@127.0.0.1:5050"></p>
	<p>From:</p>
	<p><input type="text" name="from" size="20" value="sip:from@127.0.0.1:5060"></p>
	<p><input type="submit" value="Submit" name="B1"><input type="reset" value="Reset" name="B2"></p>
</form>
<p>&nbsp;</p>

</body>
</html>
