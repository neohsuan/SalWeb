<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Dialog</title>
<script src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-ui-1.9.2.js"></script>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery-ui.min.css">
<script type="text/javascript">
	$(function(){
		var dialogOption = {
			autoOpen: true,//初始化後，是否自動打開對話框
			dialogClass: "message",//對話框的CSS
			draggable: false,//可否移動位置
			height: "auto",//高度
			width: 800,//寬度
			modal: true,//打開對話框時，要不要讓後面(下面)的UI無法操作
			position: {my:"center",at:"center",of:window},//對話框位置
			resizable: false,//可否改變大小
			title: "Dialog範例",

			buttons: {//在對話誇上新增按鈕
				"確定": function(){
					
				},
				"取消": function(){
					
					$(this).dialog("close");
				}
			}
		};
		

		$("#dialog-div").dialog(dialogOption);
		
	});
</script>
</head>
<body>
	<div id="dialog-div">
		這是一個對話框的範例
	</div>
</body>
</html>