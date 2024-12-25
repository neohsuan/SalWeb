<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<title>datatables範例頁</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-ui-1.9.2.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.showLoading.js"></script>
<script src="${pageContext.request.contextPath}/js/example.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.ui.timepicker.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.dataTables.js"></script>
<script src="${pageContext.request.contextPath}/js/datatablebutton/dataTables.buttons.min.js"></script>
<script src="${pageContext.request.contextPath}/js/datatablebutton/jszip.min.js"></script>
<script src="${pageContext.request.contextPath}/js/datatablebutton/buttons.html5.min.js"></script>
<script src="${pageContext.request.contextPath}/js/datatablebutton/buttons.print.min.js"></script>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery.dataTables.css">
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/buttons.dataTables.css">
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery-ui-1.9.2.css">
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/showLoading.css">
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/example.css">
<script>
	$(function(){
		//顯示25個項目
		var opt = new Object();
		opt.iDisplayLength = 25;
		opt.aLengthMenu = [[10, 25, 50, 100], [10, 25, 50, 100]];
		opt.columns = [null, null, null, null];
		//產生匯出Excel & 列印的功能
		opt.dom = 'Blfrtip';
		opt.buttons = ['excel',
			{
	        	extend: 'print',
	            title: '',
	            exportOptions: {
	                columns: ':visible'
	            }
	        }
	    ];
	    //啟用DataTables
		var dt = $('#resulttable').DataTable(opt);
	
		$("#searchBtn").click(function(){
			$("#resultdiv").show();
			showWait();
			$.ajax({
				url: '${pageContext.request.contextPath}/pluginController.do',
				data:{
					method 		: 'getData',
					selVal		: $("#selVal").val()
				},
				type:"POST",
				contentType: "application/x-www-form-urlencoded;charset=utf-8",  
				dataType:"json",
				success:function(data){
					
					var arr1 = new Array();
					var arr2 = new Array();
						
					$.each(data.objs, function(idx, obj){
						arr2 = [obj.hhisnum, obj.hnursta, obj.hnamec, obj.drname];
						arr1.push(arr2);
							
					});
					dt.clear().rows.add(arr1).draw();
				},
				error: function(xhr){	
					alert("搜尋病房過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
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
病房：
<select id="selVal">
	<option value="AA">AA</option>
	<option value="BB">BB</option>
	<option value="CC">CC</option>
</select>
<input id="searchBtn" type="button" class="smallBtn" value="搜尋">
<div id="resultdiv" style="width:100%;display:none;">
	<hr>
	<table id="resulttable" class="display" cellspacing="0" style="width:100%;">
		<thead>
			<tr>
				<th>病歷號</th>
				<th>病房</th>
				<th>姓名</th>
				<th>主治醫師</th>
			</tr>
		</thead>
	</table>
</div>
</body>
</html>