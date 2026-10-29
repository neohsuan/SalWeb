<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html>
<head>
<title>系統作業</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="../include.jsp"></jsp:include>
<style>
/* 本頁專屬 loading 遮罩,ID 刻意跟共用的 modal_loading 區分 */
#pageLoadingOverlay {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.4);
    z-index: 9999;
    justify-content: center;
    align-items: center;
}
#pageLoadingOverlay .loader {
    /* 沿用 include.jsp 裡 .loader 的樣式,如果外部 CSS 已有 .loader 定義可以直接沿用 */
    width: 50px;
    height: 50px;
    border: 5px solid #f3f3f3;
    border-top: 5px solid #3498db;
    border-radius: 50%;
    animation: pageSpin 0.8s linear infinite;
}
@keyframes pageSpin {
    0%   { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}
</style>
<script>
//檢查使否有代理人，如有代理人則取代原資料
var agent = "${sessionScope.agent}";	//代理人卡號
var paUserid = ("" != agent) ? agent : userid;            //使用者卡號
var paUsername = ("" != agent) ? "${sessionScope.agentname}" : username;          //使用者姓名
var pahid = ("" != agent) ? "${sessionScope.agenthid}" : hid;                     //醫院別
var pacostid = ("" != agent) ? "${sessionScope.agentcostid}" : costid;            //使用者成本中心
var padeptnm = ("" != agent) ? "${sessionScope.agentdeptnm}" : deptnm;            //使用者單位名稱
var padeptno = ("" != agent) ? "${sessionScope.agentdeptno}" : deptno;            //使用者單位名稱 英文
var today = new Date();
var firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

var tabid="";
var objCopy;
var in_data;
var codeTbl;
var oTable;
var patradt;
var salnum;
var cardno;
var paycode;
var salNm;
var selEn;
var selPs;
var selCode; 
var hid;
var rowData;
var msg;
var patradt;
var blnCheck;
var oTableInsu = null;

// DataTables 55 欄位 mapping
var objCopyInsu = {
    "bDestroy": true,
    "bAutoWidth": false,
    "bPaginate": true,
    "bLengthChange": true,
    "bFilter": true,
    "bSort": true,
    "bInfo": true,
    "iDisplayLength": 25,
    "aLengthMenu": [[10, 25, 50, 100, -1], [10, 25, 50, 100, "全部"]],
    "sDom": 'B<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-tl ui-corner-tr"lfr>t<"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix ui-corner-bl ui-corner-br"ip>',
    "buttons": ['excelHtml5'],
    
    // 💡 關鍵新增：開啟橫向捲動，讓表格總寬度超出時自動產生下方捲軸橫幅
    "sScrollX": "100%",
    "sScrollXInner": "2200px", // 可依實際顯示微調，例如 2000px ~ 2500px
    "bScrollCollapse": true,
    "aoColumns": [
        { "mData": "PAYIDNO", "sClass": "text-center", "sDefaultContent": "" },
        { "mData": "PAYCARD", "sClass": "text-center", "sDefaultContent": "" },
        { "mData": "PAYNAMEC", "sClass": "text-center", "sDefaultContent": "" },
        { "mData": "PAYTITLE", "sClass": "text-center", "sDefaultContent": "" },
        { "mData": "M1_PAYBANK", "sClass": "text-center", "sDefaultContent": "" },
        { "mData": "M1_FP000", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_FP010", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_FP100", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_FP110", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_FP120", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_FP400", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_VP000", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_VP010", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_VP01E", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_VP040", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_VP100", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_VP110", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_VP200", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_VP220", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_VP230", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M1_VP457", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_PAYBANK", "sClass": "text-center", "sDefaultContent": "" },
        { "mData": "M2_FP000", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_FP010", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_FP100", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_FP110", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_FP120", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_FP400", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_VP000", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_VP010", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_VP01E", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_VP040", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_VP100", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_VP110", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_VP200", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_VP220", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_VP230", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M2_VP457", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_PAYBANK", "sClass": "text-center", "sDefaultContent": "" },
        { "mData": "M3_FP000", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_FP010", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_FP100", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_FP110", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_FP120", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_FP400", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_VP000", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_VP010", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_VP01E", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_VP040", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_VP100", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_VP110", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_VP200", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_VP220", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_VP230", "sClass": "text-right", "sDefaultContent": "0" },
        { "mData": "M3_VP457", "sClass": "text-right", "sDefaultContent": "0" }
    ]
};

$(function(){
	$('#collapseOne').collapse('show');
		
	$('body').on('focus', '.datepicker', function() { //以此方式才能動態更改頁面datepicker
          $(this).datepicker( { dateFormat: 'yy-mm-dd' } );
    });
	$(".gclsCalTxt").datepicker({ 
		changeMonth: true,
      	changeYear: true,
      	dateFormat:"yy-mm-dd"
    });
    $(".dateBat").datepicker({ 
		changeMonth: true,
      	changeYear: true,
      	showOtherMonths : true,
	    hideIfNoPrevNext : true,
	    showAnim:"blind",
      	dateFormat:"yy-mm-dd",
      	minDate: 0, 
      	maxDate:"+3M"
    });  	
	$(".selectpicker").selectpicker({
		noneSelectedText : '請選擇'//預設顯示內容
    });
    //初始化重新整理資料
	$(window).on('load', function() {
		$('.selectpicker').selectpicker('refresh');
	});
	//查詢角色,將編輯功能隱藏
	if (pa_view=="Y"){
		$(".roleQry").addClass("d-none");	
	}else{
		$(".roleQry").removeClass("d-none");
	}
	//編輯角色,將編輯功能show
	if ((pa_admin=="Y" && agent =="") || pa_edit=="Y" || (agent !="" && "${sessionScope.agentEdit}"=="Y" )){
		$(".roleQry").removeClass("d-none");	
	}else{
		$(".roleQry").addClass("d-none");
	}
	//
	if (pahid=="1A0" || pahid=="3A0") {
		$("#pills-pay-tab").hide();
	}else if (pahid=="2A0") {
		$("#pills-pay-tab").show();
	}
	

  	//代碼選單切換
	$("#qryType1").change(function(){
		$(":text,textarea").val("");
		$("#divfield_ID,#divfield_CARD").addClass("d-none");	//隱藏
  		selCode=(typeof($("#qryType1 option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType1 option:selected").val().split(':')[0];
  		console.log(selCode);
  		if (selCode=="1" ){			//1:[整批更新]卡號 
			$("#divfield_ID").removeClass("d-none");			
			$('#msgHintText').text('卡號變更僅更新"基本個人明細"');
		}
		else if (selCode=="2"){		//2:[整批更新]身份證
			$("#divfield_CARD").removeClass("d-none");
			$('#msgHintText').text('身份證變更將更新"基本個人明細、異動支扣明細、批次檔、所得稅個人明細、住址檔"');
		}
  	});
  	 	
  	 	
	//編輯欄位檢查
  	$.checkFields = function(btn){
  		msg = '';
  		var isChecked=0;
  		
  		if (btn== 'btn_qryBatPay'){	//執行 1.整批更新卡號
  			selCode=(typeof($("#qryType1 option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType1 option:selected").val().split(':')[0];
  	  		console.log(selCode);
  		
  			if (selCode=="1"){
  				if( $("#qry_pabatpay_cardno").val()=='' || $("#qry_pabatpay_cardno1").val()=='' || $("#qry_pabatpay_cardno").val().length < 4 || $("#qry_pabatpay_cardno1").val().length < 4 ){
  		  				msg = '請輸入卡號(長度不足4碼)!!';
  		  			}
  			}else if(selCode=="2"){
  				if( $("#qry_pabatpay_idtno").val()=='' || $("#qry_pabatpay_idtno1").val()=='' || $("#qry_pabatpay_idtno").val().length < 10 || $("#qry_pabatpay_idtno1").val().length < 10 ){
		  				msg = '請輸入身份證字號(長度不足10碼)!!';
		  			}
			}
  		}else if (btn == 'btn_loadBat'){ //執行 2.批次匯入
  			var file = $("#fileInput").val();			
			if (file.length == 0) {
				msg ='請確認是否有選擇檔案';				
			}
  		}
  		return msg;	
	};

	//jQuery按紐圖示
	$(".btn").not("#lnkSampleFile").button().click(
		function(event) {event.preventDefault();}
	);
	$(".btn").click(function(){
		var id = $(this).attr('id');		
		
		if(id == 'btn_clnBatPay' ){
  			$(":text,textarea").val("");
			return false;
		}else if(id == 'btn_qryBatPay'){	//執行 1.整批更新卡號 2.整批更新身份證
			msg = $.checkFields(id);
			if(msg == ''){
				
				selCode=(typeof($("#qryType1 option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType1 option:selected").val().split(':')[0];
	  	  		console.log("執行："+selCode);
	  	  		
	  	  		cardno=$("#qry_pabatpay_cardno").val();
	  	  		cardno_new=$("#qry_pabatpay_cardno1").val();
	  	  		idtno=$("#qry_pabatpay_idtno").val();
	  	  		idtno_new=$("#qry_pabatpay_idtno1").val();	  	  	
	  	  		console.log("卡號old："+cardno+" 卡號new："+cardno_new+" 身份證old:"+idtno+" 身份證new:"+idtno_new);
	  	  						
				in_data={
						cardno     : cardno,
						cardno_new : cardno_new,
						idtno      : idtno,
						idtno_new  : idtno_new
		        };					
				$.ajax({
					url:"${pageContext.request.contextPath}/salController.do",
					data:{
						method	: "updateIDCard",
						editMode : selCode,
						in_data : JSON.stringify(in_data),						
						userid  : paUserid,
						hid     : pahid
					},
					type:"POST",		//type:POST, GET(default:GET)
					dataType:"json",	//dataType: text, xml, html, script, json, jsonp					
					contentType: "application/x-www-form-urlencoded;charset=utf-8",  
					success:function(data) {
						if (data.msg!=null){
							alert(data.msg);
						}else{
							alert("更新成功！");		
						}
					},
					error: function(xhr){					
						alert("更新[整批變更卡號或身份證]失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
					}
				}); 
			}
			else{
				alert(msg);
			}		
		}else if (id == 'btn_loadBat'){ //批次匯入
			msg = $.checkFields(id);
			if(msg == ''){
				selCode=(typeof($("#qryType option:selected").val().split(':')[0]) == "undefined" )? "1":$("#qryType option:selected").val().split(':')[0];
	  	  		console.log("執行："+selCode);
				
	  	  		var url="${pageContext.request.contextPath}/salController.do";
	  	  		var param = '?method=uploadExcelFile' +
							'&selCode=' + selCode +
							'&userid=' + paUserid +
							'&hid=' + pahid;
	    		console.log(" 選項:"+selCode+" url:"+url+param);
	    		
	    		showPageLoading(); //顯示loading(本頁專屬)
			 	$("#uploadfileForm").ajaxSubmit({
					type: "post", //提交方式 
					dataType:"json", //資料型別 
					url: url + param, //請求url 
					success: function (data) { //提交成功的回撥函式 
						alert(data.msg);
						hidePageLoading();
					},
					error: function(xhr){
						alert("檔案上傳過程中，程式發生錯誤!!!\nerror code:"+ xhr.status);
						hidePageLoading();
					},
					complete: function(data) {
						hidePageLoading(); //隱藏loading
					}
				});
			}else{
				alert(msg);
			}
		}
		else if(id == 'btn_qryInsuPer'){ 
		    var qryDate = $("#qry_insu_date").val(); // 純日期字號，例如 "2026-10-01"
		    var qryType = $("#qry_insu_type").val(); // 身分類別

		    if (qryDate == '') {
		        alert("請選擇發薪日！");
		        return false;
		    }
		    
		    var baseDate = new Date(qryDate);
		    var d1 = new Date(baseDate);
		    var d2 = new Date(baseDate); d2.setMonth(d2.getMonth() - 1);
		    var d3 = new Date(baseDate); d3.setMonth(d3.getMonth() - 2);

		    var formatDate = function(d) {
		        var year = d.getFullYear();
		        var month = ('0' + (d.getMonth() + 1)).slice(-2);
		        return year + '-' + month + '-01';
		    };

		    $("#th_mon1_title").text("發薪日 (" + formatDate(d1) + ")");
		    $("#th_mon2_title").text("發薪日 (" + formatDate(d2) + ")");
		    $("#th_mon3_title").text("發薪日 (" + formatDate(d3) + ")");

		    var in_data = {
		        paydate : qryDate,
		        paytype : qryType
		    };
		    console.log("送出查詢參數：", in_data);
		    
		    showPageLoading(); 

		    $.ajax({
		        url: "${pageContext.request.contextPath}/salController.do",
		        data: {
		            method  : 'getInsuPerList', 
		            in_data : JSON.stringify(in_data),
		            hid     : pahid,
		            userid  : paUserid
		        },
		        type: "POST",
		        dataType: "json",
		        contentType: "application/x-www-form-urlencoded;charset=utf-8",
		        success: function(data) {
		        console.log("後端實際回傳的資料：", data);
		            hidePageLoading(); 
		            if (data.msg != null && data.msg != "") {
		                alert(data.msg);
		            } else {
                        $(".dataTables_wrapper").hide(); 
                        $('#tblInsuPerList').show();
                        
                        var listData = data.data || data.insuList || [];
                        var currentConfig = $.extend(true, {}, objCopyInsu, {
                            "aaData": listData
                        });
                        oTableInsu = $('#tblInsuPerList').dataTable(currentConfig);

                        if (listData.length === 0) {
                            alert("查無符合條件的發薪資料！");
                        }
                    }
                },
		        error: function(xhr){
		            hidePageLoading();
		            alert("查詢失敗!!!程式發生錯誤!!!\nerror code:"+ xhr.status);		
		        }
		    });
		}

	});

	// 初始化動態發薪日選單
	initPayDateDropdown();
});

/**
 * 動態產生發薪日下拉選單 (恢復為純日期格式)
 */
function initPayDateDropdown() {
	var selectElem = document.getElementById("qry_insu_date");
	if (!selectElem) return;

	var today = new Date();
	var currentYear = today.getFullYear();
	var currentMonth = today.getMonth() + 1;
	var startYear = currentYear - 1;

	var optionsHtml = '<option value="">-- 請選擇發薪日 --</option>';

	for (var y = currentYear; y >= startYear; y--) {
		for (var m = 12; m >= 1; m--) {
			if (y === currentYear && m > currentMonth) continue;

			var monthStr = (m < 10) ? '0' + m : '' + m;
			var dateVal = y + '-' + monthStr + '-01';
			optionsHtml += '<option value="' + dateVal + '">' + dateVal + '</option>';
		}
	}

	selectElem.innerHTML = optionsHtml;
	
	var thisMonthStr = (currentMonth < 10) ? '0' + currentMonth : '' + currentMonth;
	var defaultVal = currentYear + '-' + thisMonthStr + '-01';
	selectElem.value = defaultVal;
}


//清除指定頁面資料
	function cleanTable(table_id){
		$(":checkbox,:radio").prop("checked", false);
		//清除所有頁面的內容(enable)，disable的不清除
		$('#'+table_id+' :input:enabled').each(function() {
			$(this).val("");
	    });
	    
	    //所有select預設選擇第一項
	    $('#'+table_id+' select').each(function() {
			$(this).prop("selectedIndex", 0);
	    });
	    
	    //清除所有table的row，只留第一行
	    $('#'+table_id+' >tbody').each(function() {
	    	var tableID = $(this).closest('table').attr("id");
	    	$(this).find("tr").each(function(index, tr) { 
	    		if (index >= 1 ) {
		    		$(this).remove(); //清除第一行以外的row
		    	}	    		
			});
	    	
	    });
	    $('.selectpicker').selectpicker('deselectAll');
	    $('.selectpicker').selectpicker('refresh'); 	    
	}
	
//開啟loading dialog
function showPageLoading(){
	$("#pageLoadingOverlay").css("display", "flex");
}

//關閉loading dialog
function hidePageLoading(){
	$("#pageLoadingOverlay").css("display", "none");
}
</script>
</head>
<body>

<ul class="nav nav-pills mb-3" id="pills-tab" role="tab">
  <li class="nav-item">
    <a class="nav-link active" id="pills-battask-tab" data-toggle="pill" href="#byBatTaskMain" role="tab" aria-controls="pills-home" aria-selected="true">更改作業</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-bathanding-tab" data-toggle="pill" href="#byBatHandingMain" role="tab" aria-controls="pills-home" aria-selected="true">批次匯入</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="pills-pay-tab" data-toggle="pill" href="#byPayDetailMain2Mon" role="tab" aria-controls="pills-home" aria-selected="false">產生投保資料</a>
  </li>
</ul>

<div class="tab-content" id="pills-tabContent">
	<!--更改作業-->
  	<div class="tab-pane fade show active" id="byBatTaskMain" role="tabpanel" aria-labelledby="pills-battask-tab">
		<div class="container-fluid">
			<div class="row" id="qryBatchPayItem">
				<div class="col-8">
					<div class="form-inline basother-filterbar">
						<div class="input-group" style="margin-right:10px;">
							<span class="input-group-text">選單</span>		  	
							<select id="qryType1" class="form-control">
								<option value="1:[整批更新]卡號:UpdCard">1整批更新卡號</option>
								<option value="2:[整批更新]身份證:UpdID">2整批更新身份證</option>     					
							</select>
						</div>
						
		    			<div class="input-group " id="divfield_ID" style="margin-right:10px;">		    				
		    				<input type="text" id="qry_pabatpay_cardno" class="text-uppercase form-control" maxlength="4" size="6" placeholder="原卡號" >
		    				<span class="input-group-text">→</span>
		    				<input type="text" id="qry_pabatpay_cardno1" class="text-uppercase form-control" maxlength="4" size="6" placeholder="新卡號" >		    										
						</div>    		
			    		<div class="input-group d-none" id="divfield_CARD" style="margin-right:10px;">			    							
							<input type="text" id="qry_pabatpay_idtno" class="text-uppercase form-control" maxlength="10" size="10" placeholder="原身份證" >
							<span class="input-group-text">→</span>
							<input type="text" id="qry_pabatpay_idtno1" class="text-uppercase form-control" maxlength="10" size="10" placeholder="新身份證" >
						</div>
						<!-- 訊息提示框 -->
						<div class="alert alert-info" id="msgHint" style="display:inline-block; vertical-align:middle; background-color:#d9edf7; border-color:#bce8f1; color:#31708f; padding:6px 12px; margin-bottom:0; font-size:14px; line-height:1.6;">
            				<i class="fa fa-bullhorn"></i><span id="msgHintText">卡號變更僅更新"基本個人明細"</span>
        				</div>
						
					</div>
					<div class="form-inline basother-filterbar" style="margin-top:10px; text-align:left;">
						<button id="btn_qryBatPay" class="btn btn-success" type="button" style="margin-right:8px;">
        					<i class="fa fa-cog"></i> 執行
						</button>
						<button id="btn_clnBatPay" class="btn btn-warning roleQry basother-fixed-btn" type="button">
        					<i class="fa fa-eraser"></i> 清空	
    					</button>
    				</div>
				</div>
			</div>				

 		</div>	
	</div>
	
	<!--批次匯入-->
  	<div class="tab-pane fade show" id="byBatHandingMain" role="tabpanel" aria-labelledby="pills-bathanding-tab">
  		<div class="row p-1 mb-1 bg-secondary">
			<div class="col-md-4">
				<div class="form-inline">
					<div class="input-group">
						<span class="input-group-text">選單</span>
		  	
						<select id="qryType" class="form-control">
							<option value="1:[匯入]階級支扣數:ImpPayInBat">1[匯入] 階級支扣數</option>
							<option value="2:[匯入]公務員健保:DelPayInBat">2[匯入] 公務員健保</option>     					
						</select>
					</div>
					&emsp;
					<a id="lnkSampleFile" href="${pageContext.request.contextPath}/salController.do?method=downloadSample&type=1" class="btn btn-outline-light">
						<i class="fa fa-download"></i>下載範例檔案
                	</a>
                	&emsp;
                	<button id="btn_loadBat" class="btn btn-success" type="button" >
						<i class="fa fa-cog"></i>執行
					</button>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="form-inline" id="rowBatHanding_1">
					<div class="input-group">
											
						<div class="col-md-12 " id="divfield_1">							
							<label class="form-label mb-1 d-block">匯入檔案（Excel），僅支援 .xlsx / .xls 格式</label>
							<form id="uploadfileForm" class="mb-0">
								<div class="custom-file-upload d-flex align-items-center border rounded p-2 bg-light">
                        			<i class="fa fa-file-excel-o text-success mr-2" style="font-size:1.3rem;"></i>
                        			<input type="file" accept=".xlsx,.xls,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel" id="fileInput" name="fileInput" class="form-control-file flex-grow-1">
                               	</div>
                               
                            </form>
                        </div>
					   	<!-- 訊息提示框 -->
						<div class="alert alert-info" id="msgHint" style="display:inline-block; vertical-align:middle; background-color:#d9edf7; border-color:#bce8f1; color:#31708f; padding:6px 12px; margin-bottom:0; font-size:14px; line-height:1.6;">
            				<i class="fa fa-bullhorn"></i><span id="msgHintText">請先[下載範例檔案]置於硬碟,可更名,修正後,再選擇檔案上傳匯入.</span>
        				</div>
					</div>
				</div>
			</div>
		</div>
		<!--  -->
  	</div>

   <!-- 產生給人事室發薪資料(發薪日-2個月) -->
    <div class="tab-pane fade" id="byPayDetailMain2Mon" role="tabpanel" aria-labelledby="pills-pay-tab">
        <div class="container-fluid">
            <div class="row" style="margin-bottom: 15px;">
                <div class="col-12">
                    <div class="form-inline basother-filterbar">
                    
 				
                        <div class="input-group" style="margin-right:10px;">
                            <span class="input-group-text">發薪日</span>         
                            <select id="qry_insu_date" class="form-control">
                                <option value="">-- 請選擇發薪日 --</option>
                            </select>
                        </div>
                        
                        <div class="input-group" style="margin-right:10px;">
                            <span class="input-group-text">身分選擇:</span>         
                            <select id="qry_insu_type" class="form-control">
                                <option value="contract">契約人員</option>
                                <option value="doctor">醫師</option>
                                <option value="worker">技工友</option>
                            </select>
                        </div>
                        

                        <button type="button" id="btn_qryInsuPer" class="btn btn-primary">
                            <i class="fa fa-search"></i> 查詢
                        </button>
                        <div class="alert alert-info" id="msgHint" style="display:inline-block; vertical-align:middle; background-color:#d9edf7; border-color:#bce8f1; color:#31708f; padding:6px 12px; margin-bottom:0; font-size:14px; line-height:1.6;">
 				<i class="fa fa-bullhorn"></i><span id="msgHintText">產生給人事室發薪資料(發薪日-2個月)</span>
 				</div>
                        
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-12" style="overflow-x: auto;">
                    <table id="tblInsuPerList" class="table table-bordered table-striped" style="width:100%; display:none;">
                        <thead>
                            <tr class="text-center">
                                <th rowspan="2" style="vertical-align: middle;">身分證字號</th>
                                <th rowspan="2" style="vertical-align: middle;">卡號</th>
                                <th rowspan="2" style="vertical-align: middle;">姓名</th>
                                <th rowspan="2" style="vertical-align: middle;">職稱</th>
                                <th colspan="17" id="th_mon1_title" class="bg-primary text-white">發薪日(當月)</th>
                                <th colspan="17" id="th_mon2_title" class="bg-info text-white">發薪日(前1個月)</th>
                                <th colspan="17" id="th_mon3_title" class="bg-secondary text-white">發薪日(前2個月)</th>
                            </tr>
                            <tr class="text-center">
                                <th>發薪日</th><th>本薪FP000</th><th>專業加給FP010</th><th>院區加給FP100</th><th>身心障礙FP110</th><th>特別津貼FP120</th><th>計畫補助FP400</th><th>工作獎金VP000</th><th>專勤醫師費VP010</th><th>專勤獎金-科經營VP01E</th><th>專勤醫師費2VP040</th><th>夜班費VP100</th><th>不休假加班費VP110</th><th>加班費VP200</th><th>未休完特休假工資VP220</th><th>未補休轉加班費VP230</th><th>津貼--專科護理師VP457</th>
                                <th>發薪日</th><th>本薪FP000</th><th>專業加給FP010</th><th>院區加給FP100</th><th>身心障礙FP110</th><th>特別津貼FP120</th><th>計畫補助FP400</th><th>工作獎金VP000</th><th>專勤醫師費VP010</th><th>專勤獎金-科經營VP01E</th><th>專勤醫師費2VP040</th><th>夜班費VP100</th><th>不休假加班費VP110</th><th>加班費VP200</th><th>未休完特休假工資VP220</th><th>未補休轉加班費VP230</th><th>津貼--專科護理師VP457</th>
                                <th>發薪日</th><th>本薪FP000</th><th>專業加給FP010</th><th>院區加給FP100</th><th>身心障礙FP110</th><th>特別津貼FP120</th><th>計畫補助FP400</th><th>工作獎金VP000</th><th>專勤醫師費VP010</th><th>專勤獎金-科經營VP01E</th><th>專勤醫師費2VP040</th><th>夜班費VP100</th><th>不休假加班費VP110</th><th>加班費VP200</th><th>未休完特休假工資VP220</th><th>未補休轉加班費VP230</th><th>津貼--專科護理師VP457</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="pageLoadingOverlay">
    <div class="loader"></div>
</div>

</body>
</html>