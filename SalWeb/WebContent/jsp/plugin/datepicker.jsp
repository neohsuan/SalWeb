<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>DatePicker</title>
<script src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-ui-1.9.2.js"></script>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery-ui.min.css">
<script type="text/javascript">
	$(function(){
		var datepickerOption = {
			dateFormat: "yy-mm-dd"
		};
		

		$("#datepickerEle").datepicker(datepickerOption);
		
	});
</script>
</head>
<body>
	<div>
		<input id="datepickerEle">
	</div>
</body>
</html>