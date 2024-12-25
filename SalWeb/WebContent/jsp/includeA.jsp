<!DOCTYPE HTML><%@page language="java"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>  

<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", -1); //prevents caching at the proxy server
    long ts = (new java.util.Date()).getTime();   
%>
<head>


</head>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/theme/itStyles.css?ts="+"<%=ts%>"/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/theme/iconClass.css?ts="+"<%=ts%>"/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/theme/jquery-ui-1.10.4.custom.min.css?ts="+"<%=ts%>"/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/tab.css?ts="+"<%=ts%>"/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/components/datatables/dataTables.jqueryui.min.css?ts="+"<%=ts%>"/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/components/datatables/Extensions/Buttons-1.7.0/css/buttons.jqueryui.min.css"+"<%=ts%>"/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/components/datatables/Extensions/FixedColumns/fixedColumns.jqueryui.min.css?ts="+"<%=ts%>"/>
<!-- Jquery -->
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.min.js?ts="+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.json-2.4.min.js?ts="+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-ui-1.10.4.min.js?ts="+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.formatCurrency-1.4.0.js?ts="+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.ui.datepicker-zh-TW-customize.js?ts="+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.validate.js?ts="+"<%=ts%>"></script>




<!--   <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css"> -->
<!--   <script src="//code.jquery.com/jquery-1.12.4.js"></script> -->
<!--   <script src="//code.jquery.com/ui/1.12.1/jquery-ui.js"></script> -->





<!-- datatable -->
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/components/datatables/jquery.dataTables.min.js?ts="+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/components/datatables/dataTables.jqueryui.min.js?ts="+"<%=ts%>"></script>


<!-- datatable Extensions  -->
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/components/datatables/Extensions/Buttons-1.7.0/js/dataTables.buttons.min.js"+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/components/datatables/Extensions/Buttons-1.7.0/js/buttons.jqueryui.min.js"+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/components/datatables/JSZip-2.5.0/jszip.min.js"+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/components/datatables/pdfmake-0.1.36/pdfmake.min.js"+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/components/datatables/pdfmake-0.1.36/vfs_fonts.js"+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/components/datatables/Extensions/Buttons-1.7.0/js/buttons.html5.min.js"+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/components/datatables/Extensions/Buttons-1.7.0/js/buttons.print.min.js"+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/components/datatables/Extensions/Buttons-1.7.0/js/buttons.colVis.min.js"+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/components/datatables/Extensions/FixedColumns/dataTables.fixedColumns.min.js?ts="+"<%=ts%>"></script>



<!-- Utility JS -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ITUtil.js?ts="+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/Common.js?ts="+"<%=ts%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/tool.js?ts="+"<%=ts%>"></script>
<!-- showloading -->
<script src="${pageContext.request.contextPath}/js/jquery.showLoading.js?ts="+"<%=ts%>"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme/jquery-showLoading.css?ts="+"<%=ts%>" />

<style> 
	/* dialog button 置右 */
	.ui-dialog .ui-dialog-buttonpane { 
	    text-align: right;
	}
	.ui-dialog .ui-dialog-buttonpane .ui-dialog-buttonset { 
	    float: none;
	}
	.ui-datepicker select.ui-datepicker-month, .ui-datepicker select.ui-datepicker-year{
		width:45%
	}
	
	table.dataTable tbody tr.even{
    	background-color:#f2dede;
	}
	table.dataTable tbody tr.odd{
    	background-color:#bce8f1;
	}
	table.dataTable tbody td{
    	border:1px solid #ddd;
	}
	
	/* 驗證的css */
	form.cmxform label.error, label.error {
	/* remove the next line when you have trouble in IE6 with labels in list */
		color: red;
		font-style: italic;
		font-size: 12px;
	}
	/* datatables Scroll - horizontal and vertical */
	th, td { padding:5px,5px;white-space: nowrap; }
    div.dataTables_wrapper {
/*         width: 1000px; */
 		width:auto;  
        margin: 0 auto;
    }
 	
 	ul{margin:0}
 	table.dataTable tbody th, table.dataTable tbody td{
 		padding: 4px 14px;
 	}
 	
 	table.dataTable thead th, table.dataTable thead td {
	  padding: 4px 15px;
	  border-bottom: 1px solid #111;
	}

</style>
<script>
//dataTable 共用設定-----------------------------------------------------------------------------------------------
//Datatable樣版設定
var dataTableConfig = {
	"bProcessing":true,//2014/11/14:先不能設true，會發生顯示圖檔但處理完不會消失問題
	"bJQueryUI": true,
	"sPaginationType": "full_numbers",
	"iDisplayLength":10,
	"lengthMenu": [[10, 20, 30, -1], [10, 20, 30, "All"]],
	"bAutoWidth": false,
	"oLanguage":{"sUrl": "${pageContext.request.contextPath}/components/datatables/dataTables.zh-tw.txt"}	//繁體中文UI設定	 
	};
dataTableConfig = $.extend(true, {}, dataTableConfig);	
//設定datatable高度		            
dataTableConfig.bScrollAutoCss=false;
dataTableConfig.scrollY = false;
dataTableConfig.scrollX = true;//產生X scrollbar
dataTableConfig.scrollCollapse= true;
dataTableConfig.destroy = true;
//金額欄位格式-統一設定
var numConfig = {	
		roundToDecimalPlace: -1 
		};
//dataTable 共用設定-----------------------------------------------------------------------------------------------		
		
		

</script>
