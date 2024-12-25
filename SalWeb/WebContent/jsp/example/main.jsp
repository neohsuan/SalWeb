<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<title>範例主頁</title>
<jsp:include page="../index.jsp"></jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-ui-1.9.2.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.showLoading.js"></script>
<script src="${pageContext.request.contextPath}/js/example.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.ui.timepicker.js"></script>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery-ui-1.9.2.css">
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/showLoading.css">
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/example.css">
<script>
	$(function(){
		$("#testBtn").click(function(){
			showWait();
			$.ajax({
				url:'${pageContext.request.contextPath}/exampleController.do',
				data:{
					method 		: 'testAjax'
				},
				type:"POST",
				contentType: "application/x-www-form-urlencoded;charset=utf-8",  
				dataType:"text",
				success:function(data){
					alert(data);
				},
				error: function(xhr){	
					alert("測試ajax過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
				},
				complete: function(){
					hideWait();
				}
			});
		});
	});
</script>
</head>
<body>
Hi~${user.userNm}
<input id="testBtn" type="button" class="smallBtn" value="測試">
</body>
</html>