<!DOCTYPE HTML><%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
  

<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", -1); //prevents caching at the proxy server
       
%>
<head>


</head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" > <!-- 強制使用IE Edge -->

<!-- CSS -->
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/components/bootstrap-4.6.0-dist/css/bootstrap.min.css" />">
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/components/bootstrap-select-1.13.14-dist/css/bootstrap-select.css" />">
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/components/css/itStyles.css" />">
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/components/css/theme/iconClass.css" />">
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/components/css/tab.css" />">
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/components/css/theme/jquery-ui-1.10.4.custom.min.css" />">
<!-- Jquery -->
<script type="text/javascript" src="<c:url value="/resources/components/js/jquery-1.10.2.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/js/jquery.json-2.4.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/js/jquery-ui-1.10.4.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/js/jquery.formatCurrency-1.4.0.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/js/jquery.ui.datepicker-zh-TW-customize.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/jquery.validate.js" />" charset="UTF-8"></script>
<script type="text/javascript" src="<c:url value="/resources/components/js/jquery.table2excel.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/js/jquery.form.js" />"></script>

<%-- <script type="text/javascript" src="<c:url value="/resources/components/popper/src/popper.js" />"></script> --%>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>

<!-- fontawesome -->
<link rel="stylesheet" href="<c:url value="/resources/components/fontawesome/css/fontawesome.min.css"/>">
<link rel="stylesheet" href="<c:url value="/resources/components/fontawesome/css/brands.min.css"/>">
<link rel="stylesheet" href="<c:url value="/resources/components/fontawesome/css/solid.min.css"/>">

<!-- datatable -->
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/components/datatables/dataTables.jqueryui.min.css" />">
<script type="text/javascript" src="<c:url value="/resources/components/datatables/jquery.dataTables.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/datatables/dataTables.jqueryui.min.js" />"></script>

<!-- datatable Extensions  -->
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/components/datatables/Extensions/Buttons-1.7.0/css/buttons.jqueryui.min.css" />">
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/components/datatables/Extensions/FixedColumns/fixedColumns.jqueryui.min.css" />">

<script type="text/javascript" src="<c:url value="/resources/components/datatables/Extensions/Buttons-1.7.0/js/dataTables.buttons.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/datatables/Extensions/Buttons-1.7.0/js/buttons.jqueryui.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/datatables/JSZip-2.5.0/jszip.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/datatables/pdfmake-0.1.36/pdfmake.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/datatables/pdfmake-0.1.36/vfs_fonts.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/datatables/Extensions/Buttons-1.7.0/js/buttons.bootstrap4.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/datatables/Extensions/Buttons-1.7.0/js/buttons.html5.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/datatables/Extensions/Buttons-1.7.0/js/buttons.print.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/datatables/Extensions/Buttons-1.7.0/js/buttons.colVis.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/datatables/Extensions/FixedColumns/dataTables.fixedColumns.min.js" />"></script>

<!-- Utility JS -->
<script type="text/javascript" src="<c:url value="/resources/components/js/MEBEUtil.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/js/Common.js" />"></script>

<!-- bootstrap -->

<script type="text/javascript" src="<c:url value="/resources/components/bootstrap-4.6.0-dist/js/bootstrap.min.js" />"></script>
<%-- <script type="text/javascript" src="<c:url value="/resources/components/DataTables-1.10.24/js/dataTables.bootstrap4.min.js" />"></script>   --%>

<!-- import excel -->
<%-- <script type="text/javascript" src="<c:url value="/resources/js/xlsx.full.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/FileSaver.min.js" />"></script> --%>

<link rel="stylesheet" href="<c:url value="/resources/components/css/MEBEStyle.css" />">

<!-- bootstrap-fileinput 檔案設定 -->
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/components/bootstrap-fileinput/css/fileinput.min.css" />">
<script type="text/javascript" src="<c:url value="/resources/components/bootstrap-fileinput/js/fileinput.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/components/bootstrap-fileinput/js/locales/zh-TW.js" />"></script>

<!-- bootstrap-select -->

<script type="text/javascript" src="<c:url value="/resources/components/bootstrap-select-1.13.14-dist/js/bootstrap-select.js" />"></script>



<style>
/*上傳檔案*/
.custom-file-upload {
    transition: border-color 0.15s ease-in-out;
}
.custom-file-upload:hover {
    border-color: #28a745 !important;
}
.custom-file-upload input[type="file"] {
    border: none;
    background: transparent;
    padding-left: 0;
}				
/* 專門處理這種「多組 label+input 並排」且希望間距一致的情況 */
.basother-panel .form-row--equal {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.basother-panel .form-row--equal .form-group {
  display: flex;
  align-items: center;
  min-width: 220px;   /* 每組 label+input 固定佔用寬度，讓下一組起始位置對齊 */
  margin-right: 0;
  margin-bottom: 8px;
}

.basother-panel .form-row--equal .form-group label {
  min-width: 90px;     /* 統一 label 寬度，含「身份證字號:」5字 */
  text-align: right;
  margin: 0 8px 0 0;
  white-space: nowrap;
  font-weight: bold;
}

  .basother-panel {
    padding: 12px 8px;
    border: 1px solid #e0e0e0;
    
    background: #fafafa;
  }

  .basother-panel .form-row {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    margin-bottom: 8px;
  }

  .basother-panel .form-row:last-child {
    margin-bottom: 0;
  }

  .basother-panel .form-group {
    display: flex;
    align-items: center;
    margin-right: 20px;
    margin-bottom: 0;
  }

  .basother-panel .form-group label {
    min-width: 130px;      /* 統一 label 寬度,依實際最長文字調整 */
    text-align: right;
    margin: 0 8px 0 0;
    white-space: nowrap;
    font-weight: bold;      /* 新增：文字粗體 */
  }

  .basother-panel .form-control {
    height: 30px;
    padding: 4px 8px;
  }

  /* 戶籍地址欄位較長,獨立一行時 label 對齊方式相同即可 */
  .basother-panel input[readonly] {
    background-color: #f0f0f0;
  }
  
  .basother-panel .form-group label.required::after {
    content: " *";
    color: #dc3545;
    font-weight: bold;
  }
  
  .basother-filter-group {
    gap: 10px 14px;
  }

  .filter-item {
    display: flex;
    flex-direction: column;
    min-width: 100px;
  }

  .filter-item-wide {
    min-width: 160px;
  }

  .filter-label {
    font-size: 12px;
    color: #666;
    margin-bottom: 2px;
    white-space: nowrap;
  }

  .filter-item .form-control,
  .filter-item .bootstrap-select {
    height: 30px;
  }
  /*篩選按鈕置中*/
 .basother-btn-group {
	position: relative;
	left: 50%;
	right: 50%;
	margin-left: -50vw;
	margin-right: -50vw;
	width: 100vw;
	display: flex;
	justify-content: center;
	gap: 0.5rem;
	padding-top: 10px;
	padding-bottom: 10px;
 }
/*基本資料*/
/* 左右兩欄間距與邊框 */
 .basother-detail-row {
	gap: 12px 0;
 }

 .baspsnListDiv-wrap {
	border: 1px solid #dee2e6;
	border-radius: 6px;
	overflow: hidden;
 }

 .basother-card-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
 }
 /* 讓查詢/清空/更改三顆按鈕寬度一致，方便精準對齊 */
 .basother-fixed-btn {
	width: 78px;
 }

 /* 清空按鈕寬度(90px) + gap(8px，對應 .gap-2) = 98px
   讓「更改」往右多留 98px，使其左邊界對齊「查詢」的左邊界 */
 .basother-align-under-qry {
	margin-right: 112px;
 }
 
.ui-datepicker-header select.ui-datepicker-month,
.ui-datepicker-header select.ui-datepicker-year {
    display: inline-block;
    width: 45%;      /* 依實際寬度微調 */
    float: none;
    vertical-align: middle;
}

.ui-datepicker-title {
    text-align: center;
    white-space: nowrap;
}
 
	@font-face {
		font-family: EUDC;
		src: url(https://zwac01p.vghks.gov.tw:4436/PUBService/PUB/CORSProxy/OutputStream?url=https://wwwhf.vghks.gov.tw/Kai_EUDC.WOFF) format("woff"),
			 url(https://zwac01p.vghks.gov.tw:4436/PUBService/PUB/CORSProxy/OutputStream?url=https://wwwhf.vghks.gov.tw/VGHKH_KAI.TTF) format("truetype");
		}
	body { font-family: EUDC; } 
	
	thead input {  width: 100%;}
	.table {
            word-break: break-all;
            width: 100%;
        }
	.modal-dialog {
	  max-width: 100%;
	  height: 100%;
	  padding: 0;
	}
	
	.modal-content {
/* 	  height: 100%; */
	  border-radius: 0;
	}
	.card-body{
		padding: 0.25rem;
	}
	
	.fixed-table-width {
	  table-layout: fixed;
	}
	.fixed-table-width .table-cell-data {
	  word-break: break-all;
	}
	
 .alignCenter { text-align: center !important;}
 .alignLeft { text-align: left !important;}
 .ui-widget-header{font-size: 11px !important;}
 .ui-widget input, .ui-widget select, .ui-widget textarea, .ui-widget button{font-size: 14px !important;font-family: Verdana, Arialx !important;}
 .dataTables_wrapper {font-size: 13px;}
 .ui-widget-header{font-size: 11px !important;}
 .dataTables_wrapper .ui-toolbar{padding:2px;}
 .dataTables_wrapper .dataTables_paginate .paginate_button{margin-left:0px;padding:0px 0px;}}
 .page-link{line-height:0.5}
 .ui-widget-header a { 
 	color: #222; 
 } 
/* dialog button 置右 */
.ui-dialog .ui-dialog-buttonpane {
	text-align: right;
}

.ui-dialog .ui-dialog-buttonpane .ui-dialog-buttonset {
	float: none;
}
/* 驗證的css */
form.cmxform label.error,label.error {
	/* remove the next line when you have trouble in IE6 with labels in list */
	color: red;
	font-style: italic;
	font-size: 12px;
}

/* datatables Scroll - horizontal and vertical */
th,td {
	white-space: nowrap;
}

div.dataTables_wrapper { /*width: 1000px;*/
	width: auto;
	margin: 0 auto;
}
/* navbar */
	.navbar {
    		padding:.1rem .5rem; 
 	} 
/* navbar */
/* 	.navbar-default { */
/*    		background-color: #9dccfc; */
/*     	border-color: #9dccfc; */
/* 	} */

/*---------------menu bar CSS---------------------------------------------  */
#menubar {
	font-family: "Lato", "Helvetica Neue", Helvetica, Arial, sans-serif;
	font-size: 22px;
	list-style-type: none;
	margin: 0;
	/* 	padding: 0; */
	overflow: hidden;
	background-color: #769dc0;
}

#menubar li {
	float: left;
}

#menubar li a,.dropbtn {
	display: inline-block;
	color: white;
	text-align: center;
	/*     padding: 14px 16px; */
	text-decoration: none;
}

#menubar li a:hover,.dropdown:hover .dropbtn {
	color: #474747;
	/*     background-color: #85c1ff;  */
}

#menubar li.dropdown {
	display: inline-block;
}

#menubar .dropdown-content {
	display: none;
	position: absolute;
	background-color: #f9f9f9;
	min-width: 160px;
	box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
	z-index: 1;
}

#menubar .dropdown-content a {
	color: black;
	padding: 12px 16px;
	text-decoration: none;
	display: block;
	text-align: left;
}

#menubar .dropdown-content a:hover {
	color: white;
	background-color: #2c3e50;
}

#menubar .dropdown:hover .dropdown-content {
	display: block;
}
/* .modal-header { */
/* 	background-color: #9dccfc; */
/* } */
/*---------------menu bar CSS---------------------------------------------  */
</style>
<script>
		
	var userid ="${sessionScope.user.getLoginId()}";	//使用者卡號
	var username ="${sessionScope.user.getUserNm()}";   //使用者姓名
	var hid ="${sessionScope.user.getHid()}";         	//醫院代碼
 	var costid ="${sessionScope.user.getCosId()}";      //使用者成本中心
 	var deptno ="${sessionScope.user.getDeptNo()}";      //使用者登入者單位
 	var deptnm ="${sessionScope.user.getDeptName()}";   //使用者單位名稱
 	
 	var pa_admin ="${sessionScope.user.getPa_adm()}";	//讀取ldap_管理者
 	var pa_edit ="${sessionScope.user.getPa_edit()}";	//讀取ldap_維護角色
 	var pa_view ="${sessionScope.user.getPa_view()}";	//讀取ldap_查詢角色
 	var pa_bones ="${sessionScope.user.getPa_bones()}";	//讀取ldap_員工獎金角色
 	var pa_tax ="${sessionScope.user.getPa_tax()}";		//讀取ldap_所得稅角色
	
	//代理人資訊
	var agent="";										//被代理人卡號
	var agentname="";         							//使用者姓名
	var agentcostid="";            						//使用者成本中心
	var agentdeptno="";         							//使用者成本中心英文
	var agentdeptnm="";         						//使用者成本中心中文
	var agenthid="";         							//使用者醫院別
	var agentpswktypd="";         						//職稱代碼
	var agentAdmin="";
	var agentEdit="";
	var agentView="";
	var agentBones="";
	var agentTax="";
	//dataTable 共用設定-----------------------------------------------------------------------------------------------
//Datatable樣版設定
var dataTableConfig = {
	"bProcessing":true,//2014/11/14:先不能設true，會發生顯示圖檔但處理完不會消失問題
	"bJQueryUI": true,
	"sPaginationType": "full_numbers",
	"iDisplayLength":10,
	"lengthMenu": [[10, 20, 30, -1], [10, 20, 30, "All"]],
	"bAutoWidth": false,
	"bSort": true,//排序
	"bFilter": true,//過濾功能
	"bInfo": true,//頁腳信息
	"bLengthChange":true, //改變每頁顯示數據數量
	"bPaginate":true, //翻頁功能
 	"oLanguage":{"sUrl": "${pageContext.request.contextPath}/resources/components/datatables/dataTables.zh-tw.txt"}	//繁體中文UI設定	 
	};
dataTableConfig = $.extend(true, {}, dataTableConfig);	
//設定datatable高度		            
dataTableConfig.bScrollAutoCss=false;
dataTableConfig.scrollY = false;
dataTableConfig.scrollX = true;//產生X scrollbar
//dataTableConfig.paging= false;
dataTableConfig.scrollCollapse= true;
dataTableConfig.destroy = true;
//金額欄位格式-統一設定
var numConfig = {	
		roundToDecimalPlace: -1 
		};
//dataTable 共用設定-----------------------------------------------------------------------------------------------		
	
	$( function() {
	    
	    //匯入bootstrap dataTable
		$(document).ready(function() {
	    	$('#example').DataTable({
	    		language:{
	    			"processing":   "處理中...",
				    "loadingRecords": "載入中...",
				    "lengthMenu":   "顯示 _MENU_ 項結果",
				    "zeroRecords":  "沒有符合的結果",
				    "info":         "顯示第 _START_ 至 _END_ 項結果，共 _TOTAL_ 項",
				    "infoEmpty":    "顯示第 0 至 0 項結果，共 0 項",
				    "infoFiltered": "(從 _MAX_ 項結果中過濾)",
				    "infoPostFix":  "",
				    "search":       "搜尋:",
				    "paginate": {
				        "first":    "第一頁",
				        "previous": "上一頁",
				        "next":     "下一頁",
				        "last":     "最後一頁"
    				}
	    		}
	    	});
		});
		
		//如果不是管理員隱藏代理人功能
		if("${sessionScope.user.getPa_adm()}" == "N"){
			$(".manage").hide();
		}
		//如果代理人卡號為空,隱藏
		if("${sessionScope.agent}" == null || "${sessionScope.agent}" == ""){
			$("#showAgentCol").hide();
		}
		
		//依卡號帶入名子
	    $("#agentText").on("change keyup", function(){
	    	if($(this).val() == "") return;
	    	if ( $(this).val().length == 4 ){
	    		$(this).val($(this).val().toUpperCase());
	    		//加條件,2A0,3A0各查各的
	    		var xuserName = getNamebyID($(this).val().toUpperCase());
	    		
		    	if(xuserName != ""){
		    		$(this).parent().find("#agentnm").text(xuserName);
		    	}else{
		    		$("#agentText").val("");
		    		$("#agentnm").text("");
		    		showSysMsgTxt("提示訊息","3","卡號輸入錯誤或不同院區","","關閉","",""); //引用include.jsp的訊息視窗
		    	}
	    	} else {
	    		//查不到卡號就清除資料
	    		$("#agentnm").text("");
	    	}    	
	    });
	
	});
	
	//依卡號帶入人名,加登入者HID
	var agentData;
	function getNamebyID(xuserid){
		var foundUserName = "";
		$.ajax({
			url:"${pageContext.request.contextPath}/salController.do",
			data: {
				method   : 'getNamebyID',
				cardno   : xuserid,
				loginHid : hid,	
			},
			dataType: "json",
			async: false,	//逐步執行
			type: "POST",
			success: function(data) {
				foundUserName = data.userName;
				if ( null != foundUserName) {
					agentData = data;
				} else {
					foundUserName = "";
				}
			},
			error: function(xhr) {	
				//showSysMsgTxt("提示訊息","3","卡號輸入錯誤","","關閉","",""); //引用include.jsp的訊息視窗
			},
			complete: function(data) {
				hideLoading(); //隱藏loading
			}
		});
		
		return foundUserName;
	}
	
	//設定代理人
	function setAgent() {
		agent = $('#agentText').val();
		if ( "" != agent ) {
			agentname = agentData.userName;//使用者姓名
			agentcostid = agentData.userCosId;//使用者成本中心
			agentdeptno = agentData.userDept;//使用者單位名稱_英文
			agentdeptnm = agentData.userDeptName;//使用者成本中心中文
			agenthid = agentData.userHid;//使用者醫院別
			agentpswktypd =agentData.userPsWkTypd;//使用者職稱代碼
			agentAdmin=agentData.userAdmin;//代理人_Admin
			agentEdit=agentData.userEdit;//代理人_Edit
			agentView=agentData.userView;//代理人_View
			agentBones=agentData.userBones;//代理人_Bones
			agentTax=agentData.userTax;//代理人_Tax
 		}

		$.ajax({
				url: "${pageContext.request.contextPath}/salController.do",
				data: {
					"method"      : 'setAgent',
					"agent"       : agent,
					"agentname"   : agentname,
					"agentcostid" : agentcostid,
					"agentdeptno" : agentdeptno,
					"agentdeptnm" : agentdeptnm,
					"agenthid"    : agenthid,
					"agentpswktypd" : agentpswktypd,
					"agentAdmin"  : agentAdmin,
					"agentEdit"   : agentEdit,
					"agentView"   : agentView,
					"agentBones"  : agentBones,
					"agentTax"    : agentTax,
				},
// 				dataType: "json",
				async: false,	//逐步執行
				type: "POST",
				success: function(data) {
					$('#agentDialog').modal("hide");
					if ( "" != agent ) {
						$("#showAgentCol").show();
						$("#agentid").text(agent+agentname);
					}
					window.location.reload();
				},
				error: function(xhr) {
					alert("程式發生錯誤！！！");
					$('#agentDialog').modal("hide");	
				},
				complete: function(data) {
					hideLoading(); //隱藏loading
				}
			});
// 		$( "#agentDialog" ).dialog( "close" );
	}
	
	//sysMsgTxt(操作提示), Matitle(標題)、Matype(圖片類型)、Mamsg(簡易訊息)、yesBtnTitle(確認按鈕名稱)、noBtnTitle(關閉按鈕名稱)、yesBtnAction(按下確認鍵連結的function)、detailMsg(詳細訊息)
	function showSysMsgTxt(sysMsgTxt, /*Matitle,*/ Matype, Mamsg, yesBtnTitle, noBtnTitle, yesBtnAction, detailMsg){
	
		//標題
		var Matitle = "<h4 class=\"modal-title\" id=\"myModalLabel\">"+sysMsgTxt+"</h4>";
		$('#modal_sys_msg .modal-header').html(Matitle); //載入資訊
		
		//內容	
		var Mgr_alert_html = "";
		//Mgr_alert_html += "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>";
		Mgr_alert_html += "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
		Mgr_alert_html += "<td width=\"90px\" align=\"center\" valign=\"middle\">";
		if ( "0" == Matype ) Mgr_alert_html += ' <img src="<c:url value="/resources/images/msg_ok.png"/>" alt="">';
		else if ( "1" == Matype ) Mgr_alert_html += ' <img src="<c:url value="/resources/images/msg_help.png"/>" alt="">';
		else if ( "2" == Matype ) Mgr_alert_html += ' <img src="<c:url value="/resources/images/msg_search.png"/>" alt="">';
		else if ( "3" == Matype ) Mgr_alert_html += ' <img src="<c:url value="/resources/images/msg_attention.png"/>" alt="">';
		else if ( "4" == Matype ) Mgr_alert_html += ' <img src="<c:url value="/resources/images/msg_redx.png"/>" alt="">';
		else if ( "5" == Matype ) Mgr_alert_html += ' <img src="<c:url value="/resources/images/msg_info.png"/>" alt="">';
		
		
		Mgr_alert_html += "</td>";
		Mgr_alert_html += '<td><sapn style="display:inline-block;width:100%;word-wrap:break-word;white-space:normal">'+Mamsg+'</sapn></td>';
		Mgr_alert_html += "</tr></table></td></tr><tr><td align=\"center\">&nbsp;";
		
		$('#modal_sys_msg .modal-body').html(Mgr_alert_html); //載入資訊
		
		//標尾
		var footer_html = "";
		if(yesBtnTitle!=""){ //確認
			footer_html += "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\" onClick=\""+yesBtnAction+"\"> "+yesBtnTitle+"</button>";
		}
		if(noBtnTitle!=""){ //關閉
			footer_html += "<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">"+noBtnTitle+"</button>";
		}		
        $('#modal_sys_msg .modal-footer').html(footer_html); //載入資訊
		
		
		$("#modal_sys_msg").modal(); //顯示dialog視窗
		$("#modal_sys_msg").removeClass("formTable");
	}
	
	//開啟loading dialog
	function showLoading(){
		$("#modal_loading").modal("show");
	}
	
	//關閉loading dialog
	function hideLoading(){
		$("#modal_loading").modal("hide");
	}

</script>

<body>

<!-- 代理人 -->
<input type="hidden" id="agent" name="agent" value=""> <!-- 代理人卡號 -->

<!-- 主要導航====================================================================================================================-->
<!-- navbar 項目內容 -->

<nav class="navbar navbar-expand-sm bg-dark navbar-dark">
  <!-- Brand/logo -->
  <a class="navbar-brand" href="<c:url value="/jsp/index.jsp"/>"><img src="<c:url value="/resources/images/home_48.png"/>" width="35" height="30" >薪資作業首頁</a>
  <!-- Links -->
  <ul class="navbar-nav ml-auto">
    <li class="manage nav-item">
    	<a class="nav-link" href="#" data-toggle="modal" data-target="#agentDialog"><img src="<c:url value="/resources/images/admin.png"/>" width="35" height="30" >代理</a>    	      
    </li>    
  </ul>
</nav>

<!-- 顯示目前操作者及代理人 -->
<div id="showAgentDiv" class='container-fluid'>
	<div class="row" >
		<div class="col-3" id="showUserCol">
		操作者 : <span id="useropid">${sessionScope.user.getHid()} ${sessionScope.user.getLoginId()}${sessionScope.user.getUserNm()}</span>
		</div>
		<div class="col-3" id="showAgentCol">
		代理 : <span id="agentid">${sessionScope.agenthid} ${sessionScope.agent}${sessionScope.agentname}</span>
		</div>
	</div>	
</div>

<!-- 代理人dialog -->
<div class="modal fade" id="agentDialog" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
      	<button type="button" class="close" data-dismiss="modal">&times;</button>
        <h5 class="modal-title" id="exampleModalLabel" style="font-size:20px">代理人設定</h5>
      </div>
      <div class="modal-body">
      	<div class="form-inline" role="form">
      		<!-- <select id="setHID"class="form-select form-select-sm" aria-label=".form-select-sm example">
			  <option selected>醫院代碼</option>
			  <option value="1A0">高雄榮總</option>
			  <option value="2A0">屏東榮總</option>
			  <option value="3A0">聯合醫院</option>
			</select> -->
        	<label for="title">請輸入卡號:</label>
        	<input type="text" id="agentText" name="agentText" class="form-control" size="8" placeholder="請輸入卡號" onkeyup="this.value = this.value.toUpperCase()" />
        	&emsp;<span id="agentnm"></span>
        </div>
      </div>
      <div class="modal-footer">
      	<button type="button" id="setAgentBtn" class="btn btn-primary" onclick="setAgent();">設定</button>
      	<button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
      </div>
    </div>
  </div>
</div>

<!-- 系統訊息dialog -->
<div class="modal fade" id="modal_sys_msg" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-dialog-scrollable" role="document">
		 <div class="modal-content">
		 	<div class="modal-header" id="modal_header_modal_sys_msg">
			</div>
		 	<div class="modal-body" id="modal_body_modal_sys_msg" >		 		
		 	</div>
		 	<div class="modal-footer">                
            </div>
		 </div>
	</div>
</div>
<div class="modal fade modal-spinner" id="modal_loading" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-dialog-centered justify-content-center" role="document">
        <div class="loader" style="margin: 0px auto;"></div>
    </div>
</div>

</body>
